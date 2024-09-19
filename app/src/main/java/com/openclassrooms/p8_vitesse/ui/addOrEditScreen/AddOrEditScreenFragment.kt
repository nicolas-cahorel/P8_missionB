package com.openclassrooms.p8_vitesse.ui.addOrEditScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentAddOrEditScreenBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import com.openclassrooms.p8_vitesse.ui.detailScreen.DetailScreenFragment
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fragment responsible for the Add Screen where users can add a new candidate.
 */
class AddOrEditScreenFragment : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001 // Request code for permissions
    }

    /**
     * The binding for the add screen layout.
     */
    private var _binding: FragmentAddOrEditScreenBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for managing the state of the add screen.
     */
    private val viewModel: AddOrEditScreenViewModel by viewModel()

    /**
     * Launcher to pick an image from the gallery.
     */
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    /**
     * URL of the selected image. Defaults to a placeholder image.
     */
    private var selectedImageUrl: String =
        "android.resource://com.openclassrooms.p8_vitesse/${R.drawable.default_avatar}"

    /**
     * Validation flags for user input fields.
     */
    private var isFirstNameCorrect: Boolean = false
    private var isLastNameCorrect: Boolean = false
    private var isPhoneCorrect: Boolean = false
    private var isEmailCorrect: Boolean = false
    private var isBirthdayCorrect: Boolean = false

    private var inputErrorCount: Int = 0
    private var incorrectFields: String? = null

    /**
     * Stores the candidate to edit if navigate from DetailScreen.
     */
    private var candidate: Candidate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            candidate = it.getParcelable("candidate")
        }
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddOrEditScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after `onCreateView`. Initializes the image picker launcher and sets up listeners.
     */
    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPickImageLauncher()
        setupAvatarField()
        setupFirstNameField()
        setupLastNameField()
        setupPhoneNumberField()
        setupEmailAddressField()
        setupBirthdayField()
        setupSaveButton()

        candidate?.let {
            setupEditScreen(it)
        } ?: run {
            setupAddScreen()
        }
    }

    private fun setupEditScreen(candidate: Candidate) {
        // Set the top bar title
        binding.addOrEditScreenTopBarTitle.text = getString(R.string.edit_screen_top_bar_title)

// Set up the back button to navigate to the detail screen
        binding.buttonAddScreenBack.setOnClickListener {
            navigateToDetailScreen()
        }

        // Load candidate photo using Glide
        selectedImageUrl = candidate.photo
        Glide.with(binding.root.context)
            .load(selectedImageUrl)
            .error(R.drawable.default_avatar)
            .into(binding.displayAddScreenAvatar)


// Set candidate details to input fields
        binding.inputAddScreenFirstName.setText(candidate.firstName)
        binding.inputAddScreenLastName.setText(candidate.lastName)
        binding.inputAddScreenPhone.setText(candidate.phoneNumber)
        binding.inputAddScreenEmail.setText(candidate.emailAddress)

        // Convert date from timestamp to desired format (if needed)
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(candidate.dateOfBirthStr))
        binding.inputAddScreenBirthday.setText(formattedDate)

        if (candidate.expectedSalary != null)
        binding.inputAddScreenSalary.setText(candidate.expectedSalary.toString())
        binding.inputAddScreenNote.setText(candidate.informationNote)

    }

    private fun setupAddScreen() {
        binding.addOrEditScreenTopBarTitle.text = getString(R.string.add_screen_top_bar_title)
        binding.buttonAddScreenBack.setOnClickListener {
            navigateToHomeScreen()
        }
    }


    /**
     * Sets up the click listener for the "Save" button.
     * Validates the input fields and displays a Snackbar message if there are errors.
     * If all inputs are valid, the candidate is added and the user is navigated to the home screen.
     */
    private fun setupSaveButton() {
        binding.buttonAddScreenSaveButton.setOnClickListener {
            inputErrorCount = 0
            incorrectFields = null

            if (!isFirstNameCorrect) {
                inputErrorCount += 1
                incorrectFields = getString(R.string.input_hint_first_name)
            }
            if (!isLastNameCorrect) {
                inputErrorCount += 1
                incorrectFields += ", " + getString(R.string.input_hint_last_name)
            }
            if (!isPhoneCorrect) {
                inputErrorCount += 1
                incorrectFields += ", " + getString(R.string.input_hint_phone)
            }
            if (!isEmailCorrect) {
                inputErrorCount += 1
                incorrectFields += ", " + getString(R.string.input_hint_email)
            }
            if (!isBirthdayCorrect) {
                inputErrorCount += 1
                incorrectFields += ", " + getString(R.string.display_title_detail_screen_birthday)
            }
            if (inputErrorCount == 0) {
                addOrEditCandidate()
                navigateToHomeScreen()
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.button_validate_error_message) + " : " + incorrectFields,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Sets up the birthday field, including the calendar button, date picker, and validation.
     * Initializes listeners for the date picker buttons and the birthday input field.
     */
    private fun setupBirthdayField() {
        // Set up the calendar button click listener to show the DatePicker and related buttons
        binding.buttonAddScreenCalendar.setOnClickListener {
            binding.addScreenDatePicker.visibility = View.VISIBLE
            binding.buttonAddScreenValidate.visibility = View.VISIBLE
            binding.buttonAddScreenCancel.visibility = View.VISIBLE
            binding.addScreenBirthdayHint2.visibility = View.GONE
            binding.addScreenBirthdayComponent2.visibility = View.GONE
            binding.buttonAddScreenCalendar.visibility = View.GONE
        }

        // Set the maximum date on the DatePicker to prevent future dates from being selected
        binding.addScreenDatePicker.maxDate = System.currentTimeMillis()

        // Set up the validate button click listener to process the selected date
        binding.buttonAddScreenValidate.setOnClickListener {
            // Retrieve the selected date from the DatePicker
            val day = binding.addScreenDatePicker.dayOfMonth
            val month = binding.addScreenDatePicker.month + 1 // Months are indexed from 0, so add 1
            val year = binding.addScreenDatePicker.year

            // Format the date as "dd/MM/yyyy"
            val formattedDate = String.format("%02d/%02d/%04d", day, month, year)

            // Update the input field with the selected date
            binding.inputAddScreenBirthday.setText(formattedDate)

            // Hide the DatePicker and the associated buttons
            binding.addScreenDatePicker.visibility = View.GONE
            binding.buttonAddScreenValidate.visibility = View.GONE
            binding.buttonAddScreenCancel.visibility = View.GONE

            // Show the other components
            binding.addScreenBirthdayHint2.visibility = View.VISIBLE
            binding.addScreenBirthdayComponent2.visibility = View.VISIBLE
            binding.buttonAddScreenCalendar.visibility = View.VISIBLE
        }

// Set up the cancel button click listener to hide the DatePicker and related buttons
        binding.buttonAddScreenCancel.setOnClickListener {
            binding.addScreenDatePicker.visibility = View.GONE
            binding.buttonAddScreenValidate.visibility = View.GONE
            binding.buttonAddScreenCancel.visibility = View.GONE
            binding.addScreenBirthdayHint2.visibility = View.VISIBLE
            binding.addScreenBirthdayComponent2.visibility = View.VISIBLE
            binding.buttonAddScreenCalendar.visibility = View.VISIBLE
        }

        // Set up a focus change listener for the birthday input field
        binding.inputAddScreenBirthday.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {  // If the field gains focus
                val preInputText =
                    getString(R.string.input_birthday_pre_input)  // Retrieve the placeholder text
                if (binding.inputAddScreenBirthday.text.toString() == preInputText) {
                    // If the current text matches the placeholder, clear it
                    binding.inputAddScreenBirthday.text?.clear()
                }
            }
        }

        // Add a TextWatcher to validate the date format and value
        binding.inputAddScreenBirthday.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                // Regex to validate the date format (dd/MM/yyyy)
                val datePattern = Regex("^\\d{2}/\\d{2}/\\d{4}$")

                if (s.isNullOrEmpty()) {
                    // If the field is empty, show an error and change the border color
                    isBirthdayCorrect = false
                    binding.addScreenBirthdayComponent2.error =
                        getString(R.string.error_empty_birthday)
                    binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                } else if (!datePattern.matches(s)) {
                    // If the format is incorrect, show an error and change the border color
                    isBirthdayCorrect = false
                    binding.addScreenBirthdayComponent2.error =
                        getString(R.string.error_invalid_format)
                    binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                } else {
                    // Validate if the date actually exists
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatter.isLenient = false // Disable lenient parsing for invalid dates

                    try {
                        val inputDate: Date = formatter.parse(s.toString())!!
                        val currentDate = Date()

                        if (inputDate.after(currentDate)) {
                            // If the date is in the future, show an error
                            isBirthdayCorrect = false
                            binding.addScreenBirthdayComponent2.error =
                                getString(R.string.error_invalid_birthday_future_date)
                            binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                        } else {
                            // If the date is valid, remove the error and restore the default border color
                            isBirthdayCorrect = true
                            binding.addScreenBirthdayComponent2.error = null
                            ContextCompat.getColorStateList(
                                requireContext(),
                                com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                            )?.let {
                                binding.addScreenBirthdayComponent2.setBoxStrokeColorStateList(it)
                            }
                        }
                    } catch (e: ParseException) {
                        // If the date does not exist (e.g., 32/01/1999), show an error
                        isBirthdayCorrect = false
                        binding.addScreenBirthdayComponent2.error =
                            getString(R.string.error_invalid_birthday_date)
                        binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                    }
                }
            }
        })
    }

    /**
     * Sets up the email address field with validation for both focus changes and text input.
     * - Initializes a focus change listener to check if the email field is empty and displays an error if necessary.
     * - Sets up a TextWatcher to validate the email format and update the UI based on the input.
     */
    private fun setupEmailAddressField() {
        // Set up a focus change listener for the email input field
        binding.inputAddScreenEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {  // If the field gains focus
                if (binding.inputAddScreenEmail.text.isNullOrEmpty()) {
                    isEmailCorrect = false
                    binding.addScreenEmailComponent.error =
                        getString(R.string.error_empty_email)
                    binding.addScreenEmailComponent.boxStrokeColor = Color.RED
                }
            }
        }

        // Set up a TextWatcher to validate the email format in real-time
        binding.inputAddScreenEmail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (s.isNullOrEmpty()) {
                    // If the field is empty, mark email as incorrect and display a specific error message
                    isEmailCorrect = false
                    binding.addScreenEmailComponent.error = getString(R.string.error_empty_email)
                    binding.addScreenEmailComponent.boxStrokeColor = Color.RED
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // If the email format is incorrect, mark email as incorrect and display a format error message
                    isEmailCorrect = false
                    binding.addScreenEmailComponent.error = getString(R.string.error_invalid_format)
                    binding.addScreenEmailComponent.boxStrokeColor = Color.RED
                } else {
                    // If the email is valid, remove the error and restore the default border color
                    isEmailCorrect = true
                    binding.addScreenEmailComponent.error = null

                    // Restore the default border color
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                    )?.let {
                        binding.addScreenEmailComponent.setBoxStrokeColorStateList(it)
                    }
                }
            }
        })
    }

    /**
     * Sets up the phone number field with validation for both focus changes and text input.
     * - Initializes a focus change listener to check if the phone number field is empty and displays an error if necessary.
     * - Sets up a TextWatcher to validate the phone number format and update the UI based on the input.
     */
    private fun setupPhoneNumberField() {
        // Set up a focus change listener for the phone number input field
        binding.inputAddScreenPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {  // If the field gains focus
                if (binding.inputAddScreenPhone.text.isNullOrEmpty()) {
                    isPhoneCorrect = false
                    binding.addScreenPhoneComponent.error =
                        getString(R.string.error_empty_phone)
                    binding.addScreenPhoneComponent.boxStrokeColor = Color.RED
                }
            }
        }

        /// Set up a TextWatcher to validate the phone number format in real-time
        binding.inputAddScreenPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                // Regex pattern for validating French phone numbers (10 digits)
                val phonePattern = Regex("^\\d{10}$")

                if (s.isNullOrEmpty()) {
                    // If the field is empty, mark phone number as incorrect and display a specific error message
                    isPhoneCorrect = false
                    binding.addScreenPhoneComponent.error = getString(R.string.error_empty_phone)
                    binding.addScreenPhoneComponent.boxStrokeColor = Color.RED
                } else if (!phonePattern.matches(s)) {
                    // If the format is incorrect, mark phone number as incorrect and display a format error message
                    isPhoneCorrect = false
                    binding.addScreenPhoneComponent.error = getString(R.string.error_invalid_format)
                    binding.addScreenPhoneComponent.boxStrokeColor = Color.RED
                } else {
                    // If the format is correct, remove the error and restore the default border color
                    isPhoneCorrect = true
                    binding.addScreenPhoneComponent.error = null
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                    )?.let {
                        binding.addScreenPhoneComponent.setBoxStrokeColorStateList(it)
                    }
                }
            }
        })
    }

    /**
     * Sets up the last name field with validation for both focus changes and text input.
     * - Initializes a focus change listener to check if the last name field is empty and displays an error if necessary.
     * - Sets up a TextWatcher to validate the last name input and update the UI based on the input.
     */
    private fun setupLastNameField() {
        // Set up a focus change listener for the last name input field
        binding.inputAddScreenLastName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {  // If the field gains focus
                if (binding.inputAddScreenLastName.text.isNullOrEmpty()) {
                    isLastNameCorrect = false
                    binding.addScreenLastNameComponent.error =
                        getString(R.string.error_empty_last_name)
                    binding.addScreenLastNameComponent.boxStrokeColor = Color.RED
                }
            }
        }

        // Set up a TextWatcher to validate the last name input in real-time
        binding.inputAddScreenLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    isLastNameCorrect = false
                    binding.addScreenLastNameComponent.error =
                        getString(R.string.error_empty_last_name)
                    binding.addScreenFirstNameComponent.boxStrokeColor = Color.RED
                } else {
                    isLastNameCorrect = true
                    binding.addScreenLastNameComponent.error = null
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                    )?.let {
                        binding.addScreenLastNameComponent.setBoxStrokeColorStateList(
                            it
                        )
                    }
                }
            }
        })
    }

    /**
     * Sets up the first name field with real-time validation and focus-based error handling.
     * - Adds a TextWatcher to validate the first name input as the user types.
     * - Initializes a focus change listener to display an error if the first name field is empty when it gains focus.
     */
    private fun setupFirstNameField() {
        // Set up a TextWatcher for real-time validation of the first name input
        binding.inputAddScreenFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    // If the input is empty, mark first name as incorrect and display an error message
                    isFirstNameCorrect = false
                    binding.addScreenFirstNameComponent.error =
                        getString(R.string.error_empty_first_name)
                    binding.addScreenFirstNameComponent.boxStrokeColor = Color.RED
                } else {
                    // If the input is not empty, mark first name as correct and clear any error messages
                    isFirstNameCorrect = true
                    binding.addScreenFirstNameComponent.error = null
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                    )?.let {
                        binding.addScreenFirstNameComponent.setBoxStrokeColorStateList(
                            it
                        )
                    }
                }
            }
        })

        // Set up a focus change listener to handle errors when the first name field gains focus
        binding.inputAddScreenFirstName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {  // If the field gains focus
                if (binding.inputAddScreenFirstName.text.isNullOrEmpty()) {
                    // If the field is empty, mark first name as incorrect and display an error message
                    isFirstNameCorrect = false
                    binding.addScreenFirstNameComponent.error =
                        getString(R.string.error_empty_first_name)
                    binding.addScreenFirstNameComponent.boxStrokeColor = Color.RED
                }
            }
        }
    }

    /**
     * Launches an image picker activity to allow the user to select an image from the gallery.
     *
     * This method creates an intent to open the image picker with the action to pick images from
     * the external content URI. It then uses the `pickImageLauncher` to start the activity and
     * handle the result.
     */
    private fun launchImagePicker() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    /**
     * Sets up the avatar field by initializing a click listener for selecting an avatar image.
     * - Checks if media access permissions are granted before launching the image picker.
     * - If permissions are not granted, it requests the necessary permissions.
     */
    private fun setupAvatarField() {
        binding.displayAddScreenAvatar.setOnClickListener {
            // Check permissions before allowing image selection
            if (viewModel.getMediaAccessPermissionStatus())
                launchImagePicker()
            else
                checkPermissions()
        }
    }

    /**
     * Initializes the launcher for picking an image from the gallery.
     * - Registers the activity result launcher to handle the result of the image selection.
     * - Updates the ImageView with the selected image or displays a default image if an error occurs.
     */
    private fun setupPickImageLauncher() {
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // User has selected an image
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        // Obtain the URI of the selected image
                        val imageUri: Uri = data.data!!
                        // Save image url as String
                        selectedImageUrl = imageUri.toString()

                        // Update imageview with the selected image
                        Glide.with(binding.root.context)
                            .load(selectedImageUrl)
                            .error(R.drawable.default_avatar)
                            .into(binding.displayAddScreenAvatar)
                    }
                }
            }
    }



    /**
     * Checks if the permission to read media images is granted.
     * If the permission is not granted, it requests the permission from the user.
     */
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_CODE
            )
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
     * Handles the result of a permission request.
     *
     * @param requestCode The request code provided when the permission was requested.
     * @param permissions An array of requested permissions.
     * @param grantResults An array of grant results for the corresponding permissions.
     *
     * This method is called when the user responds to a permission request.
     * It checks if the request code matches the one for media permissions and updates
     * the ViewModel and UI accordingly based on whether the permission was granted or denied.
     */
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing media
                viewModel.setMediaAccessPermissionStatus(true)
                launchImagePicker()
            } else {
                // Permission denied, show a message to the user
                viewModel.setMediaAccessPermissionStatus(false)
                Toast.makeText(
                    context,
                    getString(R.string.toast_media_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    /**
     * Adds a new candidate or updates an existing one with the provided details.
     *
     * This function collects data from input fields, creates a `Candidate` object, and
     * passes it to the ViewModel to be added to the database or updated. If the candidate
     * has an ID (i.e., it is being edited), the function will update the existing candidate;
     * otherwise, it will create a new candidate.
     */
    private fun addOrEditCandidate() {

        // Default value for expected salary
        var expectedSalary = 0

        // Check if the salary field is not empty or null
        if (binding.inputAddScreenSalary.text.toString().isNotEmpty()) {
            expectedSalary = binding.inputAddScreenSalary.text.toString().trim().toInt()
        }

        // Use existing ID if candidate is not null, otherwise 0 for new candidate
        val candidateId = candidate?.id ?: 0

        // Use existing isFavorite status if candidate is not null, otherwise default to false
        val candidateIsFavorite = candidate?.isFavorite ?: false

        // Create or update the Candidate object
        val candidate = Candidate(
            id = candidateId,
            photo = selectedImageUrl,
            firstName = capitalizeWords(binding.inputAddScreenFirstName.text.toString().trim()),
            lastName = capitalizeWords(binding.inputAddScreenLastName.text.toString().trim()),
            phoneNumber = binding.inputAddScreenPhone.text.toString().trim(),
            emailAddress = binding.inputAddScreenEmail.text.toString().trim(),
            dateOfBirthStr = convertDateToTimestamp(
                binding.inputAddScreenBirthday.text.toString().trim()
            ),
            expectedSalary = expectedSalary,
            informationNote = binding.inputAddScreenNote.text.toString().trim(),
            isFavorite = candidateIsFavorite
        )

        // Add the new candidate to the ViewModel
        viewModel.addOrEditCandidate(candidate)
    }

    /**
     * Converts a date string in the format "dd/MM/yyyy" to a timestamp in milliseconds.
     *
     * @param dateStr The date string to be converted.
     * @return The timestamp representing the date in milliseconds since the Unix epoch.
     */
    private fun convertDateToTimestamp(dateStr: String): Long {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date =
            formatter.parse(dateStr)
        return date!!.time // Return the time in milliseconds since Unix epoch, or 0 if parsing fails
    }

    /**
     * Capitalizes the first letter of each word in the given text.
     *
     * @param text The text where each word's first letter should be capitalized.
     * @return The text with each word's first letter capitalized.
     */
    private fun capitalizeWords(text: String): String {
        return text.split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } } // Join the words with spaces
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