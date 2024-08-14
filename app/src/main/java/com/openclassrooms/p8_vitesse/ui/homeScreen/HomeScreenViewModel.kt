package com.openclassrooms.p8_vitesse.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.p8_vitesse.data.repository.HomeScreenRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel (
    private val homeScreenRepository: HomeScreenRepository
): ViewModel() {

    // MutableStateFlow representing the state
    private val _homeScreenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    // Expose state as StateFlow
    val homeScreenStateState: StateFlow<HomeScreenState> = _homeScreenState

    private val _homeScreenMessage = MutableSharedFlow<String>()
    val homeScreenMessage: SharedFlow<String> get() = _homeScreenMessage

    init {
        // Simulate loading data
        viewModelScope.launch {
            // Simulate a delay
            kotlinx.coroutines.delay(2000)
            _homeScreenState.value = HomeScreenState.Success
        }
    }
}