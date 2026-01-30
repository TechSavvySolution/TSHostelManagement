package com.techsavvy.tshostelmanagement.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.techsavvy.tshostelmanagement.data.models.*
import com.techsavvy.tshostelmanagement.data.utils.Role
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun saveUser(user: User) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

    suspend fun getUser(uid: String): User? {
        return try {
            Log.d("FirestoreRepository", "Fetching user with UID: $uid")
            firestore.collection("users").document(uid).get().await().toObject<User>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun assignHostellerRoom(hostellerRoom: HostellerRoom) {
        firestore.collection("hosteller_room").add(hostellerRoom)
    }

    suspend fun getComplaintById(complaintId: String): Complaint? {
        return try {
            firestore.collection("complaints")
                .document(complaintId)
                .get()
                .await()
                .toObject<Complaint>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getHostelers(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereEqualTo("role", Role.HOSTELER)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects()) }
            }
        awaitClose { listener.remove() }
    }

    fun getUnassignedUsers(): Flow<List<User>> = callbackFlow {
        val assignedUsers = firestore.collection("hosteller_room").get().await().toObjects<HostellerRoom>()
        var listener = firestore.collection("users")
            .whereEqualTo("role", Role.HOSTELER)

        if(assignedUsers.isNotEmpty()) {
            listener = listener.whereNotIn("uid", assignedUsers.map { it.uid })
        }

        val result = listener.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let {
                trySend(it.toObjects())
            }
        }

        awaitClose {
            result.remove()
        }
    }

    fun getAssignedStaffComplaints(staffUid: String): Flow<List<Complaint>> = callbackFlow {
        val listener = firestore.collection("complaints")
            .whereEqualTo("assignedStaffUid", staffUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects<Complaint>()) }
            }
        awaitClose { listener.remove() }
    }

    fun getAssignedUsers(): Flow<List<User>> = callbackFlow {
        val assignedUsers = firestore.collection("hosteller_room").get().await().toObjects<HostellerRoom>()
        var listener = firestore.collection("users")
            .whereEqualTo("role", Role.HOSTELER)

        if(assignedUsers.isNotEmpty()) {
            listener = listener.whereIn("uid", assignedUsers.map { it.uid })
        }

        val result = listener.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let {
                trySend(it.toObjects())
            }
        }

        awaitClose {
            result.remove()
        }
    }

    fun getBlocks(): Flow<List<Block>> = callbackFlow {
        val listener = firestore.collection("blocks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects()) }
            }
        awaitClose { listener.remove() }
    }

    fun getFloors(blockId: String): Flow<List<Floor>> = callbackFlow {
        val listener = firestore.collection("floors").whereEqualTo("blockId", blockId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects()) }
            }
        awaitClose { listener.remove() }
    }

    fun getStaff(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereEqualTo("role", Role.STAFF)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects()) }
            }
        awaitClose { listener.remove() }
    }

    fun assignStaffTask(task: StaffTask) {
        firestore.collection("staff_tasks").add(task)
    }

    fun getRooms(floorId: String): Flow<List<Room>> = callbackFlow {
        val listener = firestore.collection("rooms").whereEqualTo("floorId", floorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects()) }
            }
        awaitClose { listener.remove() }
    }

    fun saveComplaint(complaint: Complaint) {
        firestore.collection("complaints").add(complaint)
    }

    fun getHostelerComplaints(userId: String): Flow<List<Complaint>> = callbackFlow {
        val listener = firestore.collection("complaints")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects<Complaint>()) }
            }
        awaitClose { listener.remove() }
    }

    // --- ANNOUNCEMENT MODULE METHODS ---

    /**
     * Fetches announcements sorted by createdAt on the server.
     * Memory-side sorting by "order" is handled in the ViewModel.
     */
    fun getAnnouncements(onlyActive: Boolean = false): Flow<List<Announcement>> = callbackFlow {
        var query = firestore.collection("announcements")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)

        if (onlyActive) {
            query = query.whereEqualTo("isActive", true)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let { trySend(it.toObjects<Announcement>()) }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveAnnouncement(announcement: Announcement) {
        if (announcement.id.isEmpty()) {
            firestore.collection("announcements").add(announcement).await()
        } else {
            firestore.collection("announcements").document(announcement.id).set(announcement).await()
        }
    }

    suspend fun deleteAnnouncement(id: String) {
        firestore.collection("announcements").document(id).delete().await()
    }

    // --- ADMIN COMPLAINT MANAGEMENT METHODS ---

    fun getAllComplaints(): Flow<List<Complaint>> = callbackFlow {
        val listener = firestore.collection("complaints")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects<Complaint>()) }
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateComplaintStatus(complaintId: String, newStatus: String) {
        firestore.collection("complaints")
            .document(complaintId)
            .update("status", newStatus)
            .await()
    }

    suspend fun deleteComplaint(complaintId: String): Result<Unit> {
        return try {
            firestore.collection("complaints")
                .document(complaintId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignStaff(complaintId: String, staffUid: String, staffName: String, staffPhone: String) {
        try {
            firestore.collection("complaints")
                .document(complaintId)
                .update(
                    "assignedStaffUid", staffUid,
                    "assignedStaffName", staffName,
                    "assignedStaffPhone", staffPhone,
                    "status", "In-Progress"
                )
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}