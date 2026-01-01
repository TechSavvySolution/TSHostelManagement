package com.techsavvy.tshostelmanagement.ui.hosteller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.techsavvy.tshostelmanagement.data.models.HostellerRoom
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AssignHostellerViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    var success by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun assignRoom(uid: String, roomId: String, notes: String?) {
        loading = true
        error = null
        success = false

        val hostellerRoom = HostellerRoom(
            uid = uid,
            roomId = roomId,
            notes = notes
        )

        try {
            firestoreRepository.assignHostellerRoom(hostellerRoom)
            success = true
            loading = false
        } catch (e: Exception) {
            error = e.message
            loading = false
        }
    }
}
