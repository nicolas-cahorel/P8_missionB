package com.openclassrooms.p8_vitesse

import android.content.SharedPreferences
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenState
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneOffset


@ExperimentalCoroutinesApi
class HomeScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockCandidateRepository: CandidateRepository

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var viewModel: HomeScreenViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        MockitoAnnotations.openMocks(this)

        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }

        viewModel = HomeScreenViewModel(mockCandidateRepository, mockSharedPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test the fetchCandidates method to verify successful candidate loading.
     */
    @Test
    fun fetchCandidates_Success() = runTest {
        println("test fetchCandidate_Success : ARRANGE")
        val expectedCandidates = listOf(
            Candidate(
                id = 1,
                photo = "https://xsgames.co/randomusers/assets/avatars/male/0.jpg",
                firstName = "John",
                lastName = "Doe",
                phoneNumber = "1234567890",
                emailAddress = "john.doe@example.com",
                dateOfBirthStr = LocalDate.of(1985, 5, 15).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                expectedSalary = 50000,
                informationNote = "TEST : Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor...",
                isFavorite = false),

            Candidate(
                id = 2,
                photo = "https://xsgames.co/randomusers/assets/avatars/male/1.jpg",
                firstName = "Jim",
                lastName = "Nastik",
                phoneNumber = "1234567891",
                emailAddress = "jim.nastik@example.com",
                dateOfBirthStr = LocalDate.of(1980, 4, 20).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                expectedSalary = 45000,
                informationNote = "TEST : Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor...",
                isFavorite = false)
        )
        // Expectation for the home screen state
        val expectedHomeScreenState = HomeScreenState.DisplayCandidates(expectedCandidates)

        // Mock repository to return the expected candidates
        `when`(mockCandidateRepository.getCandidates(false, null))
            .thenReturn(expectedCandidates)

        // Call the method to be tested
        println("test fetchCandidates_Success : ACT")
        viewModel.fetchCandidates(favorite = false, query = null)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert that the homeScreenState is DisplayCandidates
        println("test fetchCandidates_Success : ASSERT")
        try {
            assertEquals(expectedHomeScreenState, viewModel.homeScreenState.value)
            println("test fetchCandidates_Success : SUCCESS, ${viewModel.homeScreenState.value}")
        } catch (e: AssertionError) {
            println("test fetchCandidates_Success : FAIL, ${viewModel.homeScreenState.value}")
            throw e
        }
    }

    /**
     * Test the fetchCandidates method to verify empty handling.
     */
    @Test
    fun fetchCandidates_empty() = runTest {
        println("test fetchCandidates_empty : ARRANGE")
        val expectedEmptyMessage = R.string.home_screen_error_state_message

        // Mock repository to return an empty list
//        `when`(mockCandidateRepository.getCandidates(false, null))
//            .thenThrow(Exception("An error has occurred, please try again"))
        doThrow(IOException::class.java).`when`(mockCandidateRepository).getCandidates(false, null)

        // Call the method to be tested
        println("test fetchCandidates_empty : ACT")
        viewModel.fetchCandidates(favorite = false, query = null)

        // Simulates time passed and the flow went to the end
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert that the homeScreenState is Empty
        println("test fetchCandidates_empty : ASSERT")
        try {
            assertEquals(HomeScreenState.Error(expectedEmptyMessage), viewModel.homeScreenState.value)
            println("test fetchCandidates_empty : SUCCESS, ${viewModel.homeScreenState.value}")
        } catch (e: AssertionError) {
            println("test fetchCandidates_empty : FAIL, ${viewModel.homeScreenState.value}")
            throw e
        }
    }
}