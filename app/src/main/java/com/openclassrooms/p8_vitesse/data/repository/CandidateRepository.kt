package com.openclassrooms.p8_vitesse.data.repository

import com.openclassrooms.p8_vitesse.data.dao.CandidateDtoDao
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.first

/**
 * Repository class for managing operations related to candidates.
 *
 * This class handles the data transactions between the application and the database.
 *
 * @property candidateDao The Data Access Object (DAO) for managing CandidateDto operations.
 */
class CandidateRepository(private val candidateDao: CandidateDtoDao) {


    /**
     * Retrieves a filtered list of candidates from the database.
     *
     * This method fetches all candidates and applies filters based on the favorite status
     * and an optional search query. If no search query is provided, it returns all candidates.
     *
     * @param favorite A flag indicating whether to filter for favorite candidates only.
     * @param searchQuery An optional string to filter candidates by their full name.
     * @return A list of [Candidate] objects matching the filters.
     */
    suspend fun getCandidates(favorite: Boolean, searchQuery: String?): List<Candidate> {

        return candidateDao.getAllCandidates()
            .filter { if (favorite) it.isFavorite else true } // Filter by favorite status
            .filter { candidateDto ->
                searchQuery?.let {
                    val fullName = "${candidateDto.firstName} ${candidateDto.lastName}"
                    fullName.contains(it, ignoreCase = true) // Case-insensitive search
                } ?: true // If searchQuery is null, skip filtering
            }
            .map { Candidate.fromDto(it) } // Convert each CandidateDto to Candidate
    }


    /**
     * Retrieves a candidate by their ID from the database.
     *
     * This method returns the candidate with the specified ID, or null if not found.
     *
     * @param id The ID of the candidate to retrieve.
     * @return The corresponding [Candidate] object, or null if not found.
     */
    suspend fun getCurrentCandidate(id: Long): Candidate? {
        return candidateDao.getCandidateById(id)
            .first() // Collect the first emission of the Flow
            ?.let { Candidate.fromDto(it) } // // Convert CandidateDto to Candidate
    }

    /**
     * Adds a new candidate or updates an existing one in the database.
     *
     * This method inserts or updates a candidate record based on the ID. If the candidate already
     * exists, the existing record is replaced.
     *
     * @param candidate The [Candidate] to be added or updated.
     */
    suspend fun addOrUpdateCandidate(candidate: Candidate) {
        candidateDao.insertOrUpdateCandidate(candidate.toDto()) // Convert Candidate to CandidateDto
    }

    /**
     * Deletes a candidate record from the database by their ID.
     *
     * This method removes the candidate specified by their ID. If the ID is null, an exception is thrown.
     *
     * @param candidate The [Candidate] to be deleted.
     * @throws IllegalArgumentException If the candidate ID is null.
     */
    suspend fun deleteCandidate(candidate: Candidate) {
        candidate.id.let { id ->
            candidateDao.deleteCandidateById(id) // Delete candidate by ID
        } ?: throw IllegalArgumentException("Candidate ID must not be null for deletion.")
    }
}