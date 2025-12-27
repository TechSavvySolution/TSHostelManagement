package com.techsavvy.tshostelmanagement.data.models

data class Building(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val floors: List<Floor> = emptyList()
)
