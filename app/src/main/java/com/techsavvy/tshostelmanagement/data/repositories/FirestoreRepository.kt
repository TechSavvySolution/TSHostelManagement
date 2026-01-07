package com.techsavvy.tshostelmanagement.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.HostellerRoom
import com.techsavvy.tshostelmanagement.data.models.Room
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.utils.Role
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun saveUser(user: User) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
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
        val assignedUsers = firestore.collection("hosteller_room").get().await().toObjects<HostellerRoom>();
        var listener = firestore.collection("users")
            .whereEqualTo("role", Role.HOSTELER)

        //users = 100
        //hosteler_room = 50
        //100 * 50 = 500
        //use index instead

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
    fun getAssignedUsers(): Flow<List<User>> = callbackFlow {
        val assignedUsers = firestore.collection("hosteller_room").get().await().toObjects<HostellerRoom>();
        var listener = firestore.collection("users")
            .whereEqualTo("role", Role.HOSTELER)

        //users = 100
        //hosteler_room = 50
        //100 * 50 = 500
        //use index instead

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
}
