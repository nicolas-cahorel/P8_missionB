package com.openclassrooms.p8_vitesse.domain.model

class Candidate (
    var id : Long,
    var photo : String,
    var firstName : String,
    var lastName : String,
    var phoneNumber : Int,
    var emailAdress : String,
    var dateOfBirth : String,
    var expectedSalary : Double,
    var informationNote : String
) {

}