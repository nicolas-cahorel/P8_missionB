package com.openclassrooms.p8_vitesse

import android.app.Application
import com.openclassrooms.p8_vitesse.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * MainApplication class that initializes application-wide settings and dependencies.
 *
 * This class extends [Application] and is responsible for setting up the dependency
 * injection framework Koin. It starts Koin and sets the application context and modules.
 */
class MainApplication : Application() {

    /**
     * Called when the application is starting, before any activity, service, or receiver objects
     * (excluding content providers) have been created.
     *
     * This is where the Koin dependency injection framework is initialized, setting the application
     * context and loading the defined modules.
     */
    override fun onCreate() {
        super.onCreate()

        // Start Koin for dependency injection
        startKoin {
            // Provide the application context to Koin
            androidContext(this@MainApplication)

            // Load the Koin modules, including appModule which defines dependencies
            modules(appModule)

            // Enable Koin logging
            printLogger()
        }
    }
}
