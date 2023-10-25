package it.polito.waii.ticketingserver.product
import io.micrometer.observation.annotation.Observed
import it.polito.waii.ticketingserver.product.dtos.ProductDTO
import it.polito.waii.ticketingserver.product.services.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@Observed
class ProductController(private val productService: ProductService) {
    @GetMapping("/API/products")
    fun getAll(): List<ProductDTO>{
        return productService.getAll()
    }
    @GetMapping("/API/products/{ean}")
    fun getProduct(@PathVariable ean: String): ProductDTO? {
        return productService.getProduct(ean)
    }

    @GetMapping("/API/products/customer/{customerId}")
    fun getProductsForCustomer(@PathVariable customerId: String): List<ProductDTO>? {
        return productService.getProductsForCustomer(customerId)
    }
}
