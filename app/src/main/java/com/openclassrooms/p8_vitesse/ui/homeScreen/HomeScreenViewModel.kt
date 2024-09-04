package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.content.Context
import android.content.SharedPreferences
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
 * @property sharedPreferences SharedPreferences for storing candidate data.
 */
class HomeScreenViewModel(
    private val candidateRepository: CandidateRepository,
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_CANDIDATE_IDENTIFIER = "candidateIdentifier"
    }

    /**
     * Event to trigger navigation to [DetailScreenFragment].
     * Emitted when a candidate item is clicked and should navigate to the detail screen.
     */
    private val _navigateToDetailScreenEvent = MutableSharedFlow<Unit>()
    val navigateToDetailScreenEvent: SharedFlow<Unit> get() = _navigateToDetailScreenEvent

    /**
     * Event to trigger navigation to [AddScreenFragment].
     * Emitted when navigating to the add screen is required.
     */
    private val _navigateToAddScreenEvent = MutableSharedFlow<Unit>()
    val navigateToAddScreenEvent: SharedFlow<Unit> get() = _navigateToAddScreenEvent

    /**
     * MutableStateFlow representing the current state of the Home Screen.
     * It can be in different states like Loading, DisplayCandidates, Empty, or Error.
     */
    private val _homeScreenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val homeScreenStateState: StateFlow<HomeScreenState> = _homeScreenState

    /**
     * SharedFlow for sending messages to the UI, such as error messages or notifications.
     */
    private val _stateMessage = MutableSharedFlow<String>()
    val stateMessage: SharedFlow<String> get() = _stateMessage

    /**
     * The candidate identifier accessed from SharedPreferences.
     * This identifier is used to keep track of the selected candidate.
     */
    private var candidateIdentifier: Long

    /**
     * MutableStateFlow to hold the current search query.
     */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    /**
     * Initializes the ViewModel and fetches the initial list of candidates.
     * Also retrieves the candidate identifier from SharedPreferences.
     */
    init {
        fetchCandidates(favorite = false, query = null)
        candidateIdentifier =
            sharedPreferences.getLong(KEY_CANDIDATE_IDENTIFIER, -1) // Provide a default value
    }

    /**
     * Fetches candidates from the repository and updates the UI state.
     * Displays filtered candidates or shows appropriate messages based on the result.
     *
     * @param favorite Whether to filter candidates based on favorites.
     * @param query The search query to filter candidates.
     */
    fun fetchCandidates(favorite: Boolean, query: String?) {
        viewModelScope.launch {
            _homeScreenState.value = HomeScreenState.Loading

            try {
                // Fetch all filtered candidates from the repository
                val candidates = candidateRepository.getCandidates(favorite, query)


                // Update state based on whether candidates were found or not
                if (candidates.isEmpty()) {
                    _homeScreenState.value =
                        HomeScreenState.Empty(context.getString(R.string.display_message_no_result))
                } else {
                    _homeScreenState.value = HomeScreenState.DisplayCandidates(candidates)
                }
            } catch (e: Exception) {
                _homeScreenState.value =
                    HomeScreenState.Error(context.getString(R.string.display_message_error))
            }
        }
    }

    /**
     * Handles the event when an item is clicked.
     * Stores the clicked candidate's ID in SharedPreferences.
     *
     * @param candidateId The ID of the clicked candidate.
     */
    fun onItemClicked(candidateId: Long) {
        sharedPreferences.edit().putLong(KEY_CANDIDATE_IDENTIFIER, candidateId).apply()

    }

}