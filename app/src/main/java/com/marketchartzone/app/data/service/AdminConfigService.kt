package com.marketchartzone.app.data.service

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.marketchartzone.app.R
import kotlinx.coroutines.tasks.await

object AdminConfigService {
    private const val COLLECTION_NAME = "admin_config"
    private const val DOCUMENT_ID = "settings"
    private const val FIELD_ADMIN_UID = "adminUid"
    private const val FIELD_INITIALIZED = "initialized"

    private fun getFirestore(context: Context): FirebaseFirestore {
        val dbId = context.getString(R.string.firestore_database_id)
        return FirebaseFirestore.getInstance(dbId)
    }

    suspend fun getAdminUid(context: Context): String? {
        return try {
            val db = getFirestore(context)
            val snapshot = db.collection(COLLECTION_NAME).document(DOCUMENT_ID).get().await()
            if (snapshot.exists()) {
                snapshot.getString(FIELD_ADMIN_UID)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AdminConfigService", "Error getting admin UID", e)
            null
        }
    }

    suspend fun initializeAdmin(context: Context, uid: String): Boolean {
        return try {
            val db = getFirestore(context)
            val docRef = db.collection(COLLECTION_NAME).document(DOCUMENT_ID)
            
            // Run transaction to ensure single-admin permanence and prevent reassignment
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists()) {
                    val existingUid = snapshot.getString(FIELD_ADMIN_UID)
                    val isInitialized = snapshot.getBoolean(FIELD_INITIALIZED) ?: false
                    if (isInitialized && !existingUid.isNullOrBlank()) {
                        // Admin already initialized — strictly forbid reassignment if different UID
                        if (existingUid != uid) {
                            throw IllegalStateException("Admin UID is already established and cannot be reassigned.")
                        }
                        return@runTransaction true
                    }
                }
                // First initialization
                val data = mapOf(
                    FIELD_ADMIN_UID to uid,
                    FIELD_INITIALIZED to true
                )
                transaction.set(docRef, data)
                true
            }.await()
        } catch (e: Exception) {
            Log.e("AdminConfigService", "Failed to initialize or enforce admin UID", e)
            false
        }
    }

    suspend fun isAdmin(context: Context, uid: String): Boolean {
        if (uid.isBlank()) return false
        val adminUid = getAdminUid(context)
        return adminUid == uid
    }
}
