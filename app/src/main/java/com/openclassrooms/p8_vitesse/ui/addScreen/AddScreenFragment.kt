package com.openclassrooms.p8_vitesse.ui.addScreen

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentAddScreenBinding
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class AddScreenFragment : Fragment() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001 // Define a constant for permission request code
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
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), PERMISSION_REQUEST_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing media
                viewModel.setMediaAccessPermissionStatus(true)
                launchImagePicker()
            } else {
                // Permission denied, show a message to the user
                viewModel.setMediaAccessPermissionStatus(false)
                Toast.makeText(context,  getString(R.string.toast_media_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchImagePicker () {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
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