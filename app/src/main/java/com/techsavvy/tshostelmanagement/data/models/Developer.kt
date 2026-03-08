package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Developer(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val study: String = ""
)
