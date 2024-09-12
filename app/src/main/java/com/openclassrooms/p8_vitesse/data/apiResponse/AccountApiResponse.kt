package com.openclassrooms.p8_vitesse.data.apiResponse

import com.openclassrooms.p8_vitesse.data.model.CurrencyConversionResultModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the API response for a user accounts request.
 *
 * @property apiResponseBody The list of user account API responses.
 */
@JsonClass(generateAdapter = true)
data class ConverterApiResponse(
    @Json(name = "date") val apiResponseDate: String,
    @Json(name = "rates") val apiResponseRates: Map<String, Double>
) {
    /**
     * Converts the API response to a domain model.
     *
     * @return The domain model representation of the user account.
     */
    fun toDomainModel(): CurrencyConversionResultModel {
        return CurrencyConversionResultModel(
            conversionDate = apiResponseDate,
            conversionRates = apiResponseRates
        )
    }
}