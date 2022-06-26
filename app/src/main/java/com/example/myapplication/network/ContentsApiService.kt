package com.example.myapplication.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://3je34r9ta3.execute-api.us-east-1.amazonaws.com"
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ContentsApiService {
    @GET("dev/test/")
    suspend fun getContents(@Query("city_name") city_name: String): Response
}

object ContentsApi {
    val retrofitService: ContentsApiService by lazy {
        retrofit.create(ContentsApiService::class.java)
    }
}