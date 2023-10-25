package it.polito.waii.ticketingserver.product.dtos

import it.polito.waii.ticketingserver.product.database.entities.Product

data class ProductDTO(
    val ean: String,
    val name: String,
    val brand: String
)

fun Product.toDTO(): ProductDTO {
    return ProductDTO(ean, name, brand)
}