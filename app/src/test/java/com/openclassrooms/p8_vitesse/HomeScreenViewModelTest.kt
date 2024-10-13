package com.openclassrooms.p8_vitesse

import android.content.SharedPreferences
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenState
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Test class for the [HomeScreenViewModel].
 *
 * This class tests the behavior of the [HomeScreenViewModel], including
 * scenarios where the candidates are successfully retrieved, the list is empty,
 * or an error occurs.
 */
@ExperimentalCoroutinesApi
class HomeScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockCandidateRepository: CandidateRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var viewModel: HomeScreenViewModel

    /**
     * Sets up the test environment before each test.
     *
     * This includes initializing the mocks, setting the test dispatcher as the
     * main dispatcher, and creating an instance of the [HomeScreenViewModel].
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.openMocks(this)

        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }

        viewModel = HomeScreenViewModel(mockCandidateRepository, mockSharedPreferences)
    }

    /**
     * Resets the main dispatcher to its original state after each test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Tests the [HomeScreenViewModel.fetchCandidates] method to verify that it correctly handles
     * the scenario where candidates are successfully retrieved.
     *
     * The method is expected to update the [HomeScreenState] to [HomeScreenState.DisplayCandidates]
     * with the list of retrieved candidates.
     */
    @Test
    fun fetchCandidates_success() = runTest {
        println("test fetchCandidates_success : ARRANGE")
        val candidates = listOf(
            Candidate(
                id = 1,
                photo = ByteArray(0),
                firstName = "John",
                lastName = "Doe",
                phoneNumber = "1234567890",
                emailAddress = "john.doe@example.com",
                dateOfBirthStr = LocalDate.of(1985, 5, 15).atStartOfDay(ZoneOffset.UTC).toInstant()
                    .toEpochMilli(),
                expectedSalary = 50000,
                informationNote = "TEST : candidate 1",
                isFavorite = false
            ),

            Candidate(
                id = 2,
                photo = ByteArray(0),
                firstName = "Jim",
                lastName = "Nastik",
                phoneNumber = "1234567891",
                emailAddress = "jim.nastik@example.com",
                dateOfBirthStr = LocalDate.of(1980, 4, 20).atStartOfDay(ZoneOffset.UTC).toInstant()
                    .toEpochMilli(),
                expectedSalary = 45000,
                informationNote = "TEST : candidate 2",
                isFavorite = false
            )
        )
        // Expectation for the home screen state
        val expectedHomeScreenState = HomeScreenState.DisplayCandidates(candidates)

        // Mock repository to return the expected candidates
        `when`(mockCandidateRepository.getCandidates(false, null))
            .thenReturn(candidates)

        // Call the method to be tested
        println("test fetchCandidates_success : ACT")
        viewModel.fetchCandidates(favorite = false, query = null)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()

        // Collect actual homeScreenState from the ViewModel
        val actualHomeScreenState = viewModel.homeScreenState.value

        // Assert that the homeScreenState is DisplayCandidates
        println("test fetchCandidates_success : ASSERT")
        println("test fetchCandidates_success : expectedHomeScreenState = $expectedHomeScreenState")
        println("test fetchCandidates_success : actualHomeScreenState = ${actualHomeScreenState.toString()}")
        try {
            assertEquals(expectedHomeScreenState, viewModel.homeScreenState.value)
            println("test fetchCandidates_success : SUCCESS")
        } catch (e: AssertionError) {
            println("test fetchCandidates_success : FAIL")
            throw e
        }
    }

    /**
     * Tests the [HomeScreenViewModel.fetchCandidates] method to verify that it correctly handles
     * the scenario where no candidates are available.
     *
     * The method is expected to update the [HomeScreenState] to [HomeScreenState.Empty].
     */
    @Test
    fun fetchCandidates_empty() = runTest {
        println("test fetchCandidates_empty : ARRANGE")
        val candidates = emptyList<Candidate>()

        // Expectation for the home screen state
        val expectedHomeScreenState =
            HomeScreenState.Empty(R.string.home_screen_empty_state_message)

        // Mock repository to return an empty list
        `when`(mockCandidateRepository.getCandidates(false, null))
            .thenReturn(candidates)

        // Call the method to be tested
        println("test fetchCandidates_empty : ACT")
        viewModel.fetchCandidates(favorite = false, query = null)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()

        // Collect actual homeScreenState from the ViewModel
        val actualHomeScreenState = viewModel.homeScreenState.value

        // Assert that the homeScreenState is Empty
        println("test fetchCandidates_empty : ASSERT")
        println("test fetchCandidates_empty : expectedHomeScreenState = $expectedHomeScreenState")
        println("test fetchCandidates_empty : actualHomeScreenState = ${actualHomeScreenState.toString()}")
        try {
            assertEquals(expectedHomeScreenState, actualHomeScreenState)
            println("test fetchCandidates_empty : SUCCESS")
        } catch (e: AssertionError) {
            println("test fetchCandidates_empty : FAIL")
            throw e
        }
    }

    /**
     * Tests the [HomeScreenViewModel.fetchCandidates] method to verify that it correctly handles
     * the scenario where an error occurs while fetching candidates.
     *
     * The method is expected to update the [HomeScreenState] to [HomeScreenState.Error].
     */
    @Test
    fun fetchCandidates_error() = runTest {
        println("test fetchCandidates_error : ARRANGE")

        // Expectation for the home screen state
        val expectedHomeScreenState =
            HomeScreenState.Error(R.string.home_screen_error_state_message)

        // Mock repository to return an exception
        `when`(
            mockCandidateRepository.getCandidates(false, null)
        ).thenThrow(RuntimeException("CandidateDao Exception"))

        // Call the method to be tested
        println("test fetchCandidates_error : ACT")
        viewModel.fetchCandidates(favorite = false, query = null)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()

        // Collect actual homeScreenState from the ViewModel
        val actualHomeScreenState = viewModel.homeScreenState.value

        // Assert that the homeScreenState is Empty
        println("test fetchCandidates_error : ASSERT")
        println("test fetchCandidates_error : expectedHomeScreenState = $expectedHomeScreenState")
        println("test fetchCandidates_error : actualHomeScreenState = ${actualHomeScreenState.toString()}")
        try {
            assertEquals(expectedHomeScreenState, viewModel.homeScreenState.value)
            println("test fetchCandidates_error : SUCCESS")
        } catch (e: AssertionError) {
            println("test fetchCandidates_error : FAIL")
            throw e
        }
    }
}