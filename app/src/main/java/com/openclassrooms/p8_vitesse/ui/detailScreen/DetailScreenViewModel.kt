package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.data.repository.ExchangeRatesRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException

/**
 * ViewModel responsible for managing the state of the Detail Screen.
 *
 * This ViewModel interacts with the [CandidateRepository] and [ExchangeRatesRepository] to fetch
 * candidate and exchange rate data, and update the UI state accordingly.
 *
 * @property candidateRepository Repository used to fetch and manage candidate data.
 * @property exchangeRatesRepository Repository used to fetch exchange rate data.
 * @property context Application context used for accessing resources.
 * @property sharedPreferences SharedPreferences used for saving and retrieving user preferences.
 */
class DetailScreenViewModel(
    private val candidateRepository: CandidateRepository,
    private val exchangeRatesRepository: ExchangeRatesRepository,
    private val context: Application,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_CANDIDATE_IDENTIFIER = "candidateIdentifier"
    }

    /**
     * The candidate identifier accessed from SharedPreferences.
     * This identifier is used to keep track of the selected candidate.
     * It defaults to 0 if not found.
     */
    private var candidateIdentifier: Long =
        sharedPreferences.getLong(KEY_CANDIDATE_IDENTIFIER, 0) // Provide a default value

    // StateFlow to expose candidate data to the UI
    private val _candidateState = MutableStateFlow<Candidate?>(null)
    val candidateState: StateFlow<Candidate?> = _candidateState.asStateFlow()

    // Flow to emit exchange rate messages to the UI
    private var _exchangeRateMessage = MutableSharedFlow<String>()
    val exchangeRateMessage: SharedFlow<String> get() = _exchangeRateMessage.asSharedFlow()

    // Flow to emit converted salary values to the UI
    private var _convertedSalary = MutableSharedFlow<Double>()
    val convertedSalary: SharedFlow<Double> get() = _convertedSalary.asSharedFlow()

    init {
        // Load the candidate information when the ViewModel is created
        viewModelScope.launch(Dispatchers.IO) {
            _candidateState.value = candidateRepository.getCurrentCandidate(candidateIdentifier)
            if (_candidateState.value != null) {
                loadRateData(_candidateState.value!!.expectedSalary)
            } else {
                _exchangeRateMessage.emit("Candidate could not be loaded")
            }
        }
    }

    /**
     * Updates the given candidate in the DB.
     *
     * @param candidate The candidate object to update.
     */
    fun updateCandidate(candidate: Candidate) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.addOrUpdateCandidate(candidate)
        }
    }

    /**
     * Deletes the given candidate from the DB.
     *
     * @param candidate The candidate object to delete.
     */
    fun deleteCandidate(candidate: Candidate) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.deleteCandidate(candidate)
        }
    }

    /**
     * Loads exchange rate data for converting the candidate's expected salary.
     *
     * @param expectedSalary The candidate's expected salary in EUR.
     */
    private fun loadRateData(expectedSalary: Int?) {

        viewModelScope.launch {
            try {
                // Collect the flow from the exchange rates repository
                exchangeRatesRepository.fetchExchangeData().collect { rateInformationResult ->

                    // Extract data from the API result
                    val rateInformation = rateInformationResult.exchangeRatesInformation
                    val rateEurToGbp = rateInformation.exchangeRates["gbp"]
                    val statusCode = rateInformationResult.exchangeRatesStatusCode

                    // Handle the API status code and update the exchange rate data
                    handleApiStatusCode(
                        statusCode,
                        rateEurToGbp,
                        rateInformation.exchangeRatesDate,
                        expectedSalary
                    )
                }

            } catch (e: IOException) {
                // Handle network errors (e.g., no Internet connection)
                _exchangeRateMessage.emit("Connection error. Please check your Internet connection.")
            } catch (e: UnknownHostException) {
                _exchangeRateMessage.emit("No Internet connection. Please check your connection.")
            } catch (e: Exception) {
                // Handle other types of errors
                println("Exception: ${e.message}")
                _exchangeRateMessage.emit("An error occurred. Please try again.")
            }
        }
    }

    /**
     * Handles the exchange rate status code and computes the converted salary if the rate is found.
     *
     * @param statusCode The status code returned from the exchange rate API.
     * @param rateEurToGbp The exchange rate from EUR to GBP, or null if not found.
     * @param rateDate The date when the exchange rate was retrieved.
     * @param expectedSalary The candidate's expected salary in EUR.
     */
    private fun handleApiStatusCode(
        statusCode: Int,
        rateEurToGbp: Double?,
        rateDate: String,
        expectedSalary: Int?
    ) {
        viewModelScope.launch {
            when (statusCode) {

                200 -> if (rateEurToGbp != null && expectedSalary != null) {
                    _exchangeRateMessage.emit("Exchange rate EUR/GBP $rateEurToGbp ($rateDate)")
                    _convertedSalary.emit(rateEurToGbp * expectedSalary)
                } else {
                    _exchangeRateMessage.emit("Error : exchange rate not found.")
                }

                0 -> _exchangeRateMessage.emit("Error 0: API has not returned HTTP status code")
                1 -> _exchangeRateMessage.emit("Error 1: no response from API")
                2 -> _exchangeRateMessage.emit("Error 2: unexpected error")

                in 3..99 -> _exchangeRateMessage.emit("Error $statusCode: Unknown Error")
                in 100..199 -> _exchangeRateMessage.emit("Error $statusCode: Information Error")
                in 201..299 -> _exchangeRateMessage.emit("Error $statusCode: Success Error")
                in 300..399 -> _exchangeRateMessage.emit("Error $statusCode: Redirection Error")
                in 400..499 -> _exchangeRateMessage.emit("Error $statusCode: Client Error")
                in 500..599 -> _exchangeRateMessage.emit("Error $statusCode: Server Error")
                in 600..999 -> _exchangeRateMessage.emit("Error $statusCode: Unknown Error")
                else -> _exchangeRateMessage.emit("Unexpected Error. Please try again.")
            }
        }
    }
}