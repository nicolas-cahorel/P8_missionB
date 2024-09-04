package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentHomeScreenBinding
import com.openclassrooms.p8_vitesse.ui.addScreen.AddScreenFragment
import com.openclassrooms.p8_vitesse.ui.detailScreen.DetailScreenFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE


/**
 * Fragment responsible for handling the home screen UI.
 *
 * This fragment manages the display of all and favorite candidates using tabs and observes
 * the [HomeScreenViewModel] to update the UI based on the state.
 */
class HomeScreenFragment : Fragment() {

    /**
     * The binding for the home screen layout.
     */
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for managing the state of the Home Screen.
     */
    private val viewModel: HomeScreenViewModel by viewModel()

    /**
     * Adapter for displaying candidates in the RecyclerView. It handles item clicks
     * and triggers navigation to the detail screen.
     */
    private val homeScreenAdapter = HomeScreenAdapter(emptyList()) { candidateId ->
        // Call ViewModel's onItemClicked with the clicked candidate ID
        viewModel.onItemClicked(candidateId)
        navigateToDetailScreen()
    }

    private var currentQuery: String? = null

    private var isFavoritesTabSelected = false

    /**
     * Creates and returns the view hierarchy associated with this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the fragment's activity has been created and the fragment's view hierarchy instantiated.
     *
     * Sets up the TabLayout with custom tabs, observes the ViewModel for state updates,
     * and initializes RecyclerView and search bar.
     *
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        // Set up the RecyclerView for displaying the list of candidates.
        binding.homeScreenRecyclerview.layoutManager =
            LinearLayoutManager(context)
        binding.homeScreenRecyclerview.adapter = homeScreenAdapter

        // Setup TabLayout with custom tabs
        binding.homeScreenTabLayout.addTab(
            binding.homeScreenTabLayout.newTab().setText(getString(R.string.display_tab_title_all))
        )
        binding.homeScreenTabLayout.addTab(
            binding.homeScreenTabLayout.newTab()
                .setText(getString(R.string.display_tab_title_favorites))
        )

        // Set a listener for tab selection
        binding.homeScreenTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    // Trigger state to display all candidates when "All" tab is selected
                    0 -> {
                        isFavoritesTabSelected = false
                        viewModel.fetchCandidates(favorite = false, currentQuery)
                    }
                    // Trigger state to display favorites candidates when "Favorites" tab is selected
                    1 -> {
                        isFavoritesTabSelected = true
                        viewModel.fetchCandidates(favorite = true, currentQuery)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Update the ViewModel with changes in the search bar field
        binding.inputHomeScreenSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                viewModel.fetchCandidates(favorite = isFavoritesTabSelected, currentQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set up a click listener for the "Add" button.
        binding.buttonHomeScreenAdd.setOnClickListener {
            navigateToAddScreen()
        }

        // Observe the state flow from the ViewModel and update the UI accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeScreenStateState.collect { state ->
                    when (state) {

                        // Show loading state
                        is HomeScreenState.Loading -> {
                            binding.homeScreenStateProgressBar.visibility = View.VISIBLE
                            binding.homeScreenStateMessage.visibility = View.GONE
                            binding.inputHomeScreenSearchBar.isEnabled = FALSE
                            binding.homeScreenRecyclerview.visibility = View.GONE
                            binding.buttonHomeScreenAdd.visibility = View.GONE
                        }

                        // Show all candidates state
                        is HomeScreenState.DisplayCandidates -> {
                            binding.homeScreenStateProgressBar.visibility = View.GONE
                            binding.homeScreenStateMessage.visibility = View.GONE
                            binding.inputHomeScreenSearchBar.isEnabled = TRUE
                            binding.homeScreenRecyclerview.adapter = homeScreenAdapter
                            homeScreenAdapter.updateData(state.candidates)
                            binding.homeScreenRecyclerview.visibility = View.VISIBLE
                            binding.buttonHomeScreenAdd.visibility = View.VISIBLE
                        }

                        // Show empty state with a message
                        is HomeScreenState.Empty -> {
                            binding.homeScreenStateProgressBar.visibility = View.GONE
                            binding.homeScreenStateMessage.apply {
                                visibility = View.VISIBLE
                                text = state.stateMessage // Display the message for empty state
                            }
                            binding.inputHomeScreenSearchBar.isEnabled = TRUE
                            binding.homeScreenRecyclerview.visibility = View.GONE
                            binding.buttonHomeScreenAdd.visibility = View.VISIBLE
                        }

                        // Show error state with a message
                        is HomeScreenState.Error -> {
                            binding.homeScreenStateProgressBar.visibility = View.GONE
                            binding.homeScreenStateMessage.apply {
                                visibility = View.VISIBLE
                                text = state.stateMessage // Display the error message
                            }
                            binding.inputHomeScreenSearchBar.isEnabled = TRUE
                            binding.homeScreenRecyclerview.visibility = View.GONE
                            binding.buttonHomeScreenAdd.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    /**
     * Set up observers for the ViewModel's state flows.
     */
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeScreenStateState.collect() { state ->
                // Check if the state is DisplayCandidates
                if (state is HomeScreenState.DisplayCandidates) {
                    // Update the adapter with the list of candidates from the state
                    homeScreenAdapter.updateData(state.candidates)
                }
            }
        }
    }

    /**
     * Navigate to the AddScreenFragment.
     */
    private fun navigateToAddScreen() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val addScreenFragment = AddScreenFragment()
        fragmentTransaction.replace(R.id.fragment_container, addScreenFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /**
     * Navigate to the DetailScreenFragment.
     */
    private fun navigateToDetailScreen() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val detailScreenFragment = DetailScreenFragment()
        fragmentTransaction.replace(R.id.fragment_container, detailScreenFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /**
     * Called when the view previously created by [onCreateView] has been detached from the fragment.
     *
     * This method clears the binding reference to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}