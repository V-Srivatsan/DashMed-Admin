package com.dashmed.admin.networking

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://dashmed-av.herokuapp.com/warehouse/api/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    ))
    .baseUrl(BASE_URL)
    .build()


interface APIService {

    @POST("login/")
    suspend fun login (@Body data: LoginReq): LoginRes



    @GET("get-employees/")
    suspend fun getEmployees(@Query("uid") uid: String): GetEmployeeRes

    @POST("add-employee/")
    suspend fun addEmployee(@Body data: AddEmployeeReq): AddEmployeeRes

    @PUT("update-employee/")
    suspend fun updateEmployee(@Body data: UpdateEmployeeReq): EmptyRes

    @HTTP(method = "DELETE", path = "remove-employee/", hasBody = true)
    suspend fun removeEmployee(@Body data: RemoveEmployeeReq): EmptyRes



    @GET("get-attendance/")
    suspend fun getAttendance(@Query("uid") uid: String, @Query("date") date: String): GetAttendanceRes

    @POST("add-entry/")
    suspend fun addEntry(@Body data: AddEntryReq): EmptyRes



    @GET("get-inventory/")
    suspend fun getInventory(@Query("uid") uid: String): GetInventoryRes

    @GET("get-medicines/")
    suspend fun getMedicines(@Query("query") query: String): GetMedicinesRes

    @POST("add-item/")
    suspend fun addItem(@Body data: AddItemReq): EmptyRes

    @PUT("update-item/")
    suspend fun updateItem(@Body data: UpdateItemReq): EmptyRes

    @HTTP(method = "DELETE", path = "remove-item/", hasBody = true)
    suspend fun removeItem(@Body data: RemoveItemReq): EmptyRes



    @GET("check-password/")
    suspend fun checkPassword(@Query("uid") uid: String, @Query("password") password: String): EmptyRes

    @PUT("update-password/")
    suspend fun updatePassword(@Body data: UpdatePassReq): EmptyRes
}

object API {
    val service: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}