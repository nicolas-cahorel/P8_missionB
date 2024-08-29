package com.openclassrooms.p8_vitesse.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a candidate entity for the Room database.
 * This class is used to map candidate details into the database.
 *
 * @property id The unique ID of the candidate, generated automatically by Room.
 * @property photoUrl The URL of the candidate's photo.
 * @property firstName The candidate's first name.
 * @property lastName The candidate's last name.
 * @property phoneNumber The candidate's phone number, stored as a String to accommodate various formats.
 * @property emailAddress The candidate's email address.
 * @property dateOfBirth The candidate's date of birth, stored as a Long (timestamp) for better date handling.
 * @property expectedSalary The candidate's expected salary.
 * @property informationNote Additional notes or information about the candidate.
 * @property isFavorite Indicates whether the candidate is marked as a favorite.
 */
@Entity(tableName = "candidate")
data class CandidateDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "photo_url")
    var photoUrl: String,

    @ColumnInfo(name = "first_name")
    var firstName: String,

    @ColumnInfo(name = "last_name")
    var lastName: String,

    @ColumnInfo(name = "phone_number")
    var phoneNumber: String, // Stored as String for better flexibility with various formats.

    @ColumnInfo(name = "email_address")
    var emailAddress: String,

    @ColumnInfo(name = "date_of_birth")
    var dateOfBirth: Long, // Stored as Long (timestamp) for effective date handling.

    @ColumnInfo(name = "expected_salary")
    var expectedSalary: Int,

    @ColumnInfo(name = "information_note")
    var informationNote: String,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean // Indicates if the candidate is marked as a favorite.
)

