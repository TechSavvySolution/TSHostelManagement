package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Block(
    @DocumentId val id: String = "",
    val name: String = "",
    val alias: String? = null,
)