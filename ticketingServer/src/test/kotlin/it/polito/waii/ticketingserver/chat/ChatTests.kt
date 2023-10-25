package it.polito.waii.ticketingserver

import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.waii.ticketingserver.chat.database.repositories.AttachmentRepository
import it.polito.waii.ticketingserver.chat.database.repositories.ChatRepository
import it.polito.waii.ticketingserver.chat.dtos.MessageDTO
import it.polito.waii.ticketingserver.product.database.entities.Product
import it.polito.waii.ticketingserver.product.database.repositories.ProductRepository
import it.polito.waii.ticketingserver.profile.database.entities.Profile
import it.polito.waii.ticketingserver.profile.database.repositories.ProfileRepository
import it.polito.waii.ticketingserver.ticket.database.entities.Employee
import it.polito.waii.ticketingserver.ticket.database.entities.Sale
import it.polito.waii.ticketingserver.ticket.database.entities.Ticket
import it.polito.waii.ticketingserver.ticket.database.entities.TicketStatusHistory
import it.polito.waii.ticketingserver.ticket.database.repositories.EmployeeRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.SaleRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketStatusHistoryRepository
import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.EPriority
import it.polito.waii.ticketingserver.ticket.models.ERole
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import org.junit.jupiter.api.Test

import org.aspectj.lang.annotation.Before
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.keycloak.admin.client.Keycloak
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.util.LinkedMultiValueMap
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

data class LoginDTO(
    val username: String,
    val password: String
)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatServiceImplTest  {
    private val prefixURL = "/API/ticket"

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @Container
        val keycloak: KeycloakContainer = KeycloakContainer()
            .withRealmImportFile("keycloak/realm-export.json")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            val keycloakBaseUrl = keycloak.authServerUrl
            registry.add("keycloakBaseUrl") { keycloakBaseUrl }
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") { "${keycloakBaseUrl}/realms/ticket_management_system" }
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri") {"${keycloakBaseUrl}/realms/ticket_management_system/protocol/openid-connect/certs"}
        }
    }
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var attachmentRepository: AttachmentRepository

    @Autowired
    lateinit var chatRepository: ChatRepository

    @Autowired
    lateinit var employeesRepository: EmployeeRepository

    @Autowired
    lateinit var profileRepository: ProfileRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var saleRepository: SaleRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var ticketHistoryRepository: TicketStatusHistoryRepository

    private lateinit var customer: Profile
    private lateinit var otherCustomer: Profile
    private lateinit var expert: Employee
    private lateinit var otherExpert: Employee
    private lateinit var manager: Employee
    private lateinit var sale: Sale
    private lateinit var otherSale: Sale
    private lateinit var vendor: Employee

    private lateinit var ticket: Ticket
    private lateinit var otherTicket: Ticket


    private lateinit var customerToken: String
    private lateinit var expertToken: String
    private lateinit var managerToken: String



    @BeforeEach
    fun populateDatabase(){
        if(!ChatServiceImplTest.keycloak.isRunning){
            ChatServiceImplTest.keycloak.start()
        }
        println("----populating database------")
        customer = Profile("axsasdawe", "john.smith@mail.com", "John", "Smith", LocalDate.of(1980, 12, 11), "3409827627" )
        otherCustomer = Profile("asdafgees","sarah.aniston@mail.com", "Sarah", "Aniston", LocalDate.of(1970, 10, 2), "3412827437" )
        profileRepository.save(customer)
        profileRepository.save(otherCustomer)

        manager = Employee("",
            "Steve",
            "Jobs",
            "steve.jobs@company.com",
            LocalDate.of(1968, 10, 24),
            "3248764182",
            ERole.MANAGER,
            EExpertiseArea.NULL
        )

        expert = Employee("23123nkdw", "Carl", "Jobs", "carl.jobs@company.com",
            LocalDate.of(1970, 3, 14), "3200743450", ERole.EXPERT, EExpertiseArea.TECHNICAL_SUPPORT )
        otherExpert = Employee("asd21t21", "William", "Terry", "william.terry@company.com", LocalDate.of(1988, 8, 9), "3200749200", ERole.EXPERT, EExpertiseArea.PRODUCT_DELIVERY )
        employeesRepository.save(expert)
        employeesRepository.save(otherExpert)

        val product1 = Product("ae12bcf91ygs3", "Sony Monitor 34 inches", "Sony")
        productRepository.save(product1)
        val product2 = Product("al92pcf91ygrt", "Sony Phone model XYZ", "Sony")
        productRepository.save(product2)
        val product3 = Product("aj22bcfggygs0", "FreeBuds Pro2", "Huawei")
        productRepository.save(product3)

        sale = Sale(1,
            Timestamp.valueOf(LocalDateTime.of(2023, 5, 10,9,30,20)),
            Timestamp.valueOf(LocalDateTime.of(2024, 4, 12,10,10,15)),
            120.10,
            customer.email,
            product1.ean,
            mutableListOf()
        )
        otherSale = Sale(2,
            Timestamp.valueOf(LocalDateTime.of(2023, 4, 2,17,20,20)),
            Timestamp.valueOf(LocalDateTime.of(2024, 5, 10,15,10,15)),
            89.90,
            otherCustomer.email,
            product2.ean,
            mutableListOf()
        )


        saleRepository.save(sale)
        saleRepository.save(otherSale)


        ticket = Ticket(1, null, "Broken glass", null, sale)
        otherTicket = Ticket(2, null, "Broken SIM", null, otherSale)
        ticketRepository.save(ticket)
        ticketRepository.save(otherTicket)

        val history1 = TicketStatusHistory(1, ETicketStatus.OPEN,
            Timestamp.valueOf(LocalDateTime.of(2023, 2, 15, 18, 10, 30)), ticket)
        val history2 = TicketStatusHistory(2, ETicketStatus.OPEN,
            Timestamp.valueOf(LocalDateTime.of(2023, 2, 16, 10, 20, 30)), ticket)

        ticketHistoryRepository.save(history1)
        ticketHistoryRepository.save(history2)

        println("---------------------------------")
    }
    @AfterEach
    fun destroyDatabase(){
        println("----destroying database------")
        attachmentRepository.deleteAll()
        chatRepository.deleteAll()
        ticketHistoryRepository.deleteAll()
        ticketRepository.deleteAll()
        productRepository.deleteAll()
        employeesRepository.deleteAll()
        profileRepository.deleteAll()
        println("---------------------------------")

    }
    @BeforeEach
    fun refreshCustomerToken(){
        val loginDTO = LoginDTO(customer.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        customerToken = response.body!!
    }

    @BeforeEach
    fun refreshExpertToken(){
        val loginDTO = LoginDTO(expert.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        expertToken = response.body!!
    }

    @BeforeEach
    fun refreshManagerToken(){
        val loginDTO = LoginDTO(manager.email, "password")
        val body = HttpEntity(loginDTO)
        val response = restTemplate.postForEntity<String>("/API/login", body, HttpMethod.POST )
        managerToken = response.body!!
    }

    fun assignTicket(ticketId: Long, expert: Employee, by: Employee) {
        val ticket = ticketRepository.findByIdOrNull(ticketId)!!
        ticket.expert = expert
        ticket.priority = EPriority.HIGH

        val newTicketStatus: TicketStatusHistory =
            TicketStatusHistory(null, ETicketStatus.IN_PROGRESS,
                Timestamp.from(Instant.now()), ticket)
        ticket.statusHistory.add(
            ticketHistoryRepository.save(newTicketStatus)
        )
        ticketRepository.save(ticket)
    }

    @Test
    fun `get last 10 messages for a ticket as a Customer`() {
        val ticketId = ticket.ticketId
        val messageBody = "This is a test message"
        val headers = HttpHeaders()
        headers.setBearerAuth(customerToken)
        val request = RequestEntity.post("$prefixURL/newmessage")
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(LinkedMultiValueMap<String, Any>().apply {
                add("message", messageBody)
            })
        val response = restTemplate.exchange(request, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.headers.location.toString().isNotBlank())
        val responseGet: ResponseEntity<Set<MessageDTO>> =
            restTemplate.exchange("$prefixURL/$ticketId/chat",
                HttpMethod.GET,
                HttpEntity(null, headers))
        assert(responseGet.statusCode == HttpStatus.OK)
        assert(responseGet.body!!.size == 1)
        assert(responseGet.body!!.elementAt(0).author == customer.email)
        assert(responseGet.body!!.elementAt(0).content == messageBody)
    }
}
