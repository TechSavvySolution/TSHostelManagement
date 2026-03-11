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

    suspend fun updateUser(user: User) {
        firestore.collection("users")
            .document(user.uid)
            .set(user, com.google.firebase.firestore.SetOptions.merge())
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

                snapshot?.let {
                    val users = it.documents.mapNotNull { doc ->
                        val user = doc.toObject(User::class.java)

                        // Include if deleted is false OR field doesn't exist
                        if (user?.deleted != true) user else null
                    }

                    trySend(users)
                }
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
                val users = it.toObjects(User::class.java).filter { user -> user.deleted != true }
                trySend(users)
            }
        }

        awaitClose {
            result.remove()
        }
    }

    fun getAssignedStaffComplaints(staffUid: String): Flow<List<Complaint>> = callbackFlow {
        val listener = firestore.collection("complaints")
            .whereEqualTo("assignedStaffUid", staffUid)
            .whereEqualTo("deleted", false)
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
                val users = it.toObjects(User::class.java).filter { user -> user.deleted != true }
                trySend(users)
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

                snapshot?.let {
                    val staff = it.documents.mapNotNull { doc ->
                        val user = doc.toObject(User::class.java)

                        // include if deleted is false OR not present
                        if (user?.deleted != true) user else null
                    }

                    trySend(staff)
                }
            }

        awaitClose { listener.remove() }
    }

    fun assignStaffTask(task: StaffTask) {
        firestore.collection("staff_tasks").add(task)
    }

    fun getAllRooms(): Flow<List<Room>> = callbackFlow {
        val listener = firestore.collection("rooms")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val rooms = it.documents.mapNotNull { doc ->
                        val room = doc.toObject(Room::class.java)

                        // exclude only when explicitly deleted
                        if (room?.deleted != true) room else null
                    }

                    trySend(rooms)
                }
            }

        awaitClose { listener.remove() }
    }

    // Get a hosteler's room assignment details (room + floor + block names)
    suspend fun getHostelerRoomInfo(uid: String): Triple<String, String, String>? {
        return try {
            val assignment = firestore.collection("hosteller_room")
                .whereEqualTo("uid", uid)
                .limit(1)
                .get().await()
                .toObjects<HostellerRoom>()
                .firstOrNull() ?: return null

            val room = firestore.collection("rooms").document(assignment.roomId)
                .get().await().toObject<Room>()

            if (room != null) {
                val floor = firestore.collection("floors").document(room.floorId)
                    .get().await().toObject<Floor>()
                val block = firestore.collection("blocks").document(room.blockId)
                    .get().await().toObject<Block>()
                Triple(
                    room.name.ifBlank { "Room ${room.roomNumber}" },
                    floor?.name ?: "",
                    block?.name ?: ""
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Get tasks assigned to a staff member
    fun getStaffTasks(staffUid: String): Flow<List<StaffTask>> = callbackFlow {
        val listener = firestore.collection("staff_tasks")
            .whereEqualTo("staffUid", staffUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects()) }
            }
        awaitClose { listener.remove() }
    }

    // Update staff task status
    suspend fun updateTaskStatus(taskId: String, newStatus: String) {
        firestore.collection("staff_tasks").document(taskId)
            .update("status", newStatus).await()
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

                snapshot?.let {
                    val complaints = it.documents.mapNotNull { doc ->
                        val complaint = doc.toObject(Complaint::class.java)

                        // include if not explicitly deleted
                        if (complaint?.deleted != true) complaint else null
                    }

                    trySend(complaints)
                }
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
            .whereEqualTo("deleted", false)

        if (onlyActive) {
            query = query.whereEqualTo("active", true)
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

    // SOFT DELETE — marks deleted=true instead of removing the document
    suspend fun deleteAnnouncement(id: String) {
        firestore.collection("announcements").document(id)
            .update("deleted", true).await()
    }

    // --- ADMIN COMPLAINT MANAGEMENT METHODS ---

    fun getAllComplaints(): Flow<List<Complaint>> = callbackFlow {
        val listener = firestore.collection("complaints")
            .whereEqualTo("deleted", false)
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

    // SOFT DELETE — marks deleted=true instead of removing the document
    suspend fun deleteComplaint(complaintId: String): Result<Unit> {
        return try {
            firestore.collection("complaints")
                .document(complaintId)
                .update("deleted", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Soft delete a user (staff or hosteler)
    suspend fun softDeleteUser(uid: String) {
        firestore.collection("users").document(uid)
            .update("deleted", true).await()
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

    // --- CHAT MODULE METHODS ---

    fun getMessages(complaintId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(complaintId)
            .collection("messages")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects<ChatMessage>()) }
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(complaintId: String, message: ChatMessage) {
        firestore.collection("chats")
            .document(complaintId)
            .collection("messages")
            .add(message)
            .await()
    }

    // --- MESS MENU MODULE METHODS ---

    fun getMessMenu(): Flow<MessMenu> = callbackFlow {
        val listener = firestore.collection("mess_menu")
            .document("weekly")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val menu = snapshot?.toObject<MessMenu>() ?: MessMenu()
                trySend(menu)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveMessMenu(menu: MessMenu) {
        firestore.collection("mess_menu")
            .document("weekly")
            .set(menu)
            .await()
    }

    // --- FEES MODULE METHODS ---

    suspend fun saveFeeSetting(setting: FeeSetting) {
        firestore.collection("fee_settings").add(setting).await()
    }

    fun getLatestFeeSetting(): Flow<FeeSetting?> = callbackFlow {
        val listener = firestore.collection("fee_settings")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val setting = snapshot?.documents?.firstOrNull()?.toObject<FeeSetting>()
                trySend(setting)
            }
        awaitClose { listener.remove() }
    }

    suspend fun publishFeeToAllHostelers(setting: FeeSetting) {
        val hostelers = firestore.collection("users")
            .whereEqualTo("role", Role.HOSTELER)
            .get().await().toObjects<User>()
        val batch = firestore.batch()
        hostelers.forEach { user ->
            val record = FeeRecord(
                hostelerUid = user.uid,
                hostelerName = user.name,
                semesterName = setting.semesterName,
                amount = setting.amount,
                status = "Unpaid",
                dueDate = setting.dueDate
            )
            val docRef = firestore.collection("fee_records").document()
            batch.set(docRef, record)
        }
        batch.commit().await()
    }

    fun getAllFeeRecords(): Flow<List<FeeRecord>> = callbackFlow {
        val listener = firestore.collection("fee_records")
            .orderBy("semesterName")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects<FeeRecord>()) }
            }
        awaitClose { listener.remove() }
    }

    fun getFeeRecordsForHosteler(uid: String): Flow<List<FeeRecord>> = callbackFlow {
        val listener = firestore.collection("fee_records")
            .whereEqualTo("hostelerUid", uid)
            .orderBy("dueDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects<FeeRecord>()) }
            }
        awaitClose { listener.remove() }
    }

    suspend fun markFeeAsPaid(recordId: String) {
        firestore.collection("fee_records")
            .document(recordId)
            .update("status", "Paid", "paidAt", System.currentTimeMillis())
            .await()
    }

    // --- ABOUT SCREEN METHODS ---

    fun getDevelopers(): Flow<List<Developer>> = callbackFlow {
        val listener = firestore.collection("developers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                snapshot?.let { trySend(it.toObjects(Developer::class.java)) }
            }
        awaitClose { listener.remove() }
    }
}