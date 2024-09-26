package com.openclassrooms.p8_vitesse.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment

/**
 * MainActivity serves as the main entry point of the application.
 *
 * This activity is responsible for hosting the fragment container where the
 * application's primary content is displayed. It initializes the initial fragment
 * and manages fragment transactions. This ensures that the [HomeScreenFragment] is
 * the default UI the user interacts with when the app is launched.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is first created.
     *
     * This method sets up the content view for the activity and, if this is the first time
     * the activity is created (i.e., `savedInstanceState` is null), it will load the
     * [HomeScreenFragment] into the fragment container.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down, this Bundle contains the data it most recently
     * supplied in [onSaveInstanceState]. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to the layout resource for this activity
        setContentView(R.layout.activity_main)

        // Check if this is the first time the activity is being created
        if (savedInstanceState == null) {
            // If the activity is newly created, add the HomeScreenFragment to the container
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    HomeScreenFragment()
                ) // Replace container with HomeScreenFragment
                .commit() // Commit the transaction to display the fragment
        }
    }
}