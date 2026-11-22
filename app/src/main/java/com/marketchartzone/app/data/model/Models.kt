package com.marketchartzone.app.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val isAdmin: Boolean = false,
    val isSuspended: Boolean = false,
    @ServerTimestamp val createdAt: Date? = null
)

@Keep
@IgnoreExtraProperties
data class AdminConfig(
    @DocumentId val id: String = "settings",
    val adminUserId: String = "",
    val passwordHash: String = ""
)

@Keep
@IgnoreExtraProperties
data class VipTrade(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    @ServerTimestamp val publishedAt: Date? = null,
    val reactions: Map<String, Int> = emptyMap()
)

@Keep
@IgnoreExtraProperties
data class MarketAnalysis(
    @DocumentId val id: String = "",
    val title: String = "",
    val marketExplanation: String = "",
    val supportLevels: String = "",
    val resistanceLevels: String = "",
    val keyLevels: String = "",
    val imageUrl: String = "",
    @ServerTimestamp val publishedAt: Date? = null,
    val reactions: Map<String, Int> = emptyMap()
)

@Keep
@IgnoreExtraProperties
data class CommunityPost(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val imageUrl: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    val reactions: Map<String, Int> = emptyMap(),
    val replyCount: Int = 0
)

@Keep
@IgnoreExtraProperties
data class CommunityReply(
    @DocumentId val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val content: String = "",
    @ServerTimestamp val createdAt: Date? = null
)

@Keep
@IgnoreExtraProperties
data class Report(
    @DocumentId val id: String = "",
    val reporterId: String = "",
    val targetId: String = "",
    val targetType: String = "",
    val reason: String = "",
    val status: String = "Pending",
    @ServerTimestamp val createdAt: Date? = null
)

@Keep
@IgnoreExtraProperties
data class ContentPage(
    @DocumentId val id: String = "",
    val title: String = "",
    val body: String = "",
    @ServerTimestamp val updatedAt: Date? = null
)
