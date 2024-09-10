package com.openclassrooms.p8_vitesse.domain.model

import com.openclassrooms.p8_vitesse.data.entity.CandidateDto

data class Candidate(
    var id: Long,
    var photo: String,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var emailAddress: String,
    var dateOfBirth: Long,
    var expectedSalary: Int,
    var informationNote: String,
    var isFavorite: Boolean
) {
    companion object {
        fun fromDto(dto: CandidateDto): Candidate {
            return Candidate(
                id = dto.id,
                photo = dto.photoUrl,
                firstName = dto.firstName,
                lastName = dto.lastName,
                phoneNumber = dto.phoneNumber,
                emailAddress = dto.emailAddress,
                dateOfBirth = dto.dateOfBirth,
                expectedSalary = dto.expectedSalary,
                informationNote = dto.informationNote,
                isFavorite = dto.isFavorite
            )
        }
    }

    fun toDto(): CandidateDto {
        return CandidateDto(
            id = id,
            photoUrl = photo,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            emailAddress = emailAddress,
            dateOfBirth = dateOfBirth,
            expectedSalary = expectedSalary,
            informationNote = informationNote,
            isFavorite = isFavorite
        )
    }
}