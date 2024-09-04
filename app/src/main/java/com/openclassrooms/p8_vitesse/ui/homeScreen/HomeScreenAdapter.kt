package com.openclassrooms.p8_vitesse.ui.homeScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.p8_vitesse.R
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
) :
    RecyclerView.Adapter<HomeScreenAdapter.CandidateViewHolder>() {

    /**
     * ViewHolder for candidate items in the RecyclerView.
     *
     * @property photo The ImageView for displaying the candidate's photo.
     * @property fullName The TextView for displaying the candidate's full name.
     * @property informationNote The TextView for displaying additional information about the candidate.
     */
    inner class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var photo: ImageView = itemView.findViewById(R.id.display_item_candidate_avatar)
        var fullName: TextView = itemView.findViewById(R.id.display_item_candidate_full_name)
        var informationNote: TextView = itemView.findViewById(R.id.display_item_candidate_note)

        /**
         * Binds the candidate data to the views and sets up click listener.
         *
         * @param candidate The candidate data to be bound to the views.
         */
        fun bind(candidate: Candidate) {
            itemView.setOnClickListener {
                onItemClicked(candidate.id)
            }
        }
    }

//    init { }

    /**
     * Creates a new ViewHolder for a candidate item.
     *
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of [CandidateViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_candidate, parent, false)
        return CandidateViewHolder(itemView)
    }

    /**
     * Binds the candidate data to the ViewHolder.
     *
     * @param holder The ViewHolder that will display the candidate data.
     * @param position The position of the candidate in the data set.
     */
    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = candidates[position]

        // Bind candidate data to the views
        holder.bind(candidate)

        // Load candidate photo using Glide
        Glide.with(holder.itemView.context)
            .load(candidate.photo)  // assuming photoUrl is a field in Candidate class containing the URL
            .error(R.drawable.default_avatar) // Optional: Error image if URL fails to load
            .into(holder.photo)  // ImageView in which to load the image

        // Set other fields
        holder.fullName.text = "${candidate.firstName} ${candidate.lastName}"
        holder.informationNote.text = candidate.informationNote
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
    fun updateData(newCandidates: List<Candidate>) {
        this.candidates = newCandidates
        notifyDataSetChanged() // Notify the RecyclerView to refresh the displayed data
    }
}