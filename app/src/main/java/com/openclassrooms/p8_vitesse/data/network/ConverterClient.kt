package com.openclassrooms.p8_vitesse.data.network

import com.openclassrooms.p8_vitesse.data.apiResponse.ConverterApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for defining user account related API endpoints.
 */
interface ConverterClient {

    /**
     * Makes a GET request to retrieve user account information based on the provided ID.
     *
     * @param userId The identifier associated with the user's account.
     * @return A Retrofit [Response] wrapping an [AccountsApiResponse].
     */
    @GET("/currencies/eur.json")
    suspend fun getEuroRate(): retrofit2.Response<ConverterApiResponse>
}