package com.openclassrooms.p8_vitesse.data.apiResponse

import com.openclassrooms.p8_vitesse.data.model.ExchangeRatesResultModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the API response for currency rates.
 *
 * This class models the structure of the response from the currency rates API.
 *
 * @property apiResponseDate The date when the rates were fetched from the API.
 * @property apiResponseRates A map of currency codes (as keys) and their conversion rates (as values) relative to a base currency.
 */
@JsonClass(generateAdapter = true)
data class ExchangeRatesApiResponse(
    @Json(name = "date") val apiResponseDate: String,
    @Json(name = "eur") val apiResponseRates: Map<String, Double>
) {

    /**
     * Converts the API response to a domain model.
     *
     * This function transforms the API response into a format more suitable for business logic,
     * represented by the [ExchangeRatesResultModel].
     *
     * @return The domain model [ExchangeRatesResultModel] that contains the date and the rates map.
     */
    fun toDomainModel(): ExchangeRatesResultModel {
        return ExchangeRatesResultModel(
            exchangeRatesDate = apiResponseDate,
            exchangeRates = apiResponseRates
        )
    }
}