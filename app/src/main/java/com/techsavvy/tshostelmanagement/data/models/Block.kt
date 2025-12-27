package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Block(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val alias: String? = null,
    val blockType: String = "Mixed", // Boys, Girls, Mixed
    @ServerTimestamp val createdDate: Date? = null
)
