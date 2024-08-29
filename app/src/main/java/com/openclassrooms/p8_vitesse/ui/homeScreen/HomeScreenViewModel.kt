package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the state of the Home Screen.
 *
 * This ViewModel interacts with the [CandidateRepository] to fetch data and update the UI state accordingly.
 * It exposes state as [StateFlow] for UI observation and handles data loading, filtering, and error states.
 *
 * @property candidateRepository The repository used to fetch candidate data.
 * @property context The context used to access resources, such as strings.
 */
class HomeScreenViewModel(
    private val candidateRepository: CandidateRepository,
    private val context: Context
) : ViewModel() {

    // MutableStateFlow representing the current state of the Home Screen
    private val _homeScreenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)

    // Publicly exposed StateFlow for observing the Home Screen state
    val homeScreenStateState: StateFlow<HomeScreenState> = _homeScreenState

    // SharedFlow for sending messages (e.g., errors, notifications) to the UI
    private val _homeScreenMessage = MutableSharedFlow<String>()

    // Publicly exposed SharedFlow for observing messages from the ViewModel
    val homeScreenMessage: SharedFlow<String> get() = _homeScreenMessage

    // Initializing ViewModel with data loading simulation
    init {
        loadCandidates()
    }

    /**
     * Loads candidates from the repository and updates the UI state accordingly.
     *
     * This method simulates data loading by setting the state to loading, introducing a delay, and
     * then checking for the presence of candidates. Depending on the result, it updates the state
     * to either display candidates, show an empty message, or an error state.
     */
    private fun loadCandidates() {
        viewModelScope.launch {
            _homeScreenState.value = HomeScreenState.Loading

            // Fetch candidates from the repository
            try {
                val candidates = candidateRepository.getAllCandidates()

                // Check if the list is empty and update the state accordingly
                if (candidates.isEmpty()) {
                    val emptyMessage: String = context.getString(R.string.home_screen_message)
                    _homeScreenState.value = HomeScreenState.Empty(emptyMessage)
                } else {
                    _homeScreenState.value = HomeScreenState.DisplayAllCandidates(candidates)
                }
            } catch (e: Exception) {
                // If an error occurs, update the state to Error
                val errorMessage: String = context.getString(R.string.home_screen_error_message)
                _homeScreenState.value = HomeScreenState.Error(errorMessage)
                // Optionally, send a message to the UI through the shared flow
                _homeScreenMessage.emit(errorMessage)
            }
        }
    }

    /**
     * Displays all candidates by updating the state to [HomeScreenState.DisplayAllCandidates].
     */
    fun displayAllCandidates() {
        viewModelScope.launch {
            try {
                val candidates = candidateRepository.getAllCandidates()
                if (candidates.isEmpty()) {
                    _homeScreenState.value =
                        HomeScreenState.Empty(context.getString(R.string.home_screen_message))
                } else {
                    _homeScreenState.value = HomeScreenState.DisplayAllCandidates(candidates)
                }
            } catch (e: Exception) {
                _homeScreenState.value =
                    HomeScreenState.Error(context.getString(R.string.home_screen_error_message))
            }
        }
    }

    /**
     * Displays only favorite candidates by updating the state to [HomeScreenState.DisplayFavoritesCandidates].
     */
    fun displayFavoritesCandidates() {
        viewModelScope.launch {
            try {
                val favorites = candidateRepository.getFavoritesCandidates()
                if (favorites.isEmpty()) {
                    _homeScreenState.value =
                        HomeScreenState.Empty(context.getString(R.string.home_screen_message))
                } else {
                    _homeScreenState.value = HomeScreenState.DisplayFavoritesCandidates(favorites)
                }
            } catch (e: Exception) {
                _homeScreenState.value =
                    HomeScreenState.Error(context.getString(R.string.home_screen_error_message))
            }
        }
    }
}