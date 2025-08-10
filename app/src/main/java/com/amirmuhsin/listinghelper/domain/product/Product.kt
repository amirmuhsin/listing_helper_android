package com.amirmuhsin.listinghelper.domain.product

import com.amirmuhsin.listinghelper.util.Time

data class Product(
    val id: Long,
    val serverId: Long,
    val sku: String,
    val isActive: Boolean,
    val name: String,
    val description: String,
    val shortDescription: String,
    val addedTime: String, // store as dd-MM-yyyy HH:mm:ss
    val changedTime: String, // store as dd-MM-yyyy HH:mm:ss

    val status: Status = Status.DRAFT,
    val totalImageCount: Int = 0
) {

    enum class Status {
        DRAFT, DONE, HAS_FAILURE
    }

    companion object {

        fun createEmpty(totalImageCount: Int = 0): Product {
            val nowIso = Time.nowUtcIso()

            return Product(
                id = 0,
                serverId = -1,
                sku = "",
                isActive = true,
                name = "New Product",
                description = "",
                shortDescription = "No Description",
                addedTime = nowIso,
                changedTime = nowIso,
                status = Status.DRAFT,
                totalImageCount = totalImageCount
            )
        }
    }
}