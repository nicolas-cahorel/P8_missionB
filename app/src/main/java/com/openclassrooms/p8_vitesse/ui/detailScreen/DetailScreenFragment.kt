package com.openclassrooms.p8_vitesse.ui.detailScreen

import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.FragmentDetailScreenBinding
import com.openclassrooms.p8_vitesse.ui.editScreen.EditScreenFragment
import com.openclassrooms.p8_vitesse.ui.homeScreen.HomeScreenFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import java.util.Locale


class DetailScreenFragment : Fragment() {

    /**
     * The binding for the add screen layout.
     */
    private var _binding: FragmentDetailScreenBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewModel for managing the state of the Home Screen.
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

        // Set up a click listener for the "Add" button.
        binding.buttonDetailScreenBack.setOnClickListener {
            navigateToHomeScreen()
        }

        binding.buttonDetailScreenDelete.setOnClickListener{
            binding.detailScreenDialogComponent.visibility = View.VISIBLE
            binding.displayDetailScreenScreenDialogTitle.visibility = View.VISIBLE
            binding.displayDetailScreenScreenDialogContent.visibility = View.VISIBLE
            binding.buttonDetailScreenScreenDialogConfirm.visibility = View.VISIBLE
            binding.buttonDetailScreenScreenDialogCancel.visibility = View.VISIBLE
            // Optionnel : Ajuster l'élévation par programmation
            binding.buttonDetailScreenScreenDialogConfirm.elevation = 100f
            binding.buttonDetailScreenScreenDialogCancel.elevation = 100f

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


        binding.buttonDetailScreenDelete.setOnLongClickListener{
            Toast.makeText(context, getString(R.string.toast_delete), Toast.LENGTH_SHORT).show()
            true
        }




        // Observe the candidate state and update the UI when data is available
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.candidateState.collect { candidate ->
                if (candidate != null) {
                    // Update the UI with candidate information

                    if (candidate.isFavorite) {
                        binding.buttonDetailScreenFavoriteFalse.visibility = View.GONE
                        binding.buttonDetailScreenFavoriteTrue.visibility = View.VISIBLE
                    } else {
                        binding.buttonDetailScreenFavoriteFalse.visibility = View.VISIBLE
                        binding.buttonDetailScreenFavoriteTrue.visibility = View.GONE
                    }

                    binding.buttonDetailScreenFavoriteTrue.setOnClickListener() {
                        binding.buttonDetailScreenFavoriteFalse.visibility = View.VISIBLE
                        binding.buttonDetailScreenFavoriteTrue.visibility = View.GONE
                        updateCandidate(false)
                    }

                    binding.buttonDetailScreenFavoriteFalse.setOnClickListener() {
                        binding.buttonDetailScreenFavoriteFalse.visibility = View.GONE
                        binding.buttonDetailScreenFavoriteTrue.visibility = View.VISIBLE
                        updateCandidate(true)
                    }

                    binding.buttonDetailScreenFavoriteTrue.setOnLongClickListener() {
                        Toast.makeText(context, getString(R.string.toast_favorite), Toast.LENGTH_SHORT).show()
                        true
                    }
                    binding.buttonDetailScreenFavoriteFalse.setOnLongClickListener() {
                        Toast.makeText(context, getString(R.string.toast_favorite), Toast.LENGTH_SHORT).show()
                        true
                    }

                    binding.buttonDetailScreenEdit.setOnClickListener() {
                        navigateToEditScreen()
                    }

                    binding.buttonDetailScreenEdit.setOnLongClickListener() {
                        Toast.makeText(context, getString(R.string.toast_edit), Toast.LENGTH_SHORT
                        ).show()
                        true
                    }




                    binding.displayDetailScreenScreenName.text = "${candidate.firstName} ${candidate.lastName}"
                    // Load the candidate photo with Glide
                    Glide.with(binding.root.context)
                        .load(candidate.photo)
                        .error(R.drawable.default_avatar)
                        .into(binding.displayDetailScreenAvatar)

//                    binding.displayDetailScreenAvatar.setImageURI(Uri.parse(candidate.photo))
                    binding.displayDetailScreenBirthday.text = displayBirthdayDetails(candidate.dateOfBirthStr)
                    binding.displayDetailScreenSalary.text = "${candidate.expectedSalary} €"
//                    binding.displayDetailScreenConvertedSalary.text = displayConvertedSalary (candidate.expectedSalary)
                    binding.displayDetailScreenNote.text = candidate.informationNote

                }
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
     * Navigate to the EditScreenFragment.
     */
    private fun navigateToEditScreen() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val editScreenFragment = EditScreenFragment()
        fragmentTransaction.replace(R.id.fragment_container, editScreenFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    private fun displayBirthdayDetails(birthDateTimestamp: Long): String {
        // Définir le format du convertisseur de date
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Convertir le timestamp en Date
        val date = Date(birthDateTimestamp)
        // Retourner la date formatée sous forme de String
        val formattedBirthDate: String = formatter.format(date)

        // Convertir le timestamp en LocalDate
        val birthDate = Date(birthDateTimestamp).toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        // Obtenir la date actuelle
        val currentDate = LocalDate.now()
        // Calculer la période entre la date de naissance et la date actuelle
        val age: Int = Period.between(birthDate, currentDate).years

        val birthdayDetailsToDisplay : String = "$formattedBirthDate ($age ${binding.root.context.getString(R.string.years)})"

        return birthdayDetailsToDisplay
    }

    private fun updateCandidate(isFavorite: Boolean) {
        val updatedCandidate = viewModel.candidateState.value!!.copy(
            isFavorite = isFavorite
        )

        // Add the new candidate to the ViewModel
        viewModel.updateCandidate(updatedCandidate)
    }

    private fun deleteCandidate() {
        val candidateToDelete = viewModel.candidateState.value!!.copy()
        // Add the new candidate to the ViewModel
        viewModel.deleteCandidate(candidateToDelete)
    }

//    private fun displayConvertedSalary(salary: Int) : String {
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}