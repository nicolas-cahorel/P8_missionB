package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
 * It allows the user to perform several actions such as:
 * - Calling the candidate
 * - Sending an SMS or email to the candidate
 * - Updating the candidate's information (like favorite status)
 * - Deleting the candidate from the database
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

    /**
     * A flag to keep track of the candidate's favorite status.
     */
    private var isFavorite: Boolean = false

    /**
     * Inflates the view for this fragment and binds it using View Binding.
     *
     * @param inflater The LayoutInflater object to inflate the views.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState A Bundle object containing the saved state data.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Sets up the UI components and retrieves candidate data to be displayed.
     *
     * This function is called when the fragment's view is created. It initializes
     * various buttons, such as back, edit, delete, call, SMS, and email buttons.
     * It also fetches the candidate's data and updates the UI accordingly.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved state data of the fragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        setupEditButton()
        setupDeleteButton()
        setupCallButton()
        setupSmsButton()
        setupEmailButton()
        fetchDataToDisplay() // Load candidate details from ViewModel and display them
        setupFavoriteButton()
    }

    /**
     * Sets up the behavior for the back button.
     *
     * When clicked, it navigates back to the home screen.
     */
    private fun setupBackButton() {
        // Handle the back button click to navigate to the home screen
        binding.detailScreenBackButton.setOnClickListener {
            navigateToHomeScreen()
        }
    }

    /**
     * Configures the favorite button to toggle the candidate's favorite status.
     *
     * When clicked, the button updates the favorite status in the database and
     * changes its appearance to reflect whether the candidate is a favorite.
     *
     * Additionally, on a long press, it shows a tooltip explaining the button's functionality.
     */
    private fun setupFavoriteButton() {
        binding.detailScreenFavoriteButton.setOnClickListener {
            // Toggle the favorite status and update the UI and database
            isFavorite = !isFavorite
            updateFavoriteButton(isFavorite)
            updateCandidateInDataBase(isFavorite)
        }

        binding.detailScreenFavoriteButton.setOnLongClickListener {
            Toast.makeText(
                context,
                getString(R.string.detail_screen_favorite_button_toast),
                Toast.LENGTH_SHORT
            ).show()
            true
        }
    }

    /**
     * Updates the appearance of the favorite button based on the candidate's favorite status.
     *
     * @param isFavorite The current favorite status of the candidate.
     */
    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.detailScreenFavoriteButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.icon_favorite_true)
        } else {
            binding.detailScreenFavoriteButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.icon_favorite_false)
        }
    }

    /**
     * Configures the edit button to allow editing the candidate's details.
     *
     * When clicked, it navigates to the add/edit screen if the candidate's data is available.
     * If the data is unavailable, it shows a Toast message informing the user.
     *
     * A long press on the button displays a tooltip describing the button's function.
     */
    private fun setupEditButton() {
        binding.detailScreenEditButton.setOnClickListener {
            val candidate = viewModel.candidateState.value
            candidate?.let {
                navigateToAddOrEditScreen(it)
            } ?: run {
                Toast.makeText(context, "Candidate data is not available.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.detailScreenEditButton.setOnLongClickListener {
            Toast.makeText(
                context, getString(R.string.detail_screen_edit_button_toast), Toast.LENGTH_SHORT
            ).show()
            true
        }
    }

    /**
     * Configures the delete button to remove the candidate.
     *
     * When clicked, it shows a confirmation dialog to ensure the user wants to delete the candidate.
     * A long press on the button displays a tooltip explaining the button's functionality.
     */
    private fun setupDeleteButton() {
        // Set up the delete button with a confirmation dialog
        binding.detailScreenDeleteButton.setOnClickListener {
            deleteConfirmationDialog()
        }
        binding.detailScreenDeleteButton.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.detail_screen_delete_button_toast), Toast.LENGTH_SHORT).show()
            true
        }
    }

    /**
     * Displays a confirmation dialog before deleting a candidate.
     *
     * When the confirm button is clicked, the candidate is deleted from the database, and the user is navigated back to the home screen.
     * When the cancel button is clicked, the dialog is hidden without any action.
     */
    private fun deleteConfirmationDialog() {
        // Show confirmation dialog components
        binding.detailScreenDialogComponent.visibility = View.VISIBLE
        binding.detailScreenScreenDialogTitle.visibility = View.VISIBLE
        binding.detailScreenScreenDialogContent.visibility = View.VISIBLE
        binding.detailScreenScreenDialogConfirmButton.visibility = View.VISIBLE
        binding.detailScreenScreenDialogCancelButton.visibility = View.VISIBLE
        binding.detailScreenScreenDialogConfirmButton.elevation = 50f
        binding.detailScreenScreenDialogCancelButton.elevation = 50f

        // Set up confirm button to delete the candidate and navigate to the home screen
        binding.detailScreenScreenDialogConfirmButton.setOnClickListener {
            deleteCandidateInDataBase()
            navigateToHomeScreen()
        }

        // Set up cancel button to hide the dialog
        binding.detailScreenScreenDialogCancelButton.setOnClickListener {
            binding.detailScreenDialogComponent.visibility = View.GONE
            binding.detailScreenScreenDialogTitle.visibility = View.GONE
            binding.detailScreenScreenDialogContent.visibility = View.GONE
            binding.detailScreenScreenDialogConfirmButton.visibility = View.GONE
            binding.detailScreenScreenDialogCancelButton.visibility = View.GONE
        }
    }

    /**
     * Sets up the call button to dial the candidate's phone number.
     *
     * If the candidate has a valid phone number, the phone app is launched to make the call.
     * Otherwise, a toast message is displayed to inform the user that the phone number is unavailable.
     */
    private fun setupCallButton() {
        // Handle the "Call" button
        binding.detailScreenCallButton.setOnClickListener {
            val numberToCall = viewModel.candidateState.value?.phoneNumber
            if (!numberToCall.isNullOrEmpty()) {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$numberToCall")
                }
                startActivity(dialIntent)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.detail_screen_unavailable_phone_number_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Sets up the SMS button to send an SMS to the candidate's phone number.
     *
     * If the candidate has a valid phone number, the messaging app is launched.
     * Otherwise, a toast message is displayed to inform the user that the phone number is unavailable.
     */
    private fun setupSmsButton() {
        // Handle the "SMS" button
        binding.detailScreenSmsButton.setOnClickListener {
            val numberToSendSms = viewModel.candidateState.value?.phoneNumber
            if (!numberToSendSms.isNullOrEmpty()) {
                val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$numberToSendSms")
                }
                startActivity(smsIntent)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.detail_screen_unavailable_phone_number_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Sets up the email button to send an email to the candidate's email address.
     *
     * If the candidate has a valid email address, the email app is launched.
     * If no email apps are found or the email address is unavailable, a toast message is displayed.
     */
    private fun setupEmailButton() {
        // Handle the "Email" button
        binding.detailScreenEmailButton.setOnClickListener {
            val emailToSendMessage = viewModel.candidateState.value?.emailAddress
            if (!emailToSendMessage.isNullOrEmpty()) {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$emailToSendMessage") // Uri pour les emails
                }
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        getString(R.string.detail_screen_no_email_app_found_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.detail_screen_unavailable_email_address_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Fetches data to display on the detail screen by combining multiple flows.
     *
     * Combines the candidate's state, exchange rate message, and converted salary.
     * Updates the UI with the combined data once all of it has been collected.
     */
    private fun fetchDataToDisplay() {
        // Combine multiple flows to update the UI when all data has been collected
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.candidateState,
                viewModel.exchangeRateMessage,
                viewModel.convertedSalary
            ) { candidate, message, convertedSalary ->
                Triple(candidate, message, convertedSalary)  // Combine results into a Triple
            }.collect { (candidate, message, convertedSalary) ->
                // Update the UI when all data is available
                candidate?.let {
                    val convertedSalaryToDisplay =
                        String.format(Locale.getDefault(), "%.2f", convertedSalary)
                    updateUIWithCandidateDetails(
                        it,
                        message,
                        convertedSalaryToDisplay
                    )  // Update with candidate details
                }
            }
        }
    }

    /**
     * Updates the UI with the details of the candidate.
     *
     * This function displays the candidate's name, birthday, salary, converted salary, and other information.
     * It also uses Glide to load the candidate's photo and updates the favorite status button.
     *
     * @param candidate The candidate object containing all the details to display.
     * @param message The message to show as a toast (usually currency exchange rate info).
     * @param convertedSalary The converted salary of the candidate in the desired currency.
     */
    private fun updateUIWithCandidateDetails(
        candidate: Candidate,
        message: String,
        convertedSalary: String
    ) {
        // Set the candidate's name
        binding.detailScreenScreenName.text =
            getString(R.string.detail_screen_top_bar_candidate_name, candidate.firstName, candidate.lastName)

        // Display birthday details with age
        binding.detailScreenBirthdayDetails.text = displayBirthdayDetails(candidate.dateOfBirthStr)

        // Set the expected salary
        binding.detailScreenSalary.text =
            getString(R.string.candidate_salary, candidate.expectedSalary)

        // Display the converted salary
        binding.detailScreenConvertedSalary.text =
            getString(R.string.converted_salary, convertedSalary)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

        // Display additional candidate information note
        binding.detailScreenNote.text = candidate.informationNote

        // Load the candidate's photo using Glide, with a default avatar if not available
        Glide.with(binding.root.context)
            .load(candidate.photo)
            .error(R.drawable.default_avatar)
            .into(binding.detailScreenAvatar)

        // Update the favorite status button based on the candidate's status
        isFavorite = candidate.isFavorite
        updateFavoriteButton(isFavorite)
    }

    /**
     * Updates the favorite status of the candidate in the database.
     *
     * This function creates a new candidate object with the updated favorite status and
     * sends it to the ViewModel for updating the database.
     *
     * @param isFavorite Whether the candidate should be marked as favorite.
     */
    private fun updateCandidateInDataBase(isFavorite: Boolean) {
        val updatedCandidate = viewModel.candidateState.value!!.copy(isFavorite = isFavorite)
        // Update the candidate in the ViewModel
        viewModel.updateCandidate(updatedCandidate)
        Toast.makeText(
            context,
            "candidate updated in DB : message to delete",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Deletes the candidate from the database.
     *
     * This function retrieves the current candidate and requests its deletion via the ViewModel.
     */
    private fun deleteCandidateInDataBase() {
        val candidateToDelete = viewModel.candidateState.value!!.copy()
        // Request the ViewModel to delete the candidate
        viewModel.deleteCandidate(candidateToDelete)
    }

    /**
     * Navigates back to the HomeScreenFragment.
     *
     * This function handles fragment transactions to return the user to the home screen.
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
     * Navigates to the AddOrEditScreenFragment with the selected candidate.
     *
     * This function handles fragment transactions to open the candidate edit screen,
     * passing the selected candidate in a Bundle.
     *
     * @param candidate The candidate to be edited.
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

        // Replace the current fragment with the AddOrEditScreenFragment
        fragmentTransaction.replace(R.id.fragment_container, addOrEditScreenFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /**
     * Displays the candidate's birthdate and age.
     *
     * This function formats the candidate's birthdate into a readable string and calculates the age based on the current date.
     *
     * @param birthDateTimestamp The timestamp representing the candidate's birthdate.
     * @return A string with the formatted birthdate and the candidate's age.
     */
    private fun displayBirthdayDetails(birthDateTimestamp: Long): String {
        // Retrieve the date format from resources based on the selected language
        val dateFormat = binding.root.context.getString(R.string.detail_screen_date_of_birth_format)

        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val date = Date(birthDateTimestamp)
        val formattedBirthDate: String = formatter.format(date)

        // Convert the timestamp to a LocalDate
        val birthDate = Date(birthDateTimestamp).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val currentDate = LocalDate.now()

        // Calculate the age of the candidate
        val age: Int = Period.between(birthDate, currentDate).years

        // Return the formatted birthdate and the age in years
        return "$formattedBirthDate ($age ${binding.root.context.getString(R.string.detail_screen_birthday_format)})"
    }

    /**
     * Called when the fragment's view is destroyed.
     *
     * This function sets the binding to null to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}