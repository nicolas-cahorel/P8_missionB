package com.openclassrooms.p8_vitesse.ui.homeScreen

import com.openclassrooms.p8_vitesse.domain.model.Candidate

/**
 * Sealed interface representing the various states of the Home Screen.
 */
sealed interface HomeScreenState {

    /**
     * Represents a loading state during the Home Screen data loading process.
     * A progress bar is displayed while data is being loaded.
     */
    data object Loading : HomeScreenState

    /**
     * Represents a state where all candidates are displayed on the Home Screen.
     * This state is shown when the "All" tab is selected or when the user first arrives on the Home Screen.
     *
     * @property candidates The list of [Candidate] objects that will be displayed.
     */
    data class DisplayAllCandidates(val candidates: List<Candidate>) : HomeScreenState

    /**
     * Represents a state where only favorite candidates are displayed on the Home Screen.
     * This state is shown when the "Favorites" tab is selected.
     *
     * @property candidates The list of [Candidate] objects that will be displayed.
     */
    data class DisplayFavoritesCandidates(val candidates: List<Candidate>) : HomeScreenState

    /**
     * Represents an empty state on the Home Screen, usually indicating that there are no candidates to display.
     *
     * @property message The message explaining the absence of candidates.
     */
    data class Empty(val message: String) : HomeScreenState

    /**
     * Represents an error state with a message explaining why the data loading failed.
     *
     * @property message The message describing the cause of the failure.
     */
    data class Error(val message: String) : HomeScreenState
}
