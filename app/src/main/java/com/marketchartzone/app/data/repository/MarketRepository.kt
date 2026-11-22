package com.marketchartzone.app.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.marketchartzone.app.R
import com.marketchartzone.app.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MarketRepository(context: Context) {
    private val dbId = context.getString(R.string.firestore_database_id)
    private val db = FirebaseFirestore.getInstance(dbId)

    companion object {
        const val HARDCODED_CREATOR_UID = "rahulsharmasau@gmail.com"
    }

    // Firestore-backed AdminConfig Manager (Strict Single-Admin Enforcement)
    suspend fun getAdminConfig(): AdminConfig? {
        return try {
            val snapshot = db.collection("admin_config").document("settings").get().await()
            if (snapshot.exists()) {
                snapshot.toObject(AdminConfig::class.java)
            } else {
                // Initialize single hardcoded creator admin if not exists
                val defaultAdmin = AdminConfig(
                    id = "settings",
                    adminUserId = HARDCODED_CREATOR_UID,
                    passwordHash = "creator_master_secure"
                )
                db.collection("admin_config").document("settings").set(defaultAdmin).await()
                defaultAdmin
            }
        } catch (e: Exception) {
            Log.e("MarketRepo", "Error getting/initializing admin config", e)
            null
        }
    }

    suspend fun isAuthorizedAdmin(userId: String, email: String?): Boolean {
        val config = getAdminConfig() ?: return userId == HARDCODED_CREATOR_UID || email == HARDCODED_CREATOR_UID
        return userId == config.adminUserId || email == config.adminUserId || email == HARDCODED_CREATOR_UID
    }

    suspend fun verifyAdminLogin(userId: String, passwordHash: String): Boolean {
        val config = getAdminConfig() ?: return false
        // Strictly verify against hardcoded creator or stored single admin config
        return (userId == config.adminUserId || userId == HARDCODED_CREATOR_UID) && 
               (passwordHash == config.passwordHash || passwordHash == "creator_master_secure")
    }

    // User Profile
    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            Log.e("MarketRepo", "Error getting user profile", e)
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            db.collection("users").document(profile.uid).set(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // VIP Trades
    fun observeVipTrades(): Flow<List<VipTrade>> = callbackFlow {
        val listener = db.collection("vip_trades")
            .orderBy("publishedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MarketRepo", "Error observing VIP trades", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val trades = snapshot?.documents?.mapNotNull { it.toObject(VipTrade::class.java) } ?: emptyList()
                trySend(trades)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addVipTrade(trade: VipTrade): Result<Unit> {
        return try {
            val data = mapOf(
                "title" to trade.title,
                "description" to trade.description,
                "imageUrl" to trade.imageUrl,
                "publishedAt" to FieldValue.serverTimestamp(),
                "reactions" to trade.reactions
            )
            db.collection("vip_trades").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVipTrade(id: String): Result<Unit> {
        return try {
            db.collection("vip_trades").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Market Analysis
    fun observeMarketAnalysis(): Flow<List<MarketAnalysis>> = callbackFlow {
        val listener = db.collection("market_analysis")
            .orderBy("publishedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MarketRepo", "Error observing market analysis", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(MarketAnalysis::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addMarketAnalysis(analysis: MarketAnalysis): Result<Unit> {
        return try {
            val data = mapOf(
                "title" to analysis.title,
                "marketExplanation" to analysis.marketExplanation,
                "supportLevels" to analysis.supportLevels,
                "resistanceLevels" to analysis.resistanceLevels,
                "keyLevels" to analysis.keyLevels,
                "imageUrl" to analysis.imageUrl,
                "publishedAt" to FieldValue.serverTimestamp(),
                "reactions" to analysis.reactions
            )
            db.collection("market_analysis").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMarketAnalysis(id: String): Result<Unit> {
        return try {
            db.collection("market_analysis").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Community Posts
    fun observeCommunityPosts(): Flow<List<CommunityPost>> = callbackFlow {
        val listener = db.collection("community_posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { it.toObject(CommunityPost::class.java) } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addCommunityPost(post: CommunityPost): Result<Unit> {
        return try {
            val data = mapOf(
                "userId" to post.userId,
                "userName" to post.userName,
                "userAvatar" to post.userAvatar,
                "content" to post.content,
                "imageUrl" to post.imageUrl,
                "createdAt" to FieldValue.serverTimestamp(),
                "reactions" to post.reactions,
                "replyCount" to 0
            )
            db.collection("community_posts").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCommunityPost(id: String): Result<Unit> {
        return try {
            db.collection("community_posts").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Community Replies
    fun observeReplies(postId: String): Flow<List<CommunityReply>> = callbackFlow {
        val listener = db.collection("community_replies")
            .whereEqualTo("postId", postId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val replies = snapshot?.documents?.mapNotNull { it.toObject(CommunityReply::class.java) } ?: emptyList()
                trySend(replies)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addReply(reply: CommunityReply): Result<Unit> {
        return try {
            val data = mapOf(
                "postId" to reply.postId,
                "userId" to reply.userId,
                "userName" to reply.userName,
                "userAvatar" to reply.userAvatar,
                "content" to reply.content,
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("community_replies").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reports
    suspend fun submitReport(report: Report): Result<Unit> {
        return try {
            val data = mapOf(
                "reporterId" to report.reporterId,
                "targetId" to report.targetId,
                "targetType" to report.targetType,
                "reason" to report.reason,
                "status" to "Pending",
                "createdAt" to FieldValue.serverTimestamp()
            )
            db.collection("reports").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeReports(): Flow<List<Report>> = callbackFlow {
        val listener = db.collection("reports")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val reports = snapshot?.documents?.mapNotNull { it.toObject(Report::class.java) } ?: emptyList()
                trySend(reports)
            }
        awaitClose { listener.remove() }
    }

    suspend fun resolveReport(id: String): Result<Unit> {
        return try {
            db.collection("reports").document(id).update("status", "Resolved").await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Users (Admin)
    fun observeUsers(): Flow<List<UserProfile>> = callbackFlow {
        val listener = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull { it.toObject(UserProfile::class.java) } ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    suspend fun setUserSuspended(uid: String, suspended: Boolean): Result<Unit> {
        return try {
            db.collection("users").document(uid).update("isSuspended", suspended).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(uid: String): Result<Unit> {
        return try {
            db.collection("users").document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
