package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class DetailScreenViewModel(
    private val candidateRepository: CandidateRepository,
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_CANDIDATE_IDENTIFIER = "candidateIdentifier"
    }

    /**
     * The candidate identifier accessed from SharedPreferences.
     * This identifier is used to keep track of the selected candidate.
     */
    private var candidateIdentifier: Long =
        sharedPreferences.getLong(KEY_CANDIDATE_IDENTIFIER, 0) // Provide a default value

    // StateFlow to expose candidate data to the UI
    private val _candidateState = MutableStateFlow<Candidate?>(null)
    val candidateState: StateFlow<Candidate?> = _candidateState.asStateFlow()

    init {
        // Load the candidate information when the ViewModel is created
        viewModelScope.launch(Dispatchers.IO) {
            val candidate = candidateRepository.getCurrentCandidate(candidateIdentifier)
            _candidateState.value = candidate // Update the StateFlow with the fetched candidate
        }

    }

    fun updateCandidate(candidate: Candidate) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.updateCandidate(candidate)
        }
    }

    fun deleteCandidate(candidate: Candidate) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.deleteCandidate(candidate)
        }
    }
}