package it.polito.waii.ticketingserver.product.services

import it.polito.waii.ticketingserver.product.dtos.ProductDTO

interface ProductService {
    // this specifies the difference between our DB representation and the representation
    // that we chose to show
    fun getAll(): List<ProductDTO>
    fun getProductsForCustomer(customerId: String): List<ProductDTO>
    fun getProduct(ean: String): ProductDTO?
}
