package it.polito.waii.ticketingserver.product.services


import it.polito.waii.ticketingserver.exceptions.NoSalesException
import it.polito.waii.ticketingserver.exceptions.ProductNotFoundException
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.product.database.repositories.ProductRepository
import it.polito.waii.ticketingserver.product.dtos.ProductDTO
import it.polito.waii.ticketingserver.product.dtos.toDTO
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@LogInfo
class ProductServiceImpl(
    private val productRepository: ProductRepository,
): ProductService {
    override fun getAll(): List<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    override fun getProductsForCustomer(customerId: String): List<ProductDTO> {
        val products = productRepository.findProductsForCustomer(customerId)?.map { it.toDTO() }
        if(products.isNullOrEmpty()) {
            throw NoSalesException("No sales yet for customer $customerId")
        } else {
            return products
        }

    }
    override fun getProduct(ean: String): ProductDTO? {
        val dbProduct = productRepository.findByIdOrNull(ean)?.toDTO()
        if(dbProduct != null)
            return dbProduct
        else
            throw ProductNotFoundException("No product with ean $ean found.")
    }

}
