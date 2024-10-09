package com.openclassrooms.p8_vitesse

import android.content.SharedPreferences
import com.openclassrooms.p8_vitesse.data.model.ExchangeRatesInformationResultModel
import com.openclassrooms.p8_vitesse.data.model.ExchangeRatesResultModel
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.data.repository.ExchangeRatesRepository
import com.openclassrooms.p8_vitesse.ui.detailScreen.DetailScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
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
 * scenarios where the candidates are successfully retrieved, the list is empty,
 * or an error occurs.
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
            mockSharedPreferences
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
     * the scenario where candidates are successfully retrieved.
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
        val gbpRate = expectedRatesInformationResult.exchangeRatesInformation.exchangeRates["gbp"]
        val expectedExchangeRateMessage =
            "Exchange rate EUR/GBP $gbpRate (${expectedRatesInformationResult.exchangeRatesInformation.exchangeRatesDate})"
        val expectedConvertedSalary = gbpRate?.let { it * expectedSalary } ?: 0.0

        // Mock repository to return the expected candidates
        `when`(mockExchangeRatesRepository.fetchExchangeData())
            .thenReturn(flow { emit(expectedRatesInformationResult) })

        // Call the method to be tested
        println("test loadRateData_success : ACT")
        viewModel.loadRateData(expectedSalary)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()

        // Collect actual convertedSalary and exchangeRateMessage from the ViewModel
        val actualConvertedSalary = viewModel.convertedSalary.firstOrNull() ?: 0.00
        val actualExchangeRateMessage = viewModel.exchangeRateMessage.firstOrNull()

        // Assert that convertedSalary and exchangeRateMessage are as expected
        println("test loadRateData_success : ASSERT")
        try {
            assertEquals(expectedConvertedSalary, actualConvertedSalary, 0.00)
            assertEquals(expectedExchangeRateMessage, actualExchangeRateMessage)
            println("test loadRateData_success : SUCCESS")
        } catch (e: AssertionError) {
            println("test loadRateData_success : FAIL")
            throw e
        }
    }
}