package com.openclassrooms.p8_vitesse

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.model.ExchangeRatesInformationResultModel
import com.openclassrooms.p8_vitesse.data.model.ExchangeRatesResultModel
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.data.repository.ExchangeRatesRepository
import com.openclassrooms.p8_vitesse.ui.detailScreen.DetailScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Test class for the [DetailScreenViewModel].
 *
 * This class tests the behavior of the [DetailScreenViewModel], including
 * scenarios where the exchange rates are successfully retrieved, when there is
 * no response from the API, or when there is no internet connection.
 */
@ExperimentalCoroutinesApi
class DetailScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockCandidateRepository: CandidateRepository

    @Mock
    private lateinit var mockExchangeRatesRepository: ExchangeRatesRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var viewModel: DetailScreenViewModel

    /**
     * Sets up the test environment before each test.
     *
     * This includes initializing the mocks, setting the test dispatcher as the
     * main dispatcher, and creating an instance of the [DetailScreenViewModel].
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.openMocks(this)

        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }

        viewModel = DetailScreenViewModel(
            mockCandidateRepository,
            mockExchangeRatesRepository,
            mockSharedPreferences,
            testDispatcher
        )
    }

    /**
     * Resets the main dispatcher to its original state after each test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests the [DetailScreenViewModel.loadRateData] method to verify that it correctly handles
     * the scenario where the exchange rates are successfully retrieved.
     *
     * The method is expected to correctly calculate and update the converted salary.
     */
    @Test
    fun loadRateData_success() = runTest {
        println("test loadRateData_success : ARRANGE")
        val expectedSalary = 50000
        val expectedRatesInformationResult = ExchangeRatesInformationResultModel(
            exchangeRatesStatusCode = 200,
            exchangeRatesInformation = ExchangeRatesResultModel(
                exchangeRatesDate = "10/10/2024",
                exchangeRates = mapOf("gbp" to 2.00)
            )
        )

        // Expectation for the result
        val expectedConvertedSalary = 100000.00
        var actualConvertedSalary = 0.00
//        val gbpRate = expectedRatesInformationResult.exchangeRatesInformation.exchangeRates["gbp"]
//        val expectedExchangeRateMessage =
//            "Exchange rate EUR/GBP $gbpRate (${expectedRatesInformationResult.exchangeRatesInformation.exchangeRatesDate})"
//        var actualExchangeRateMessage = ""

        // Mock repository to return the expected candidates
        `when`(mockExchangeRatesRepository.fetchExchangeData())
            .thenReturn(flow { emit(expectedRatesInformationResult) })

        println("test loadRateData_success : ACT")
        viewModel.viewModelScope.launch {

            // Call the method to be tested
            viewModel.loadRateData(expectedSalary)

            // Simulates time passed and the flow went to the end
            testDispatcher.scheduler.advanceUntilIdle()

            // Collect actual convertedSalary and exchangeRateMessage from the ViewModel
            actualConvertedSalary = viewModel.convertedSalary.first()
//            actualExchangeRateMessage = viewModel.exchangeRateMessage.first()
        }

        println("test loadRateData_success : ASSERT")
        println("test loadRateData_success : expectedConvertedSalary = $expectedConvertedSalary")
        println("test loadRateData_success : actualConvertedSalary = $actualConvertedSalary")
//        println("test loadRateData_noApiResponse : expectedExchangeRateMessage = $expectedExchangeRateMessage")
//        println("test loadRateData_noApiResponse : actualExchangeRateMessage = $actualExchangeRateMessage")
        try {
            assertEquals(expectedConvertedSalary, actualConvertedSalary, 0.00)
//            assertEquals(expectedExchangeRateMessage, actualExchangeRateMessage)
            println("test loadRateData_success : SUCCESS")
        } catch (e: AssertionError) {
            println("test loadRateData_success : FAIL")
            throw e
        }
    }

    /**
     * Tests the [DetailScreenViewModel.loadRateData] method to verify that it correctly handles
     * the scenario where no response is received from the API.
     *
     * The method is expected to update the exchange rate message to reflect the error status.
     */
    @Test
    fun loadRateData_noApiResponse() = runTest {
        println("test loadRateData_noApiResponse : ARRANGE")
        val expectedSalary = 50000
        val expectedRatesInformationResult = ExchangeRatesInformationResultModel(
            exchangeRatesStatusCode = 1,
            exchangeRatesInformation = ExchangeRatesResultModel(
                exchangeRatesDate = "",
                exchangeRates = emptyMap()
            )
        )

        // Expectation for the result
//        val expectedConvertedSalary = 100000.00
//        var actualConvertedSalary = 0.00
//        val gbpRate = expectedRatesInformationResult.exchangeRatesInformation.exchangeRates["gbp"]
        val expectedExchangeRateMessage =
            "Error 1: no response from API"
        var actualExchangeRateMessage = ""


        // Mock repository to return the expected candidates
        `when`(mockExchangeRatesRepository.fetchExchangeData())
            .thenReturn(flow { emit(expectedRatesInformationResult) })

        println("test loadRateData_noApiResponse : ACT")
        viewModel.viewModelScope.launch {

            // Call the method to be tested
            viewModel.loadRateData(expectedSalary)

            // Simulates time passed and the flow went to the end
            testDispatcher.scheduler.advanceUntilIdle()

            // Collect actual convertedSalary and exchangeRateMessage from the ViewModel
//            actualConvertedSalary = viewModel.convertedSalary.first()
            actualExchangeRateMessage = viewModel.exchangeRateMessage.first()

        }

        println("test loadRateData_noApiResponse : ASSERT")
//        println("test loadRateData_noApiResponse : expectedConvertedSalary = $expectedConvertedSalary")
//        println("test loadRateData_noApiResponse : actualConvertedSalary = $actualConvertedSalary")
        println("test loadRateData_noApiResponse : expectedExchangeRateMessage = $expectedExchangeRateMessage")
        println("test loadRateData_noApiResponse : actualExchangeRateMessage = $actualExchangeRateMessage")
        try {
//            assertEquals(expectedConvertedSalary, actualConvertedSalary, 0.00)
            assertEquals(expectedExchangeRateMessage, actualExchangeRateMessage)
            println("test loadRateData_noApiResponse : SUCCESS")
        } catch (e: AssertionError) {
            println("test loadRateData_noApiResponse : FAIL")
            throw e
        }
    }

    /**
     * Tests the [DetailScreenViewModel.loadRateData] method to verify that it correctly handles
     * the scenario where there is no internet connection.
     *
     * The method is expected to update the exchange rate message to reflect the error status.
     */
    @Test
    fun loadRateData_noInternet() = runTest {
        println("test loadRateData_noInternet : ARRANGE")
        val expectedSalary = 50000
        val expectedRatesInformationResult = ExchangeRatesInformationResultModel(
            exchangeRatesStatusCode = 3,
            exchangeRatesInformation = ExchangeRatesResultModel(
                exchangeRatesDate = "",
                exchangeRates = emptyMap()
            )
        )

        // Expectation for the result
//        val expectedConvertedSalary = 100000.00
//        var actualConvertedSalary = 0.00
//        val gbpRate = expectedRatesInformationResult.exchangeRatesInformation.exchangeRates["gbp"]
        val expectedExchangeRateMessage =
            "Error 3: no internet access"
        var actualExchangeRateMessage = ""

        // Mock repository to return the expected candidates
        `when`(mockExchangeRatesRepository.fetchExchangeData())
            .thenReturn(flow { emit(expectedRatesInformationResult) })

        println("test loadRateData_noInternet : ACT")
        viewModel.viewModelScope.launch {

            // Call the method to be tested
            viewModel.loadRateData(expectedSalary)

            // Simulates time passed and the flow went to the end
            testDispatcher.scheduler.advanceUntilIdle()

            // Collect actual convertedSalary and exchangeRateMessage from the ViewModel
//            actualConvertedSalary = viewModel.convertedSalary.first()
            actualExchangeRateMessage = viewModel.exchangeRateMessage.first()
        }

        println("test loadRateData_noInternet : ASSERT")
//        println("test loadRateData_noApiResponse : expectedConvertedSalary = $expectedConvertedSalary")
//        println("test loadRateData_noApiResponse : actualConvertedSalary = $actualConvertedSalary")
        println("test loadRateData_noInternet : expectedExchangeRateMessage = $expectedExchangeRateMessage")
        println("test loadRateData_noInternet : actualExchangeRateMessage = $actualExchangeRateMessage")
        try {
//            assertEquals(expectedConvertedSalary, actualConvertedSalary, 0.00)
            assertEquals(expectedExchangeRateMessage, actualExchangeRateMessage)
            println("test loadRateData_noInternet : SUCCESS")
        } catch (e: AssertionError) {
            println("test loadRateData_noInternet : FAIL")
            throw e
        }
    }
}