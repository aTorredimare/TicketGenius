package it.polito.waii.ticketingserver
/*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import dasniko.testcontainers.keycloak.KeycloakContainer
import it.polito.waii.ticketingserver.product.database.entities.Product
import it.polito.waii.ticketingserver.profile.database.entities.Profile
import it.polito.waii.ticketingserver.ticket.database.entities.Employee
import it.polito.waii.ticketingserver.ticket.database.entities.Sale
import it.polito.waii.ticketingserver.ticket.database.entities.Ticket
import it.polito.waii.ticketingserver.ticket.database.entities.TicketStatusHistory
import it.polito.waii.ticketingserver.ticket.database.repositories.EmployeeRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.SaleRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketStatusHistoryRepository
import it.polito.waii.ticketingserver.ticket.dtos.TicketDTO
import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.EPriority
import it.polito.waii.ticketingserver.ticket.models.ERole
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import it.polito.waii.ticketingserver.product.database.repositories.ProductRepository
import it.polito.waii.ticketingserver.profile.database.repositories.ProfileRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.com.google.common.reflect.TypeToken
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class TicketingTest {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

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
    @BeforeEach
    fun setUp(){
        val profile1 = Profile("axsasdawe", "john.smith@mail.com", "John", "Smith", LocalDate.of(1980, 12, 11), "3409827627" )
        val profile2 = Profile("asdafgees","sarah.aniston@mail.com", "Sarah", "Aniston", LocalDate.of(1970, 10, 2), "3412827437" )
        profileRepository.save(profile1)
        profileRepository.save(profile2)

        val expert = Employee("23123nkdw", "Carl", "Jobs", "carl.jobs@company.com", LocalDate.of(1970, 3, 14), "3200743450", ERole.EXPERT, EExpertiseArea.TECHNICAL_SUPPORT )
        val expert2 = Employee("asd21t21", "William", "Terry", "william.terry@company.com", LocalDate.of(1988, 8, 9), "3200749200", ERole.EXPERT, EExpertiseArea.PRODUCT_DELIVERY )
        employeesRepository.save(expert)
        employeesRepository.save(expert2)

        val product1 = Product("ae12bcf91ygs3", "Sony Monitor 34 inches", "Sony")
        productRepository.save(product1)
        val product2 = Product("al92pcf91ygrt", "Sony Phone model XYZ", "Sony")
        productRepository.save(product2)
        val product3 = Product("aj22bcfggygs0", "FreeBuds Pro2", "Huawei")
        productRepository.save(product3)

        val sale1 = Sale(1,
            Timestamp.valueOf(LocalDateTime.of(2023, 5, 10,9,30,20)),
            Timestamp.valueOf(LocalDateTime.of(2024, 4, 12,10,10,15)),
            120.10,
            profile1.email,
            product1.ean,
            mutableListOf()
        )
        val sale2 = Sale(2,
            Timestamp.valueOf(LocalDateTime.of(2023, 4, 2,17,20,20)),
            Timestamp.valueOf(LocalDateTime.of(2024, 5, 10,15,10,15)),
            89.90,
            profile2.email,
            product2.ean,
            mutableListOf()
        )
        val sale3 = Sale(3,
            Timestamp.valueOf(LocalDateTime.of(2023, 1, 31,19,15,20)),
            Timestamp.valueOf(LocalDateTime.of(2024, 11, 5,14,56,49)),
            250.0,
            profile2.email,
            product3.ean,
            mutableListOf()
        )

        val sale4 = Sale(4,
            Timestamp.valueOf(LocalDateTime.of(2023, 11, 10,19,15,20)),
            Timestamp.valueOf(LocalDateTime.of(2024, 12, 5,14,56,49)),
            250.0,
            profile1.email,
            product2.ean,
            mutableListOf()
        )
        saleRepository.save(sale1)
        saleRepository.save(sale2)
        saleRepository.save(sale3)
        saleRepository.save(sale4)

        val ticket1 = Ticket(1, null, "Broken glass", null, sale1)
        val ticket2 = Ticket(2, EPriority.HIGH, "Broken SIM", expert, sale2)
        val ticket3 = Ticket(3, EPriority.LOW, "Broken speakers", expert, sale3)
        ticketRepository.save(ticket1)
        ticketRepository.save(ticket2)
        ticketRepository.save(ticket3)

        val history1 = TicketStatusHistory(1, ETicketStatus.OPEN,
            Timestamp.valueOf(LocalDateTime.of(2023, 2, 15, 18, 10, 30)), ticket1)
        val history2 = TicketStatusHistory(2, ETicketStatus.IN_PROGRESS,
            Timestamp.valueOf(LocalDateTime.of(2023, 2, 16, 10, 20, 30)), ticket1)
        val history3 = TicketStatusHistory(3, ETicketStatus.OPEN,
            Timestamp.valueOf(LocalDateTime.of(2023, 5, 10, 14, 15, 30)), ticket2)
        ticketHistoryRepository.save(history1)
        ticketHistoryRepository.save(history2)
        ticketHistoryRepository.save(history3)

    }
    @AfterEach
    fun cleanUp() {
        // clean up the environment
        ticketHistoryRepository.deleteAll()
        ticketRepository.deleteAll()
        saleRepository.deleteAll()

        profileRepository.deleteAll()
        productRepository.deleteAll()
        employeesRepository.deleteAll()
    }
    @Test
    @DirtiesContext
    fun `test get an existing ticket`() {
        val uri = "http://localhost:$port/API/tickets/id/1"
        val result = restTemplate.getForEntity(uri, String::class.java)

        val objectMapper = ObjectMapper()

        val jsonNode = objectMapper.readTree(result.body)
        val ticketID = jsonNode.get("ticketId").asLong()

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(1, ticketID)
    }

    @Test
    @DirtiesContext
    fun `test get a non existing ticket`() {
        val ticketID = 999
        val uri = "http://localhost:$port/API/tickets/id/$ticketID"
        val result = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
        Assertions.assertEquals(result.body, "No ticket with id $ticketID found.")
    }

    @Test
    @DirtiesContext
    fun `test get all the tickets assigned to expert 1`(){
        val expertId: String = "23123nkdw"
        val uri = "http://localhost:$port/API/tickets/expert/$expertId"

        val response = restTemplate.getForEntity(uri, String::class.java)


        val gson = Gson()
        val ticketListType = object : TypeToken<List<TicketDTO>>() {}.type
        val ticketList: List<TicketDTO> = gson.fromJson(response.body, ticketListType)

        print(ticketList)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(2, ticketList.size)
    }

    @Test
    @DirtiesContext
    fun `test get all the tickets assigned to expert with any tickets`(){
        val expertId: Long = 2
        val uri = "http://localhost:$port/API/tickets/expert/$expertId"

        val response = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        Assertions.assertEquals("No ticket assigned to expert $expertId found.", response.body)
    }


    //test commented just because security chain needs to be adjusted
    //@Test
    //@DirtiesContext
    /*fun `test assign ticket to expert retrieving expert from employee repository`(){
        val uri = "http://localhost:$port/API/ticket/assign/1"
        val response = restTemplate.getForEntity("http://localhost:$port/API/employees/id/23123nkdw", String::class.java)

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode) //now the api is protected
        Assertions.assertEquals(HttpStatus.OK, response.statusCode) //this passes if we leave the api unprotected

        val objectMapper = ObjectMapper()

        var jsonNode = objectMapper.readTree(response.body)
        val expertID = jsonNode.get("employeeId").asText()
        Assertions.assertEquals("23123nkdw", expertID)

        val requestBody = TicketDTO(1, EPriority.HIGH, "assign priority", expertID, -1)
        val result = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity(requestBody), String::class.java)
        print("\nPUT return: $result\n")

        val ticketResponse = restTemplate.getForEntity("http://localhost:$port/API/tickets/id/1", String::class.java)
        print("\nGET ticket: $ticketResponse")
        Assertions.assertEquals(HttpStatus.OK, ticketResponse.statusCode)
        jsonNode = objectMapper.readTree(ticketResponse.body)

        val newPriority = jsonNode.get("priority").asText()
        val newExpertID = jsonNode.get("expertId").asText()
        Assertions.assertEquals(newExpertID, expertID)
        Assertions.assertEquals(newPriority, EPriority.HIGH.toString())
    }*/

    @Test
    @DirtiesContext
    fun `assign a ticket to a non existent expert`(){
        val expertID = "sjdnandpas"
        val uri = "http://localhost:$port/API/ticket/assign/1"

        val requestBody = TicketDTO(1, EPriority.HIGH, "assign priority", expertID, -1)
        val result = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity(requestBody), String::class.java)
        print("\nPUT return: $result\n")

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)

        /*Assertions.assertEquals(HttpStatus.NOT_FOUND,result.statusCode)
        Assertions.assertEquals("No employee with id $expertID found.", result.body)*/
    }

    @Test
    @DirtiesContext
    fun `assign an already assigned ticket to an expert`() {
        val ticketid: Long = 2
        val expertID = "23123nkdw"
        val uri = "http://localhost:$port/API/ticket/assign/$ticketid"

        val requestBody = TicketDTO(ticketid, EPriority.HIGH, "assign priority", expertID, -1)
        val result = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity(requestBody), String::class.java)
        print("\nPUT return: $result\n")

        /*Assertions.assertEquals(HttpStatus.BAD_REQUEST,result.statusCode)
        Assertions.assertEquals("Ticket [$ticketid] already has an expert assigned: expert $expertID", result.body)*/
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }

    @Test
    @DirtiesContext
    fun `assign a non existing ticket to an expert`() {
        val ticketid: Long = 999
        val expert = "23123nkdw"
        val uri = "http://localhost:$port/API/ticket/assign/$ticketid"

        val requestBody = TicketDTO(ticketid, EPriority.HIGH, "assign priority", expert, -1)
        val result = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity(requestBody), String::class.java)
        print("\nPUT return: $result\n")

        /*Assertions.assertEquals(HttpStatus.NOT_FOUND,result.statusCode)
        Assertions.assertEquals("No ticket with id $ticketid found.", result.body)*/
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
    }

    @Test
    @DirtiesContext
    fun `test POST new ticket`() {
        val uri = "http://localhost:$port/API/ticket"

        val requestBody = TicketDTO(0, null, "Battery is slow when charging", null, 4)

        val result = restTemplate.exchange(uri, HttpMethod.POST, HttpEntity(requestBody), String::class.java)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)

        /*print("POST result: $result")
        val gson = Gson()
        val ticketDTO = gson.fromJson(result.body, TicketDTO::class.java)
        print("ticketDTO $ticketDTO")
        val newTicketID = ticketDTO.ticketId

        val newTicketResponse = restTemplate.getForEntity("http://localhost:$port/API/tickets/id/$newTicketID", String::class.java)
        print("GET new ticket $newTicketResponse")
        val newTicketDTO = gson.fromJson(newTicketResponse.body, TicketDTO::class.java)

        requestBody.ticketId = newTicketID
        Assertions.assertEquals(requestBody, newTicketDTO)

        //val responseHistory = restTemplate.getForEntity()*/



    }



    @Test
    @DirtiesContext
    fun `test change ticket status to a not existent ticket`() {
        val ticketId = 999
        val uri = "http://localhost:$port/API/ticket/changestatus/$ticketId"

        val requestBody = ETicketStatus.IN_PROGRESS

        val result = restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity(requestBody), String::class.java)
        print("PUT result: $result")


        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.statusCode)
        //Assertions.assertEquals("Ticket with id $ticketId not found.",result.body)
    }
    @Test
    @DirtiesContext
    fun `test ticket 1 history`(){
        val ticketID = 1
        val uri = "http://localhost:$port/API/tickethistory/$ticketID"

        val response = restTemplate.getForEntity(uri, String::class.java)
        print("GET result: $response")

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        /*Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertNotNull(response.body)

        val json = Json { serializersModule = SerializersModule { contextual(Timestamp::class, TimestampSerializer) } }
        val hist: List<TestTicketStatusHistoryDTO> = json.decodeFromString(response.body.toString())
        Assertions.assertEquals(2, hist.size)

        println(hist)*/
    }

    @Test
    @DirtiesContext
    fun `get ticket 1 current status`() {
        val ticketID = 1
        val uri = "http://localhost:$port/API/tickethistory/currentstatus/$ticketID"

        val response = restTemplate.getForEntity(uri, String::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)

        val gson = Gson()
        val status = gson.fromJson(response.body, ETicketStatus::class.java)

        Assertions.assertEquals(ETicketStatus.IN_PROGRESS, status)

    }

    @Test
    @DirtiesContext
    fun `get history of a non existent ticket`() {
        val ticketID = 999
        val uri = "http://localhost:$port/API/tickethistory/$ticketID"

        val response = restTemplate.getForEntity(uri, String::class.java)

        //Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

}
*/
