package com.openclassrooms.p8_vitesse.di

import com.openclassrooms.p8_vitesse.data.repository.HomeScreenRepository
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Provide necessary dependencies
    single { HomeScreenRepository() }

    // Define ViewModel for HomeScreenFragment
    viewModel{ HomeScreenViewModel(get()) }
}