package com.openclassrooms.p8_vitesse.ui.addScreen

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.Dispatchers
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
class AddScreenViewModel(
    private val candidateRepository: CandidateRepository,
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_IS_MEDIA_ACCESS_PERMITTED = "isMediaAccessPermitted"
    }

    /**
     * The candidate identifier accessed from SharedPreferences.
     * This identifier is used to keep track of the selected candidate.
     */
    private var isMediaAccessPermitted: Boolean =
        sharedPreferences.getBoolean(KEY_IS_MEDIA_ACCESS_PERMITTED, false) // Provide a default value

    /**
     * Handles the event when an item is clicked.
     * Stores the clicked candidate's ID in SharedPreferences.
     *
     * @param candidateId The ID of the clicked candidate.
     */
    /**
     * Handles the event when an item is clicked.
     * Stores the clicked candidate's ID in SharedPreferences.
     */
    fun setMediaAccessPermissionStatus(isMediaAccessPermitted: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_MEDIA_ACCESS_PERMITTED, isMediaAccessPermitted).apply()
    }

    /**
     * Retrieves the current media access permission status from SharedPreferences.
     */
    fun getMediaAccessPermissionStatus(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_MEDIA_ACCESS_PERMITTED, false) // Provide a default value
    }

    fun addNewCandidate(candidate: Candidate) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.addCandidate(candidate)
        }
    }

}