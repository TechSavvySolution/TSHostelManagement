package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Room(
    @DocumentId val id: String = "",
    val name: String = "",
    val floorId: String = "",
    val capacity: Int = 0,
)