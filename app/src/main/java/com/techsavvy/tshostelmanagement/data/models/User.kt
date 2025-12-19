package com.techsavvy.tshostelmanagement.data.models

data class User (
    val uid :String,
    val email :String,
    val phone:String,
    val password : String,
    //STAFF, HOSTELER, ADMIN
    val role : String,
    val name : String
){
    constructor():this("","","","","","")
}