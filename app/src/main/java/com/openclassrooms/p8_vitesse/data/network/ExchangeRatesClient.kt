package com.openclassrooms.p8_vitesse.data.network

import com.openclassrooms.p8_vitesse.data.apiResponse.ExchangeRatesApiResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit interface for defining currency rate related API endpoints.
 *
 * This interface defines the endpoint used to retrieve currency exchange rates from a remote API.
 */
interface ExchangeRatesClient {

    /**
     * Fetches the latest currency exchange rates for EUR against multiple currencies.
     *
     * This function makes a GET request to retrieve the conversion rates of the Euro (EUR)
     * relative to other currencies. The endpoint responds with the rates as JSON data.
     *
     * @return A [Response] object containing an [ExchangeRatesApiResponse] which holds
     *         the exchange rates and the date they were fetched.
     */
    @GET("currencies/eur.json")
    suspend fun getEuroRates(): Response<ExchangeRatesApiResponse>
}