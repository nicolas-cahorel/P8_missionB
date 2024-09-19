package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentDetailScreenBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.addOrEditScreen.AddOrEditScreenFragment
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import java.util.Locale

/**
 * Fragment responsible for displaying candidate details.
 *
 * It allows for calling, sending SMS or email to the candidate,
 * and handling candidate updates (favorite status) or deletion.
 */
class DetailScreenFragment : Fragment() {

    /**
     * The binding for the detail screen layout.
     */
    private var _binding: FragmentDetailScreenBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for managing the state of the Detail Screen.
     */
    private val viewModel: DetailScreenViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Handle the back button click to navigate to the home screen
        binding.buttonDetailScreenBack.setOnClickListener {
            navigateToHomeScreen()
        }

        // Set up the delete button with a confirmation dialog
        binding.buttonDetailScreenDelete.setOnClickListener{
            deleteConfirmationDialog()
        }
        binding.buttonDetailScreenDelete.setOnLongClickListener{
            Toast.makeText(context, getString(R.string.toast_delete), Toast.LENGTH_SHORT).show()
            true
        }

        binding.buttonDetailScreenEdit.setOnClickListener {
            val candidate = viewModel.candidateState.value
            candidate?.let {
                navigateToAddOrEditScreen(it)
            } ?: run {
                Toast.makeText(context, "Candidate data is not available.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonDetailScreenEdit.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.toast_edit), Toast.LENGTH_SHORT
            ).show()
            true
        }

        // Handle the "Make Call" button
        binding.buttonDetailScreenMakeCall.setOnClickListener {
            val numberToCall = viewModel.candidateState.value?.phoneNumber
            if (!numberToCall.isNullOrEmpty()) {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$numberToCall")
                }
                startActivity(dialIntent)
            } else {
                Toast.makeText(context, getString(R.string.toast_unavailable_phone_number), Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the "Send SMS" button
        binding.buttonDetailScreenSendSms.setOnClickListener {
            val numberToSendSms = viewModel.candidateState.value?.phoneNumber
            if (!numberToSendSms.isNullOrEmpty()) {
                val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$numberToSendSms")
                }
                startActivity(smsIntent)
            } else {
                Toast.makeText(context, getString(R.string.toast_unavailable_phone_number), Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the "Send Email" button
        binding.buttonDetailScreenSendEmail.setOnClickListener {
            val emailToSendMessage = viewModel.candidateState.value?.emailAddress
            if (!emailToSendMessage.isNullOrEmpty()) {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$emailToSendMessage") // Uri pour les emails
                }
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, getString(R.string.toast_no_email_app_found), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.toast_unavailable_email_adress), Toast.LENGTH_SHORT).show()
            }
        }

        // Combine multiple flows to update the UI when all data has been collected
        viewLifecycleOwner.lifecycleScope.launch {
            // Combine candidate state, exchange rate message, and converted salary
            combine(
                viewModel.candidateState,
                viewModel.exchangeRateMessage,
                viewModel.convertedSalary
            ) { candidate, message, convertedSalary ->
                Triple(candidate, message, convertedSalary)  // Combine results into a Triple
            }.collect { (candidate, message, convertedSalary) ->
                // Update the UI when all data is available
                candidate?.let {
                    val convertedSalaryToDisplay = String.format(Locale.getDefault(),"%.2f", convertedSalary)
                    updateUIWithCandidateDetails(it, message, convertedSalaryToDisplay)  // Update with candidate details
                }
            }
        }
    }

    /**
     * Show a confirmation dialog before deleting the candidate.
     */
    private fun deleteConfirmationDialog() {
        binding.detailScreenDialogComponent.visibility = View.VISIBLE
        binding.displayDetailScreenScreenDialogTitle.visibility = View.VISIBLE
        binding.displayDetailScreenScreenDialogContent.visibility = View.VISIBLE
        binding.buttonDetailScreenScreenDialogConfirm.visibility = View.VISIBLE
        binding.buttonDetailScreenScreenDialogCancel.visibility = View.VISIBLE
        binding.buttonDetailScreenScreenDialogConfirm.elevation = 50f
        binding.buttonDetailScreenScreenDialogCancel.elevation = 50f

        binding.buttonDetailScreenScreenDialogConfirm.setOnClickListener{
            deleteCandidate ()
            navigateToHomeScreen()
        }

        binding.buttonDetailScreenScreenDialogCancel.setOnClickListener{
            binding.detailScreenDialogComponent.visibility = View.GONE
            binding.displayDetailScreenScreenDialogTitle.visibility = View.GONE
            binding.displayDetailScreenScreenDialogContent.visibility = View.GONE
            binding.buttonDetailScreenScreenDialogConfirm.visibility = View.GONE
            binding.buttonDetailScreenScreenDialogCancel.visibility = View.GONE
        }
    }

    /**
     * Update the UI with the candidate's details.
     *
     * @param candidate The candidate whose details will be displayed.
     */
    private fun updateUIWithCandidateDetails(candidate: Candidate, message: String, convertedSalary: String) {
        binding.displayDetailScreenScreenName.text = getString(R.string.candidate_name, candidate.firstName, candidate.lastName)
        binding.displayDetailScreenBirthday.text = displayBirthdayDetails(candidate.dateOfBirthStr)
        binding.displayDetailScreenSalary.text = getString(R.string.candidate_salary, candidate.expectedSalary)
        binding.displayDetailScreenConvertedSalary.text = getString(R.string.converted_salary, convertedSalary)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        binding.displayDetailScreenNote.text = candidate.informationNote

        // Load the candidate photo using Glide
        Glide.with(binding.root.context)
            .load(candidate.photo)
            .error(R.drawable.default_avatar)
            .into(binding.displayDetailScreenAvatar)

        // Handle favorite status
        if (candidate.isFavorite) {
            binding.buttonDetailScreenFavoriteFalse.visibility = View.GONE
            binding.buttonDetailScreenFavoriteTrue.visibility = View.VISIBLE
            binding.buttonDetailScreenFavoriteTrue.setOnClickListener {
                updateCandidate(false)
            }
            binding.buttonDetailScreenFavoriteTrue.setOnLongClickListener {
                Toast.makeText(context, getString(R.string.toast_favorite), Toast.LENGTH_SHORT).show()
                true
            }
        } else {
            binding.buttonDetailScreenFavoriteFalse.visibility = View.VISIBLE
            binding.buttonDetailScreenFavoriteTrue.visibility = View.GONE
            binding.buttonDetailScreenFavoriteFalse.setOnClickListener {
                updateCandidate(true)
            }
            binding.buttonDetailScreenFavoriteFalse.setOnLongClickListener {
                Toast.makeText(context, getString(R.string.toast_favorite), Toast.LENGTH_SHORT).show()
                true
            }
        }
    }



    /**
     * Navigate to the HomeScreenFragment.
     */
    private fun navigateToHomeScreen() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val homeScreenFragment = HomeScreenFragment()
        fragmentTransaction.replace(R.id.fragment_container, homeScreenFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /**
     * Navigate to the AddOrEditScreenFragment with the selected candidate.
     */
    private fun navigateToAddOrEditScreen(candidate: Candidate) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val addOrEditScreenFragment = AddOrEditScreenFragment()

// Create a Bundle to pass the candidate
        val bundle = Bundle()
        bundle.putParcelable("candidate", candidate)

        // Set the arguments on the new fragment
        addOrEditScreenFragment.arguments = bundle

        fragmentTransaction.replace(R.id.fragment_container, addOrEditScreenFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /**
     * Display the candidate's birthday details including age.
     *
     * @param birthDateTimestamp The timestamp of the candidate's birthday.
     * @return A string representation of the candidate's birthdate and age.
     */
    private fun displayBirthdayDetails(birthDateTimestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(birthDateTimestamp)
        val formattedBirthDate: String = formatter.format(date)

        val birthDate = Date(birthDateTimestamp).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val currentDate = LocalDate.now()

        val age: Int = Period.between(birthDate, currentDate).years

        return "$formattedBirthDate ($age ${binding.root.context.getString(R.string.years)})"
    }

    /**
     * Update the favorite status of the candidate.
     *
     * @param isFavorite Whether the candidate should be marked as favorite.
     */
    private fun updateCandidate(isFavorite: Boolean) {
        val updatedCandidate = viewModel.candidateState.value!!.copy(isFavorite = isFavorite)
        // Add the new candidate to the ViewModel
        viewModel.updateCandidate(updatedCandidate)
    }

    /**
     * Delete the candidate.
     */
    private fun deleteCandidate() {
        val candidateToDelete = viewModel.candidateState.value!!.copy()
        // Add the new candidate to the ViewModel
        viewModel.deleteCandidate(candidateToDelete)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}