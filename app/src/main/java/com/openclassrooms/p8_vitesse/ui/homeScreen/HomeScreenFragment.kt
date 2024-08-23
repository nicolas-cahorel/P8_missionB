package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentHomeScreenBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel



/**
 * Fragment responsible for handling home screen.
 *
 * This fragment manages the UI for home screen
 */
class HomeScreenFragment : Fragment() {

    /**
     * The binding for the home screen layout.
     */
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    // ViewModel associated with the fragment
    private val viewModel: HomeScreenViewModel by viewModel()

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
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup TabLayout with custom tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.tab_title_all)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.tab_title_favorites)))

//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//               // filtrer la liste de ce qui va etre afficher en utilisant un bollean isfavorite = true
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                // Optional: Handle tab unselected
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                // Optional: Handle tab reselected
//            }
//        })


        // Observe the state flow
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeScreenStateState.collect { state ->
                    when (state) {

                        // Show loading state
                        is HomeScreenState.Loading -> {
                            binding.homeScreenLoadingProgressBar.visibility = View.VISIBLE
                            binding.homeScreenMessageTextView.visibility = View.GONE
                        }

                        // Show success state
                        is HomeScreenState.DisplayAllCandidates -> {
                            binding.homeScreenLoadingProgressBar.visibility = View.GONE
                            binding.homeScreenMessageTextView.visibility = View.GONE
                        }

                        // Show empty state
                        is HomeScreenState.Empty -> {
                            binding.homeScreenLoadingProgressBar.visibility = View.GONE
                            binding.homeScreenMessageTextView.visibility = View.VISIBLE
                        }

                        // Show error state
                        is HomeScreenState.Error -> {
                            binding.homeScreenLoadingProgressBar.visibility = View.GONE
                            binding.homeScreenMessageTextView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}