package com.openclassrooms.p8_vitesse.ui.homeScreen

/**
 * Sealed interface representing the different states of the Home Screen.
 */
sealed interface HomeScreenState {

    /**
     * Represents a loading state during Home Screen data loading.
     */
    data object Loading : HomeScreenState

    // TODO : progressbar display


    /**
     * Represents a success state of the Home Screen data loading success.
     */
    data object Success : HomeScreenState

    /**
     * Represents an empty state of the empty Home Screen with a dedicated message.
     *
     * @property message The message describing the empty result.
     */
    data class Empty(val message: String) : HomeScreenState
    // TODO : add expected message (Aucun candidat/no candidate)

    /**
     * Represents an error state with a message describing the failure.
     *
     * @property message The error message describing why the loading failed.
     */
    data class Error(val message: String) : HomeScreenState

}