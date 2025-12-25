package com.techsavvy.tshostelmanagement.data.models

data class Building(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val floors: List<Floor> = emptyList()
)

data class Floor(
    val id: String = "",
    val number: String = "",
    val rooms: List<Room> = emptyList()
)

data class Room(
    val id: String = "",
    val number: String = "",
    val capacity: Int = 0
)
