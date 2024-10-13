package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the state of the Add or Edit Screen.
 *
 * This ViewModel interacts with the [CandidateRepository] to handle the addition or modification
 * of candidate data. It also manages the media access permission status through SharedPreferences.
 *
 * @property candidateRepository The repository used to add or update candidate data.
 * @property sharedPreferences SharedPreferences for storing and retrieving media access permission status.
 */
class AddOrEditScreenViewModel(
    private val candidateRepository: CandidateRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_IS_MEDIA_ACCESS_PERMITTED = "isMediaAccessPermitted"
    }

    /**
     * Sets the media access permission status and saves it to SharedPreferences.
     *
     * @param isMediaAccessPermitted The new media access permission status.
     */
    fun setMediaAccessPermissionStatus(isMediaAccessPermitted: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_MEDIA_ACCESS_PERMITTED, isMediaAccessPermitted)
            .apply()
    }

    /**
     * Retrieves the current media access permission status from SharedPreferences.
     *
     * @return The current status of media access permission.
     */
    fun getMediaAccessPermissionStatus(): Boolean {
        return sharedPreferences.getBoolean(
            KEY_IS_MEDIA_ACCESS_PERMITTED,
            false
        ) // Provide a default value
    }

    /**
     * Adds a new candidate or updates an existing candidate in the repository.
     *
     * This function runs on the IO dispatcher to avoid blocking the main thread.
     * It ensures the candidate data is correctly persisted in the repository.
     *
     * @param candidate The candidate object to add or update.
     */
    fun addOrEditCandidate(candidate: Candidate) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateRepository.addOrUpdateCandidate(candidate)
        }
    }
}