package com.openclassrooms.p8_vitesse.ui.homeScreen

/**
 * Sealed interface representing the different states of the Home Screen.
 */
sealed interface HomeScreenState {

    /**
     * Represents a loading state during Home Screen data loading.
     */
    data object Loading : HomeScreenState

    /**
     * Represents a success state of the Home Screen data loading success.
     */
    data object Success : HomeScreenState

    /**
     * Represents an empty state of the empty Home Screen.
     */
    data object Empty : HomeScreenState

    /**
     * Represents an error state with a message describing the failure.
     *
     * @property message The error message describing why the loading failed.
     */
    data class Error(val message: String) : HomeScreenState

}