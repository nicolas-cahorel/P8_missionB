package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.p8_vitesse.R
import com.openclassrooms.p8_vitesse.databinding.ItemCandidateBinding
import com.openclassrooms.p8_vitesse.domain.model.Candidate

/**
 * Adapter for displaying a list of [Candidate] items in a RecyclerView.
 *
 * This adapter binds candidate data to the RecyclerView item views and handles user interactions.
 *
 * @property candidates The list of candidates to be displayed.
 * @property onItemClicked A lambda function to handle item click events, providing the candidate ID.
 */
class HomeScreenAdapter(
    private var candidates: List<Candidate>,
    private val onItemClicked: (Long) -> Unit
) : RecyclerView.Adapter<HomeScreenAdapter.CandidateViewHolder>() {

    /**
     * ViewHolder for candidate items in the RecyclerView.
     *
     * @property binding The view binding for the item layout.
     */
    inner class CandidateViewHolder(private val binding: ItemCandidateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the candidate data to the views and sets up click listener.
         *
         * @param candidate The candidate data to be bound to the views.
         */
        fun bind(candidate: Candidate) {
            // Load candidate photo using Glide
            Glide.with(binding.root.context)
                .load(candidate.photo)
                .error(R.drawable.default_avatar)
                .into(binding.itemCandidateAvatar)

            // Set other fields
            binding.itemCandidateFullName.text = binding.root.context.getString(
                R.string.detail_screen_top_bar_candidate_name,
                candidate.firstName,
                candidate.lastName
            )
            binding.itemCandidateNote.text = candidate.informationNote

            binding.constraintLayout.setOnClickListener {
                onItemClicked(candidate.id)
            }
        }
    }

    /**
     * Creates a new ViewHolder for a candidate item.
     *
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of [CandidateViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding =
            ItemCandidateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CandidateViewHolder(binding)
    }

    /**
     * Binds the candidate data to the ViewHolder.
     *
     * @param holder The ViewHolder that will display the candidate data.
     * @param position The position of the candidate in the data set.
     */
    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = candidates[position]
        holder.bind(candidate)
    }

    /**
     * Returns the total number of candidates in the data set.
     *
     * @return The number of candidates.
     */
    override fun getItemCount() = candidates.size

    /**
     * Updates the list of candidates and notifies the adapter of data changes.
     *
     * @param newCandidates The new list of candidates to display.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCandidates: List<Candidate>) {
        this.candidates = newCandidates
        notifyDataSetChanged()
    }
}