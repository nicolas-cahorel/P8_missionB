package com.openclassrooms.p8_vitesse.data.model

/**
 * Domain model representing the result of a user's account information.
 *
 * @property accountStatusCode The HTTP status code of the user account request.
 * @property accounts A list of user account results.
 */
data class CurrencyConversionResultModel(
    val conversionDate: String,
    val conversionRates: Map<String, Double>
)