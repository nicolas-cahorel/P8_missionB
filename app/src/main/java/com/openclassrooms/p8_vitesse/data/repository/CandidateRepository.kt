package com.openclassrooms.p8_vitesse.data.repository

import com.openclassrooms.p8_vitesse.data.dao.CandidateDtoDao
import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.first

/**
 * Repository class for handling operations related to candidates.
 *
 * @property candidateDao The Data Access Object for CandidateDto.
 */
class CandidateRepository(private val candidateDao: CandidateDtoDao) {


    /**
     * Retrieves all candidates from the database.
     *
     * @return A list of filtered candidates.
     */
    suspend fun getCandidates(favorite: Boolean, searchQuery: String?): List<Candidate> {

        return candidateDao.getAllCandidates()
            .filter { if (favorite) it.isFavorite else true}
            .filter { candidateDto ->
                // if search query is null return not filtered candidate list
                searchQuery ?: return@filter true
                val fullName = candidateDto.firstName + " " + candidateDto.lastName
                fullName.contains(searchQuery)
            }
            .map { Candidate.fromDto(it) } // Convert each DTO to a Candidate object
    }



    /**
     * Retrieves a candidate by their ID from the database.
     *
     * @param id The ID of the candidate to be retrieved.
     * @return The candidate with the specified ID, or null if not found.
     */
    suspend fun getCurrentCandidate(id: Long): Candidate? {
        return candidateDao.getCandidateById(id)
            .first() // Collect the first emission of the Flow
            ?.let { Candidate.fromDto(it) } // Convert DTO to a Candidate
    }

    /**
     * Adds a new candidate record to the database.
     *
     * @param candidate The candidate to be added.
     */
    suspend fun addCandidate(candidate: Candidate) {
        candidateDao.insertCandidate(candidate.toDto()) // Convert every Candidate to DTO
    }

    /**
     * Updates an existing candidate record in the database.
     *
     * @param candidate The candidate to be updated.
     */
    suspend fun updateCandidate(candidate: Candidate) {
        candidateDao.updateCandidate(candidate.toDto()) // Convert every Candidate to DTO
    }

    /**
     * Deletes a candidate record from the database.
     *
     * @param candidate The candidate to be deleted.
     */
    suspend fun deleteCandidate(candidate: Candidate) {
        candidate.id.let { id ->
            candidateDao.deleteCandidateById(id) // Delete candidate by ID
        } ?: throw IllegalArgumentException("Candidate ID must not be null for deletion.")
    }

}