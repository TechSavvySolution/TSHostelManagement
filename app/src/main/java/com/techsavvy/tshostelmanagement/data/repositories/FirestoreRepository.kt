package com.techsavvy.tshostelmanagement.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.techsavvy.tshostelmanagement.data.models.HostellerRoom
import com.techsavvy.tshostelmanagement.data.models.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveUser(user: User) {
        firestore.collection("users").document(user.uid).set(user)
    }

    fun assignHostellerRoom(hostellerRoom: HostellerRoom) {
        firestore.collection("hosteller_room").add(hostellerRoom)
    }
}
