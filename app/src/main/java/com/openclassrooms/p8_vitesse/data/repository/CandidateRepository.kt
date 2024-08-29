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
     * @return A list of all candidates.
     */
    suspend fun getAllCandidates(): List<Candidate> {
        return candidateDao.getAllCandidates()
            .first() // Collect the first emission of the Flow
            .map { Candidate.fromDto(it) } // Convert every DTO in Candidate
    }

    /**
     * Retrieves favorite candidates from the database.
     *
     * @return A list of favorite candidates.
     */
    suspend fun getFavoritesCandidates(): List<Candidate> {
        return candidateDao.getFavoritesCandidates()
            .first() // Collect the first emission of the Flow
            .filter { it.isFavorite } // Filter only favorite candidates
            .map { Candidate.fromDto(it) } // Convert every DTO to a Candidate
    }

    /**
     * Retrieves all candidates with a search filter (firstname and/or lastname).
     *
     * @return A list of filtered candidates.
     */
    suspend fun getAllFilteredCandidates(searchQuery: String): List<Candidate> {
        return candidateDao.getAllFilteredCandidates()
            .first() // Collect the first emission of the Flow
            .filter {
                it.firstName.contains(searchQuery, true) || it.lastName.contains(
                    searchQuery,
                    true
                )
            } // Filter by name
            .map { Candidate.fromDto(it) } // Convert every DTO to a Candidate
    }

    /**
     * Retrieves favorite candidates with a search filter (firstname and/or lastname).
     *
     * @return A list of favorite filtered candidates.
     */
    suspend fun getFavoritesFilteredCandidates(searchQuery: String): List<Candidate> {
        return candidateDao.getFavoritesFilteredCandidates()
            .first() // Collect the first emission of the Flow
            .filter {
                it.isFavorite && (it.firstName.contains(
                    searchQuery,
                    true
                ) || it.lastName.contains(searchQuery, true))
            } // Filter favorites by name
            .map { Candidate.fromDto(it) } // Convert every DTO to a Candidate
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