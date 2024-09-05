package com.openclassrooms.p8_vitesse.di

import android.content.Context
import android.content.SharedPreferences
import com.openclassrooms.p8_vitesse.data.database.AppDatabase
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.ui.addScreen.AddScreenViewModel
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Dependency injection module using Koin.
 */
val appModule = module {

    /**
     * Provides a CoroutineScope for the application.
     */
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    /**
     * Provides the AppDatabase instance.
     *
     * @param context The application context.
     * @param coroutineScope The CoroutineScope for database operations.
     * @return An instance of AppDatabase.
     */
    single {
        AppDatabase.getDatabase(androidContext(), get())
    }

    /**
     * Provides the CandidateDao from the AppDatabase.
     *
     * @return An instance of CandidateDtoDao.
     */
    single {
        get<AppDatabase>().candidateDtoDao()
    }

    /**
     * Provides the CandidateRepository using the CandidateDao.
     *
     * @return An instance of CandidateRepository.
     */
    single {
        CandidateRepository(get())
    }

    /**
     * Provides the HomeScreenViewModel.
     *
     * @return An instance of HomeScreenViewModel.
     */
    viewModel {
        HomeScreenViewModel(get(), get(), get())
    }

    /**
     * Provides the HomeScreenViewModel.
     *
     * @return An instance of HomeScreenViewModel.
     */
    viewModel {
        AddScreenViewModel(get(), get(), get())
    }

    // Define SharedPreferences
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "my_prefs",
            Context.MODE_PRIVATE
        )
    }

}