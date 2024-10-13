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
import com.openclassrooms.p8_vitesse.ui.addOrEditScreen.AddOrEditScreenFragment
import com.openclassrooms.p8_vitesse.ui.detailScreen.DetailScreenFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

/**
 * Fragment responsible for managing the Home Screen UI.
 *
 * This fragment handles displaying all and favorite candidates using a tab system. It observes
 * the [HomeScreenViewModel] to update the UI based on the current state, such as loading, empty,
 * or error states.
 */
class HomeScreenFragment : Fragment() {

    /**
     * The binding for the home screen layout.
     */
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for handling candidate data and UI state.
     */
    private val viewModel: HomeScreenViewModel by viewModel()

    /**
     * Adapter for displaying the list of candidates in the RecyclerView.
     * It handles item clicks and navigates to the candidate detail screen.
     */
    private val homeScreenAdapter = HomeScreenAdapter(emptyList()) { candidateId ->
        // Call ViewModel's onItemClicked with the clicked candidate ID
        viewModel.onItemClicked(candidateId)
        navigateToDetailScreen()
    }

    /**
     * Holds the current query from the search bar.
     */
    private var currentQuery: String? = null

    /**
     * Tracks if the "Favorites" tab is selected.
     */
    private var isFavoritesTabSelected = false

    /**
     * Inflates and returns the view associated with this fragment.
     *
     * @param inflater The LayoutInflater object to inflate views in the fragment.
     * @param container The parent view group.
     * @param savedInstanceState Saved instance state for restoring the fragment state.
     * @return The View representing the fragment's layout.
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
     * Called when the fragment's activity has been created.
     * Sets up the UI components like RecyclerView, TabLayout, and search bar, and observes the ViewModel.
     *
     * @param view The view returned by [onCreateView].
     * @param savedInstanceState The saved state of the fragment, if any.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchBar()
        setupTabLayout()
        setupAddButton()
        setupRecyclerView()
        updateUIWithState()
    }

    override fun onResume() {
        super.onResume()
        fetchInitialCandidates()
    }

    /**
     * Sets up the search bar to filter candidates based on user input.
     * Listens for text changes and triggers the candidate fetching logic.
     */
    private fun setupSearchBar() {
        binding.homeScreenSearchBarField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                viewModel.fetchCandidates(favorite = isFavoritesTabSelected, currentQuery)
            }
        })
    }

    /**
     * Sets up the TabLayout with custom tabs for viewing all candidates or favorite candidates.
     * Includes a listener for tab selection to fetch the relevant candidates.
     */
    private fun setupTabLayout() {
        // Setup TabLayout with custom tabs
        binding.homeScreenTabLayout.addTab(
            binding.homeScreenTabLayout.newTab()
                .setText(getString(R.string.home_screen_tab_title_all))
        )
        binding.homeScreenTabLayout.addTab(
            binding.homeScreenTabLayout.newTab()
                .setText(getString(R.string.home_screen_tab_title_favorites))
        )

        // Set a listener for tab selection
        binding.homeScreenTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    // "All" tab selected
                    0 -> {
                        isFavoritesTabSelected = false
                        viewModel.fetchCandidates(favorite = false, currentQuery)
                    }
                    // "Favorites" tab selected
                    1 -> {
                        isFavoritesTabSelected = true
                        viewModel.fetchCandidates(favorite = true, currentQuery)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    /**
     * Sets up the click listener for the "Add" button, which navigates to the Add or Edit screen.
     */
    private fun setupAddButton() {
        binding.homeScreenAddButton.setOnClickListener {
            navigateToAddOrEditScreen()
        }
    }

    /**
     * Sets up the RecyclerView for displaying the list of candidates.
     * Configures the layout manager and assigns the adapter for the RecyclerView.
     */
    private fun setupRecyclerView() {
        binding.homeScreenRecyclerview.layoutManager =
            LinearLayoutManager(context)
        binding.homeScreenRecyclerview.adapter = homeScreenAdapter
    }

    /**
     * Fetches the initial candidates when the fragment is created.
     * This method will be called when the fragment view is created to populate the candidate list.
     */
    private fun fetchInitialCandidates() {
        viewModel.fetchCandidates(favorite = isFavoritesTabSelected, query = currentQuery)
    }

    /**
     * Updates the UI based on the current state collected from the ViewModel.
     * This method observes the state flow from the ViewModel and updates the UI accordingly by:
     * - Displaying a loading indicator when the state is [HomeScreenState.Loading].
     * - Showing the list of candidates when the state is [HomeScreenState.DisplayCandidates].
     * - Displaying a message when no candidates are found (state is [HomeScreenState.Empty]).
     * - Showing an error message when an error occurs during data fetching (state is [HomeScreenState.Error]).
     *
     * The UI elements affected by this method include:
     * - A progress bar to indicate loading.
     * - A message view to show empty or error messages.
     * - A search bar that can be enabled or disabled based on the loading state.
     * - A RecyclerView to display the list of candidates, which is updated with new data when available.
     * - A button for adding new candidates, whose visibility is controlled based on the current state.
     *
     * This method is called within the lifecycle of the fragment and should be invoked when the
     * fragment's view is created to ensure the UI reflects the current data state.
     */
    private fun updateUIWithState() {
        // Observe the state flow from the ViewModel and update the UI accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeScreenState.collect { state ->
                    when (state) {

                        // Show loading state
                        is HomeScreenState.Loading -> {
                            binding.homeScreenStateProgressBar.visibility = View.VISIBLE
                            binding.homeScreenStateMessage.visibility = View.GONE
                            binding.homeScreenSearchBarField.isEnabled = FALSE
                            binding.homeScreenRecyclerview.visibility = View.GONE
                            binding.homeScreenAddButton.visibility = View.GONE
                        }

                        // Show list of candidates
                        is HomeScreenState.DisplayCandidates -> {
                            binding.homeScreenStateProgressBar.visibility = View.GONE
                            binding.homeScreenStateMessage.visibility = View.GONE
                            binding.homeScreenSearchBarField.isEnabled = TRUE
                            binding.homeScreenRecyclerview.adapter = homeScreenAdapter
                            homeScreenAdapter.updateData(state.candidates)
                            binding.homeScreenRecyclerview.visibility = View.VISIBLE
                            binding.homeScreenAddButton.visibility = View.VISIBLE
                        }

                        // Show empty state message
                        is HomeScreenState.Empty -> {
                            binding.homeScreenStateProgressBar.visibility = View.GONE
                            binding.homeScreenStateMessage.apply {
                                visibility = View.VISIBLE
                                text =
                                    getString(state.stateMessageId) // Display the message for empty state
                            }
                            binding.homeScreenSearchBarField.isEnabled = TRUE
                            binding.homeScreenRecyclerview.visibility = View.GONE
                            binding.homeScreenAddButton.visibility = View.VISIBLE
                        }

                        // Show error state message
                        is HomeScreenState.Error -> {
                            binding.homeScreenStateProgressBar.visibility = View.GONE
                            binding.homeScreenStateMessage.apply {
                                visibility = View.VISIBLE
                                text = getString(state.stateMessageId) // Display the error message
                            }
                            binding.homeScreenSearchBarField.isEnabled = TRUE
                            binding.homeScreenRecyclerview.visibility = View.GONE
                            binding.homeScreenAddButton.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    /**
     * Navigate to the AddOrEditScreenFragment for adding or editing a candidate.
     *
     * This method replaces the current fragment with the [AddOrEditScreenFragment].
     * It adds the transaction to the back stack to allow users to return to the previous fragment.
     */
    private fun navigateToAddOrEditScreen() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val addOrEditScreenFragment = AddOrEditScreenFragment()
        fragmentTransaction.replace(R.id.fragment_container, addOrEditScreenFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /**
     * Navigate to the DetailScreenFragment to view candidate details.
     *
     * This method replaces the current fragment with the [DetailScreenFragment].
     * It adds the transaction to the back stack to allow users to navigate back.
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
     * This method clears the view binding reference to avoid memory leaks by setting `_binding` to null.
     * Always ensure that `_binding` is set to null in [onDestroyView] to avoid holding references
     * to views that are no longer in use.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}