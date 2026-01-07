package com.techsavvy.tshostelmanagement.data.models

data class HostellerRoom(
    val uid: String,
    val roomId: String,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String? = null
){
    constructor():this("","",true,System.currentTimeMillis(),null)
}
