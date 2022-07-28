package com.dashmed.admin.networking

import com.squareup.moshi.Json

data class Employee (
    @Json(name = "UID") var uid: String,
    @Json(name = "name") var name: String,
    @Json(name = "phone") var phone: String,
)

data class Entry (
    @Json(name = "UID") var uid: String,
    @Json(name = "name") var name: String
)

data class InventoryItem (
    @Json(name = "uid") var uid: String,
    @Json(name = "name") var name: String,
    @Json(name = "description") var description: String,
    @Json(name = "composition") var composition: List<String>,
    @Json(name = "expiration") var expiration: Int,
    @Json(name = "cost") var cost: Float,
    @Json(name = "manufactured") var manufactured: String,
    @Json(name = "quantity") var quantity: Int
)

data class Medicine (
    @Json(name = "uid") var uid: String,
    @Json(name = "name") var name: String,
    @Json(name = "description") var description: String,
    @Json(name = "composition") var composition: List<String>,
    @Json(name = "expiration") var expiration: Int,
    @Json(name = "cost") var cost: Float
)