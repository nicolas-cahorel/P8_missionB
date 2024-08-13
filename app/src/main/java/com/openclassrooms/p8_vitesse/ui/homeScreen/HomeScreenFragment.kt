package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.openclassrooms.p8_vitesse.R


class HomeScreenFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the state flow
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {

                        // Show loading state
                        is HomeScreenState.Loading -> {
                            binding.pbLoginLoading.visibility = View.VISIBLE
                            }

                        // Show success state
                        is HomeScreenState.Success -> {

                        }

                        // Show empty state
                        is HomeScreenState.Empty -> {

                        }

                        // Show error state
                        is HomeScreenState.Error -> {

                        }
                    }
                }
            }
        }


    }

}