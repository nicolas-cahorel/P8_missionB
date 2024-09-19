package com.openclassrooms.p8_vitesse.data.model

/**
 * Domain model representing the result of a currency rates request.
 *
 * This model contains the HTTP status code of the request and the result of the currency rates information.
 *
 * @property exchangeRatesStatusCode The HTTP status code returned by the API.
 *         A 200 status code indicates a successful request.
 * @property exchangeRatesInformation The detailed information of the currency rates,
 *         including the date and the conversion rates for various currencies.
 */
data class ExchangeRatesInformationResultModel(
    val exchangeRatesStatusCode: Int,  // HTTP status code (e.g., 200 for success)
    val exchangeRatesInformation: ExchangeRatesResultModel  // Currency rates data
)

/**
 * Domain model representing currency rates data.
 *
 * This model holds the date of the rates and a map of currency codes and their corresponding conversion rates.
 *
 * @property exchangeRatesDate The date when the currency rates were fetched, in String format (e.g., "YYYY-MM-DD").
 * @property exchangeRates A map containing currency codes (as keys, e.g., "USD", "EUR") and their conversion rates
 *         (as values) relative to a base currency, usually set by the API.
 */
data class ExchangeRatesResultModel(
    val exchangeRatesDate: String,  // The date when the currency rates were fetched
    val exchangeRates: Map<String, Double>  // Currency code (key) and its conversion rate (value)
)