package com.openclassrooms.p8_vitesse.ui.addScreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentAddScreenBinding
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the activity result launcher
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val uri = result.data?.data
                    // Handle the image selection result
                    uri?.let {
                        binding.displayAddScreenAvatar.setImageURI(it)
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // Ajout du TextWatcher pour contrôler le format du numéro de téléphone
        binding.inputAddScreenPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Regex pour valider le numéro de téléphone français (10 chiffres)
                val phonePattern = Regex("^\\d{10}$")

                if (!s.isNullOrEmpty() && !phonePattern.matches(s)) {
                    // Si le format est incorrect, afficher une erreur et changer la couleur de la bordure
                    binding.addScreenPhoneComponent.error = getString(R.string.error_invalid_phone)
                    binding.addScreenPhoneComponent.boxStrokeColor = Color.RED
                } else {
                    // Si le format est correct, retirer l'erreur et restaurer la couleur de la bordure par défaut
                    binding.addScreenPhoneComponent.error = null
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                    )?.let {
                        binding.addScreenPhoneComponent.setBoxStrokeColorStateList(
                            it
                        )
                    }
                }
            }
        })


        // Setup TextWatcher for email validation
        binding.inputAddScreenEmail.addTextChangedListener(object : TextWatcher {
            @SuppressLint("PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (!isValidEmail(email)) {
                    // Set error message and change border color to red
                    binding.addScreenEmailComponent.error =
                        getString(R.string.error_invalid_email)
                    binding.addScreenEmailComponent.boxStrokeColor = Color.RED
                } else {
                    // Clear the error and reset border color to default
                    binding.addScreenEmailComponent.error = null

                    // Set the box stroke color to default using ColorStateList from the theme
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
                    )
                        ?.let {
                            binding.addScreenEmailComponent.setBoxStrokeColorStateList(
                                it
                            )
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

        binding.buttonAddScreenValidate.setOnClickListener {
            // Récupérer la date sélectionnée dans le DatePicker
            val day = binding.addScreenDatePicker.dayOfMonth
            val month = binding.addScreenDatePicker.month + 1 // Les mois sont indexés à partir de 0, donc ajouter 1
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

        // Définir la date maximale sur le DatePicker pour empêcher les dates futures
        binding.addScreenDatePicker.maxDate = System.currentTimeMillis()



        // Ajout du TextWatcher pour contrôler le format de la date de naissance
        binding.inputAddScreenBirthday.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                // Regex pour valider le format de la date (dd/MM/yyyy)
                val datePattern = Regex("^\\d{2}/\\d{2}/\\d{4}$")

                if (!s.isNullOrEmpty()) {
                    if (!datePattern.matches(s)) {
                        // Format incorrect, afficher une erreur et changer la couleur de la bordure
                        binding.addScreenBirthdayComponent2.error = getString(R.string.error_invalid_birthday_format)
                        binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                    } else {
                        // Vérification si la date est dans le futur
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val inputDate: Date = formatter.parse(s.toString())!!
                        val currentDate = Date()

                        if (inputDate.after(currentDate)) {
                            // Date dans le futur, afficher une erreur
                            binding.addScreenBirthdayComponent2.error = getString(R.string.error_future_date)
                            binding.addScreenBirthdayComponent2.boxStrokeColor = Color.RED
                        } else {
                            // Date valide, retirer l'erreur et restaurer la couleur de la bordure par défaut
                            binding.addScreenBirthdayComponent2.error = null
                            ContextCompat.getColorStateList(requireContext(), com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color)
                                ?.let {
                                    binding.addScreenBirthdayComponent2.setBoxStrokeColorStateList(
                                        it
                                    )
                                }
                        }
                    }
                } else {
                    // Champ vide, retirer l'erreur
                    binding.addScreenBirthdayComponent2.error = null
                    ContextCompat.getColorStateList(requireContext(), com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color)
                        ?.let {
                            binding.addScreenBirthdayComponent2.setBoxStrokeColorStateList(
                                it
                            )
                        }
                }
            }

        })


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

    // Function to check if email is valid
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
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