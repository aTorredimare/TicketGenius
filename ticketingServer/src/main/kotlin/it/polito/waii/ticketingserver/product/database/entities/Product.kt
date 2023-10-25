package it.polito.waii.ticketingserver.product.database.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

@Entity
@Table(name="products")
data class Product (
    @Id
    @Length(min=13, max=13)
    var ean: String = "",
    @NotBlank(message="Product name is mandatory")
    var name: String = "",
    @NotBlank(message="Brand is mandatory")
    var brand: String = ""
)