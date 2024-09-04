package com.openclassrooms.p8_vitesse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing candidate data in the Room database.
 */
@Dao
interface CandidateDtoDao {

    /**
     * Inserts a candidate in the database.
     *
     * @param candidate The candidate to be inserted.
     * @return The ID of the newly inserted candidate.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: CandidateDto): Long

    /**
     * Updates a candidate in the database.
     *
     * @param candidate The candidate to be updated.
     * @return The ID of the newly updated candidate.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCandidate(candidate: CandidateDto): Long

    /**
     * Retrieves all candidates from the database.
     *
     * @return A Flow that emits the list of candidates.
     */
    @Query("SELECT * FROM candidate")
    suspend fun getAllCandidates(): List<CandidateDto>

    /**
     * Retrieves favorites candidates from the database.
     *
     * @return A Flow that emits the list of favorites candidates.
     */
    @Query("SELECT * FROM candidate")
    fun getFavoritesCandidates(): Flow<List<CandidateDto>>

    /**
     * Retrieves all filtered candidates from the database.
     *
     * @return A Flow that emits the list of all filtered candidates.
     */
    @Query("SELECT * FROM candidate")
    fun getAllFilteredCandidates(): Flow<List<CandidateDto>>

    /**
     * Retrieves favorites filtered candidates from the database.
     *
     * @return A Flow that emits the list of favorites filtered candidates.
     */
    @Query("SELECT * FROM candidate")
    fun getFavoritesFilteredCandidates(): Flow<List<CandidateDto>>

    /**
     * Retrieves a candidate by its ID from the database.
     *
     * @param id The ID of the candidate to be retrieved.
     * @return A Flow that emits the candidate with the specified ID.
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
