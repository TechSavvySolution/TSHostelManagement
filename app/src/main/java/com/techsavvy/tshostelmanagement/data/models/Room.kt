package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Room(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val roomNumber: Int = 0,
    val capacity: Int = 0,
    val floorId: String = "",
    val blockId: String = ""
)
