package com.openclassrooms.p8_vitesse.ui.addScreen

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
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentAddScreenBinding
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddScreenFragment : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE =
            1001 // Define a constant for permission request code
    }

    /**
     * The binding for the add screen layout.
     */
    private var _binding: FragmentAddScreenBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for managing the state of the Home Screen.
     */
    private val viewModel: AddScreenViewModel by viewModel()

    // Define the image pick launcher
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private var isMediaAccessPermitted: Boolean = false

    // Déclarez `selectedImageUrl` comme une variable de classe pour stocker l'URL de l'image sélectionnée
    private var selectedImageUrl: String =
        "android.resource://com.openclassrooms.p8_vitesse/${R.drawable.default_avatar}"

    private var isFirstNameCorrect: Boolean = false
    private var isLastNameCorrect: Boolean = false
    private var isPhoneCorrect: Boolean = false
    private var isEmailCorrect: Boolean = false
    private var isBirthdayCorrect: Boolean = false

    private var inputErrorCount: Int = 0
    private var incorrectFields: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // L'utilisateur a sélectionné une image
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        // Obtenir l'URI de l'image sélectionnée
                        val imageUri: Uri = data.data!!

                        // Stocker l'URL de l'image en tant que chaîne
                        selectedImageUrl = imageUri.toString()

                        // Mettre à jour l'ImageView avec l'image sélectionnée
//                        binding.displayAddScreenAvatar.setImageURI(imageUri)

                        // Vous pouvez maintenant utiliser `selectedImageUrl` pour enregistrer l'URL de l'image sélectionnée dans la base de données
                    }
                }
            }

        // Set up a click listener for the "Add" button.
        binding.buttonAddScreenBack.setOnClickListener {
            navigateToHomeScreen()
        }

        binding.displayAddScreenAvatar.setOnClickListener {
            // Check permissions before allowing image selection
            isMediaAccessPermitted = viewModel.getMediaAccessPermissionStatus()
            if (isMediaAccessPermitted)
                launchImagePicker()
            else
                checkPermissions()
        }

        binding.inputAddScreenFirstName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {  // Si le champ a le focus
                if (binding.inputAddScreenFirstName.text.isNullOrEmpty()) {
                    isFirstNameCorrect = false
                    binding.addScreenFirstNameComponent.error =
                        getString(R.string.error_empty_first_name)
                    binding.addScreenFirstNameComponent.boxStrokeColor = Color.RED
                }
            }
        }

        binding.inputAddScreenFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    isFirstNameCorrect = false
                    binding.addScreenFirstNameComponent.error =
                        getString(R.string.error_empty_first_name)
                    binding.addScreenFirstNameComponent.boxStrokeColor = Color.RED
                } else {
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

        binding.inputAddScreenLastName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {  // Si le champ a le focus
                if (binding.inputAddScreenLastName.text.isNullOrEmpty()) {
                    isLastNameCorrect = false
                    binding.addScreenLastNameComponent.error =
                        getString(R.string.error_empty_last_name)
                    binding.addScreenLastNameComponent.boxStrokeColor = Color.RED
                }
            }
        }

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

        binding.inputAddScreenPhone.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {  // Si le champ a le focus
                if (binding.inputAddScreenPhone.text.isNullOrEmpty()) {
                    isPhoneCorrect = false
                    binding.addScreenPhoneComponent.error =
                        getString(R.string.error_empty_phone)
                    binding.addScreenPhoneComponent.boxStrokeColor = Color.RED
                }
            }
        }

        // Ajout du TextWatcher pour contrôler le format du numéro de téléphone
        binding.inputAddScreenPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                // Regex pour valider le numéro de téléphone français (10 chiffres)
                val phonePattern = Regex("^\\d{10}$")

                if (s.isNullOrEmpty()) {
                    // Si le champ est vide, afficher une erreur et changer la couleur de la bordure
                    isPhoneCorrect = false
                    binding.addScreenPhoneComponent.error = getString(R.string.error_empty_phone)
                    binding.addScreenPhoneComponent.boxStrokeColor = Color.RED
                } else if (!phonePattern.matches(s)) {
                    // Si le format est incorrect, afficher une erreur et changer la couleur de la bordure
                    isPhoneCorrect = false
                    binding.addScreenPhoneComponent.error = getString(R.string.error_invalid_phone)
                    binding.addScreenPhoneComponent.boxStrokeColor = Color.RED
                } else {
                    // Si le format est correct, retirer l'erreur et restaurer la couleur de la bordure par défaut
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

        binding.inputAddScreenEmail.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {  // Si le champ a le focus
                if (binding.inputAddScreenEmail.text.isNullOrEmpty()) {
                    isEmailCorrect = false
                    binding.addScreenEmailComponent.error =
                        getString(R.string.error_empty_email)
                    binding.addScreenEmailComponent.boxStrokeColor = Color.RED
                }
            }
        }

        // Setup TextWatcher for email validation
        binding.inputAddScreenEmail.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (s.isNullOrEmpty()) {
                    // Si le champ est vide, afficher un message d'erreur spécifique
                    isEmailCorrect = false
                    binding.addScreenEmailComponent.error = getString(R.string.error_empty_email)
                    binding.addScreenEmailComponent.boxStrokeColor = Color.RED
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // Si le format de l'e-mail est incorrect, afficher un message d'erreur différent
                    isEmailCorrect = false
                    binding.addScreenEmailComponent.error = getString(R.string.error_invalid_email)
                    binding.addScreenEmailComponent.boxStrokeColor = Color.RED
                } else {
                    // Si l'e-mail est valide, retirer l'erreur et restaurer la couleur par défaut
                    isEmailCorrect = true
                    binding.addScreenEmailComponent.error = null

                    // Restaurer la couleur par défaut de la bordure
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                    )?.let {
                        binding.addScreenEmailComponent.setBoxStrokeColorStateList(it)
                    }
                }
            }
        })

        binding.buttonAddScreenCalendar.setOnClickListener {
            binding.addScreenDatePicker.visibility = View.VISIBLE
            binding.buttonAddScreenValidate.visibility = View.VISIBLE
            binding.buttonAddScreenCancel.visibility = View.VISIBLE
            binding.addScreenBirthdayHint2.visibility = View.GONE
            binding.addScreenBirthdayComponent2.visibility = View.GONE
            binding.buttonAddScreenCalendar.visibility = View.GONE
        }

        // Définir la date maximale sur le DatePicker pour empêcher les dates futures
        binding.addScreenDatePicker.maxDate = System.currentTimeMillis()

        binding.buttonAddScreenValidate.setOnClickListener {
            // Récupérer la date sélectionnée dans le DatePicker
            val day = binding.addScreenDatePicker.dayOfMonth
            val month =
                binding.addScreenDatePicker.month + 1 // Les mois sont indexés à partir de 0, donc ajouter 1
            val year = binding.addScreenDatePicker.year

            // Formater la date comme "dd/MM/yyyy"
            val formattedDate = String.format("%02d/%02d/%04d", day, month, year)

            // Mettre à jour le champ d'entrée avec la date sélectionnée
            binding.inputAddScreenBirthday.setText(formattedDate)

            // Masquer le DatePicker et les boutons associés
            binding.addScreenDatePicker.visibility = View.GONE
            binding.buttonAddScreenValidate.visibility = View.GONE
            binding.buttonAddScreenCancel.visibility = View.GONE

            // Afficher les autres composants
            binding.addScreenBirthdayHint2.visibility = View.VISIBLE
            binding.addScreenBirthdayComponent2.visibility = View.VISIBLE
            binding.buttonAddScreenCalendar.visibility = View.VISIBLE
        }


        binding.buttonAddScreenCancel.setOnClickListener {
            binding.addScreenDatePicker.visibility = View.GONE
            binding.buttonAddScreenValidate.visibility = View.GONE
            binding.buttonAddScreenCancel.visibility = View.GONE
            binding.addScreenBirthdayHint2.visibility = View.VISIBLE
            binding.addScreenBirthdayComponent2.visibility = View.VISIBLE
            binding.buttonAddScreenCalendar.visibility = View.VISIBLE
        }

        binding.inputAddScreenBirthday.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {  // Si le champ a le focus
                val preInputText = getString(R.string.input_birthday_pre_input)  // Récupère la valeur de "input_birthday_preinput"

                if (binding.inputAddScreenBirthday.text.toString() == preInputText) {
                    // Si le texte actuel correspond à la valeur par défaut, on le vide
                    binding.inputAddScreenBirthday.text?.clear()
                }
            }
        }


        // Ajout du TextWatcher pour contrôler le format de la date de naissance
        binding.inputAddScreenBirthday.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            @SuppressLint("PrivateResource")
            override fun afterTextChanged(s: Editable?) {
                // Regex pour valider le format de la date (dd/MM/yyyy)
                val datePattern = Regex("^\\d{2}/\\d{2}/\\d{4}$")

                if (s.isNullOrEmpty()) {
                    // Champ vide, afficher une erreur et changer la couleur de la bordure
                    isBirthdayCorrect = false
                    binding.addScreenBirthdayComponent2.error = getString(R.string.error_empty_birthday)
                    binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                } else if (!datePattern.matches(s)) {
                    // Format incorrect, afficher une erreur et changer la couleur de la bordure
                    isBirthdayCorrect = false
                    binding.addScreenBirthdayComponent2.error = getString(R.string.error_invalid_birthday_format)
                    binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                } else {
                    // Vérification si la date existe réellement
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatter.isLenient = false // Désactive la tolérance pour des dates invalides

                    try {
                        val inputDate: Date = formatter.parse(s.toString())!!
                        val currentDate = Date()

                        if (inputDate.after(currentDate)) {
                            // Date dans le futur, afficher une erreur
                            isBirthdayCorrect = false
                            binding.addScreenBirthdayComponent2.error = getString(R.string.error_invalid_birthday_future_date)
                            binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                        } else {
                            // Date valide, retirer l'erreur et restaurer la couleur de la bordure par défaut
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
                        // La date n'existe pas (ex: 32/01/1999), afficher une erreur
                        isBirthdayCorrect = false
                        binding.addScreenBirthdayComponent2.error = getString(R.string.error_invalid_birthday_date)
                        binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                    }
                }
            }
        })


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

                // TODO : add candidate to DB + navigate to other screen

            } else {

                Snackbar.make(
                    binding.root, // Assurez-vous que 'binding.root' est une vue de niveau supérieur dans votre layout
                    getString(R.string.button_validate_error_message) + incorrectFields,
                    Snackbar.LENGTH_LONG
                ).show()

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

    private fun launchImagePicker() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

//    private fun fetchInputData(): Triple<String, String, String, String, String, String, String, String> {
//        val photoUrl = selectedImageUrl ?: ""
//        val firstName = binding.inputAddScreenFirstName.text.toString().trim()
//        val lastName = binding.inputAddScreenLastName.text.toString().trim()
//        val phoneNumber = binding.inputAddScreenPhone.text.toString().trim()
//        val emailAddress = binding.inputAddScreenEmail.text.toString().trim()
//        val dateOfBirthStr = binding.inputAddScreenBirthday.text.toString().trim()
//        val expectedSalaryStr = binding.inputAddScreenSalary.text.toString().trim()
//        val informationNote = binding.inputAddScreenNote.text.toString().trim()
//
//        val dateOfBirth = convertDateToTimestamp(dateOfBirthStr)
//        val expectedSalary = expectedSalaryStr.toInt()
//
//        return Triple(
//            photoUrl,
//            firstName,
//            lastName,
//            phoneNumber,
//            emailAddress,
//            dateOfBirth,
//            expectedSalary,
//            informationNote
//        )
//    }


//    private fun addCandidate(
//        photoUrl: String,
//        firstName: String,
//        lastName: String,
//        phoneNumber: String,
//        emailAddress: String,
//        dateOfBirth: String,
//        expectedSalary: String,
//        informationNote: String
//    ) {


//        // Création d'un nouvel objet CandidateDto
//        val newCandidate = CandidateDto(
//            photoUrl = photoUrl,
//            firstName = firstName,
//            lastName = lastName,
//            phoneNumber = phoneNumber,
//            emailAddress = emailAddress,
//            dateOfBirth = dateOfBirth,
//            expectedSalary = expectedSalary,
//            informationNote = informationNote,
//            isFavorite = false
//        )
//
//        // Ajouter le nouveau candidat à la base de données
//        viewModel.addNewCandidate(newCandidate)
//    }




//    private fun convertDateToTimestamp(dateStr: String): Long {
//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        return sdf.parse(dateStr)?.time ?: 0L
//    }


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