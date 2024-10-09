package com.openclassrooms.p8_vitesse.data.repository

import android.util.Log
import com.openclassrooms.p8_vitesse.data.model.ExchangeRatesInformationResultModel
import com.openclassrooms.p8_vitesse.data.model.ExchangeRatesResultModel
import com.openclassrooms.p8_vitesse.data.network.ExchangeRatesClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Repository responsible for managing currency rate-related operations.
 * This class interacts with [ExchangeRatesClient] to perform API requests for currency rates.
 *
 * @property dataService The [ExchangeRatesClient] used for API requests.
 */
class ExchangeRatesRepository(private val dataService: ExchangeRatesClient) {

    /**
     * Fetches currency rate data for the Euro via the API.
     *
     * @return A [Flow] emitting [ExchangeRatesInformationResultModel] based on the API response.
     */
    fun fetchExchangeData(): Flow<ExchangeRatesInformationResultModel> = flow {

        // Make a request to retrieve the Euro exchange rates
        val apiResponse = dataService.getEuroRates()

        // Extract the status code and the response body
        val apiStatusCode = apiResponse.code()
        val apiResponseBody = apiResponse.body()

        // Create the ExchangeRatesInformationResultModel based on the API response
        val exchangeRatesInformationResultModel = when {

            // Case 1: Both the response body and status code are not null
            apiResponseBody != null && apiStatusCode != null -> {
                ExchangeRatesInformationResultModel(
                    apiStatusCode,
                    ExchangeRatesResultModel(
                        apiResponseBody.apiResponseDate,
                        apiResponseBody.apiResponseRates
                    )
                )
            }

            // Case 2: Response body is null, but the status code is available
            apiResponseBody == null && apiStatusCode != null -> {
                ExchangeRatesInformationResultModel(
                    apiStatusCode,
                    ExchangeRatesResultModel(
                        "",
                        emptyMap() // Empty map in case of null response body
                    )
                )
            }

            // Case 3: Response body is available, but the status code is null
            apiResponseBody != null && apiStatusCode == null -> {
                ExchangeRatesInformationResultModel(
                    exchangeRatesStatusCode = 0,
                    ExchangeRatesResultModel(
                        apiResponseBody.apiResponseDate,
                        apiResponseBody.apiResponseRates
                    )

                )
            }

            // Case 4: Both response body and status code are null
            apiResponseBody == null && apiStatusCode == null -> {
                ExchangeRatesInformationResultModel(
                    exchangeRatesStatusCode = 1,
                    ExchangeRatesResultModel(
                        "",
                        emptyMap() // Empty map in case of null response body
                    )
                )
            }

            // Fallback case to handle unexpected scenarios
            else -> {
                ExchangeRatesInformationResultModel(
                    exchangeRatesStatusCode = 2,
                    ExchangeRatesResultModel(
                        "",
                        emptyMap() // Empty map in case of null response body
                    )
                )
            }
        }

        // Emit the result model through the flow
        emit(exchangeRatesInformationResultModel)

    }.catch { error ->
        // Handle errors that may occur during the execution of the flow
        Log.e("ExchangeRatesRepository", "Error fetching exchange rates: ${error.message}")
        // Emit a specific result model for network errors
        emit(
            ExchangeRatesInformationResultModel(
                exchangeRatesStatusCode = 3, // Custom error code for network issues
                ExchangeRatesResultModel(
                    "", emptyMap()
                )
            )
        )
    }
}