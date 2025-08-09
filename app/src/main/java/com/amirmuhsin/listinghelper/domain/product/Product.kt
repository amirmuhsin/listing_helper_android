package com.amirmuhsin.listinghelper.domain.product

data class Product(
    val id: Long,
    val sku: String,
    val isActive: Boolean,
    val name: String,
    val description: String,
    val shortDescription: String,
    val addedTime: String, // store as dd-MM-yyyy HH:mm:ss
    val changedTime: String // store as dd-MM-yyyy HH:mm:ss
) {

    companion object {

        fun createEmpty(): Product {
            return Product(
                id = 0,
                sku = "",
                isActive = true,
                name = "New Product",
                description = "",
                shortDescription = "",
                addedTime = "",
                changedTime = ""
            )
        }
    }
}