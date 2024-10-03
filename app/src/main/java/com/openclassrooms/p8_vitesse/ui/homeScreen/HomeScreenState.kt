package com.openclassrooms.p8_vitesse.ui.homeScreen

import com.openclassrooms.p8_vitesse.domain.model.Candidate

/**
 * Sealed interface representing the various states of the Home Screen.
 * These states help manage the UI based on the data available and user actions.
 */
sealed interface HomeScreenState {

    /**
     * Represents a loading state during the Home Screen data loading process.
     * A progress bar or loading indicator is typically displayed while data is being loaded.
     */
    data object Loading : HomeScreenState

    /**
     * Represents a state where all candidates are displayed on the Home Screen.
     * This state is shown when the user arrives on the Home Screen.
     *
     * @property candidates The list of [Candidate] objects that will be displayed on the screen.
     */
    data class DisplayCandidates(val candidates: List<Candidate>) : HomeScreenState

    /**
     * Represents an empty state on the Home Screen, usually indicating that there are no candidates to display.
     * This can occur when a filter yields no results or when there are no candidates in the list.
     *
     * @property stateMessageId The message explaining the absence of candidates, which can be used to guide the user.
     */
    data class Empty(val stateMessageId: Int) : HomeScreenState

    /**
     * Represents an error state with a message explaining why the data loading or processing failed.
     * This state can be shown when there is an issue fetching data, such as network errors or data processing errors.
     *
     * @property stateMessageId The message describing the cause of the failure, helping the user understand what went wrong.
     */
    data class Error(val stateMessageId: Int) : HomeScreenState
}