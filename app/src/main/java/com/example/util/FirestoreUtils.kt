package com.example.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException

enum class OperationType {
    CREATE, READ, UPDATE, DELETE, LIST
}

fun handleFirestoreError(error: Exception, operation: OperationType, path: String) {
    val firestoreError = generateSequence(error as Throwable) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()

    val message = when (firestoreError?.code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Permission denied at $path"
        FirebaseFirestoreException.Code.NOT_FOUND -> "Document not found at $path"
        FirebaseFirestoreException.Code.UNAVAILABLE -> "Firestore service unavailable"
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> "Quota exceeded"
        else -> error.localizedMessage ?: "Unknown Firestore error"
    }

    Log.e("FirestoreError", "[$operation] $message", error)
}
