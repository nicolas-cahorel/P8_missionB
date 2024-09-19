package com.openclassrooms.p8_vitesse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing candidate data in the Room database.
 * This interface provides methods for inserting, updating, retrieving, and deleting candidate records.
 */
@Dao
interface CandidateDtoDao {

    /**
     * Inserts or updates a candidate in the database.
     * If a candidate with the same ID already exists, it will be replaced.
     *
     * @param candidate The candidate to insert or update.
     * @return The ID of the inserted or updated candidate.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCandidate(candidate: CandidateDto): Long

    /**
     * Retrieves all candidates from the database.
     *
     * @return A Flow that emits the list of candidates.
     */
    @Query("SELECT * FROM candidate")
    suspend fun getAllCandidates(): List<CandidateDto>

    /**
     * Retrieves a candidate by its ID from the database.
     * This method returns a Flow to allow observing changes to the candidate in real-time.
     *
     * @param id The ID of the candidate to retrieve.
     * @return A Flow that emits the candidate with the specified ID, or null if not found.
     */
    @Query("SELECT * FROM candidate WHERE id = :id")
    fun getCandidateById(id: Long): Flow<CandidateDto?>

    /**
     * Deletes a candidate by its ID from the database.
     *
     * @param id The ID of the candidate to be deleted.
     */
    @Query("DELETE FROM candidate WHERE id = :id")
    suspend fun deleteCandidateById(id: Long)
}