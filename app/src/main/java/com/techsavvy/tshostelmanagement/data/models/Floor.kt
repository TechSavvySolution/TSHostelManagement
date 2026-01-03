package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Floor(
    @DocumentId val id: String = "",
    val name: String = "",
    val blockId: String = "",
    val alias: String? = null,
)