package com.dashmed.admin.networking

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class EmptyRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
)


@Keep
@JsonClass(generateAdapter = true)
data class LoginReq (
    @Json(name = "username") var username: String,
    @Json(name = "password") var password: String
)

data class LoginRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "UID") var uid: String?,
)



data class GetEmployeeRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "employees") var employees: List<Employee>?
)

@Keep
@JsonClass(generateAdapter = true)
data class AddEmployeeReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "username") var username: String,
    @Json(name = "password") var password: String,
    @Json(name = "name") var name: String,
    @Json(name = "phone") var phone: String
)

data class AddEmployeeRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "uid") var uid: String?
)

@Keep
@JsonClass(generateAdapter = true)
data class UpdateEmployeeReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "emp_uid") var emp_uid: String,
    @Json(name = "name") var name: String,
    @Json(name = "phone") var phone: String
)

@Keep
@JsonClass(generateAdapter = true)
data class RemoveEmployeeReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "emp_uid") var emp_uid: String,
)



data class GetAttendanceRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "employees") var employees: List<Entry>?
)

@Keep
@JsonClass(generateAdapter = true)
data class AddEntryReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "emp_ids") var emp_ids: List<String>,
    @Json(name = "date") var date: String
)



data class GetInventoryRes (
    @Json(name = "valid") var valid: Boolean,
    @Json(name = "message") var message: String?,
    @Json(name = "items") var items: List<InventoryItem>
)

data class GetMedicinesRes (
    @Json(name = "medicines") var medicines: List<Medicine>
)

@Keep
@JsonClass(generateAdapter = true)
data class AddItemReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "med_id") var med_id: String,
    @Json(name = "manufactured") var date: String,
    @Json(name = "quantity") var quantity: Int
)

@Keep
@JsonClass(generateAdapter = true)
data class UpdateItemReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "item_id") var item_id: String,
    @Json(name = "manufactured") var date: String,
    @Json(name = "quantity") var quantity: Int
)

@Keep
@JsonClass(generateAdapter = true)
data class RemoveItemReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "item_id") var item_id: String,
)


@Keep
@JsonClass(generateAdapter = true)
data class UpdatePassReq (
    @Json(name = "uid") var uid: String,
    @Json(name = "password") var new_password: String
)
