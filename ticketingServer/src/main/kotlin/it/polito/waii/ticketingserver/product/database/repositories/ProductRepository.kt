package it.polito.waii.ticketingserver.product.database.repositories

import it.polito.waii.ticketingserver.product.database.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, String>{
    @Query(
        value = """
            SELECT * FROM products
            WHERE ean IN (
                SELECT ean FROM sale
                where customer_id = :customerId
            )
        """,
        nativeQuery=true
    )
    fun findProductsForCustomer(
        @Param("customerId") customerId: String
    ): List<Product>?

}
