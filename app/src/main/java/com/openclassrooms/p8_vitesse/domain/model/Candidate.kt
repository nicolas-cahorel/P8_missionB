package com.openclassrooms.p8_vitesse.domain.model

import android.os.Parcelable
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import kotlinx.parcelize.Parcelize

/**
 * Domain model representing a candidate with various attributes.
 *
 * @property id The unique ID of the candidate.
 * @property photo The binary data of the candidate's photo as a ByteArray.
 * @property firstName The candidate's first name.
 * @property lastName The candidate's last name.
 * @property phoneNumber The candidate's phone number.
 * @property emailAddress The candidate's email address.
 * @property dateOfBirthStr The candidate's date of birth, stored as a timestamp.
 * @property expectedSalary The candidate's expected salary.
 * @property informationNote Additional notes or information about the candidate.
 * @property isFavorite Indicates if the candidate is marked as a favorite.
 */
@Parcelize
data class Candidate(
    var id: Long,
    var photo: ByteArray,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var emailAddress: String,
    var dateOfBirthStr: Long,
    var expectedSalary: Int,
    var informationNote: String,
    var isFavorite: Boolean
) : Parcelable {

    companion object {

        /**
         * Converts a [CandidateDto] to a [Candidate] domain model.
         *
         * @param dto The DTO to convert.
         * @return The corresponding [Candidate] domain model.
         */
        fun fromDto(dto: CandidateDto): Candidate {
            return Candidate(
                id = dto.id,
                photo = dto.photoData,
                firstName = dto.firstName,
                lastName = dto.lastName,
                phoneNumber = dto.phoneNumber,
                emailAddress = dto.emailAddress,
                dateOfBirthStr = dto.dateOfBirth,
                expectedSalary = dto.expectedSalary,
                informationNote = dto.informationNote,
                isFavorite = dto.isFavorite
            )
        }
    }

    /**
     * Converts the [Candidate] domain model to a [CandidateDto].
     *
     * @return The corresponding [CandidateDto].
     */
    fun toDto(): CandidateDto {
        return CandidateDto(
            id = id,
            photoData = photo,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            emailAddress = emailAddress,
            dateOfBirth = dateOfBirthStr,
            expectedSalary = expectedSalary,
            informationNote = informationNote,
            isFavorite = isFavorite
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Candidate

        if (id != other.id) return false
        if (!photo.contentEquals(other.photo)) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (phoneNumber != other.phoneNumber) return false
        if (emailAddress != other.emailAddress) return false
        if (dateOfBirthStr != other.dateOfBirthStr) return false
        if (expectedSalary != other.expectedSalary) return false
        if (informationNote != other.informationNote) return false
        if (isFavorite != other.isFavorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + photo.contentHashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + emailAddress.hashCode()
        result = 31 * result + dateOfBirthStr.hashCode()
        result = 31 * result + expectedSalary
        result = 31 * result + informationNote.hashCode()
        result = 31 * result + isFavorite.hashCode()
        return result
    }
}