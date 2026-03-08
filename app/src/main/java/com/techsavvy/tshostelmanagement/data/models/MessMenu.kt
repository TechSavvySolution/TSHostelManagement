package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class DayMenu(
    val breakfast: String = "",
    val lunch: String = "",
    val dinner: String = ""
)

data class MessMenu(
    @DocumentId val id: String = "weekly",
    val monday: DayMenu = DayMenu(),
    val tuesday: DayMenu = DayMenu(),
    val wednesday: DayMenu = DayMenu(),
    val thursday: DayMenu = DayMenu(),
    val friday: DayMenu = DayMenu(),
    val saturday: DayMenu = DayMenu(),
    val sunday: DayMenu = DayMenu()
)
