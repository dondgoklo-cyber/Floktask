package com.taskmanager.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class FirebaseService @Inject constructor() {

    fun getCurrentUserId(): String? =
        FirebaseAuth.getInstance().currentUser?.uid

    fun getTasksRef(userId: String): DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users/$userId/tasks")

    fun getProjectsRef(userId: String): DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users/$userId/projects")
}
