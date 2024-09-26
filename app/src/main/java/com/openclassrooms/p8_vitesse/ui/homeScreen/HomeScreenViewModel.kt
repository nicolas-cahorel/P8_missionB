package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import kotlinx.coroutines.flow.MutableStateFlow
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
 * @property sharedPreferences SharedPreferences for storing candidate data.
 */
class HomeScreenViewModel(
    private val candidateRepository: CandidateRepository,
    private val context: Application,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_CANDIDATE_IDENTIFIER = "candidateIdentifier"
    }

    /**
     * MutableStateFlow representing the current state of the Home Screen.
     * The state can be one of the following:
     * - [HomeScreenState.Loading]: when data is being loaded.
     * - [HomeScreenState.DisplayCandidates]: when candidates are successfully loaded and displayed.
     * - [HomeScreenState.Empty]: when no candidates match the current filter.
     * - [HomeScreenState.Error]: when an error occurs during data fetching.
     */
    private val _homeScreenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val homeScreenState: StateFlow<HomeScreenState> = _homeScreenState

    /**
     * Initializes the ViewModel by fetching the list of candidates without filters.
     */
    init {
        fetchCandidates(favorite = false, query = null)
    }

    /**
     * Fetches candidates from the repository based on favorite filter and search query.
     * Updates the UI state based on the result.
     *
     * @param favorite Boolean flag to filter candidates by favorite status.
     * @param query Optional search query to filter candidates by name or other attributes.
     */
    fun fetchCandidates(favorite: Boolean, query: String?) {
        viewModelScope.launch {
            _homeScreenState.value = HomeScreenState.Loading

            try {
                // Fetch candidates from the repository with optional filtering
                val candidates = candidateRepository.getCandidates(favorite, query)


                // Update state depending on the candidates list
                if (candidates.isEmpty()) {
                    _homeScreenState.value =
                        HomeScreenState.Empty(context.getString(R.string.home_screen_empty_state_message))
                } else {
                    _homeScreenState.value = HomeScreenState.DisplayCandidates(candidates)
                }
            } catch (e: Exception) {
                _homeScreenState.value =
                    HomeScreenState.Error(context.getString(R.string.home_screen_error_state_message))
            }
        }
    }

    /**
     * Handles the event of selecting a candidate from the list.
     * Saves the selected candidate's ID in SharedPreferences for later retrieval.
     *
     * @param candidateId The ID of the selected candidate.
     */
    fun onItemClicked(candidateId: Long) {
        sharedPreferences.edit().putLong(KEY_CANDIDATE_IDENTIFIER, candidateId)
            .apply() // Store the candidate ID
    }
}