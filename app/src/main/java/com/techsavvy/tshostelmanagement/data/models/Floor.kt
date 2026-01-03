package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Floor(
    @DocumentId val id: String = "",
    val name: String = "",
    val blockId: String = "",
    val floorNumber: Int = 0,
    val description: String? = null,
    val alias: String? = null,
)