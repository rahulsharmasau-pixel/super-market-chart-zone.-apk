package com.marketchartzone.app.ui.community

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.marketchartzone.app.data.model.CommunityPost
import com.marketchartzone.app.data.model.Report
import com.marketchartzone.app.data.repository.MarketRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MarketRepository(application)

    val posts: StateFlow<List<CommunityPost>> = repository.observeCommunityPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var hasAgreedToGuidelines by mutableStateOf(false)
    var isChecked by mutableStateOf(false)
    var newPostContent by mutableStateOf("")

    fun createPost(userId: String, userName: String, userAvatar: String) {
        if (newPostContent.isBlank()) return
        viewModelScope.launch {
            val post = CommunityPost(
                userId = userId,
                userName = userName,
                userAvatar = userAvatar,
                content = newPostContent
            )
            repository.addCommunityPost(post)
            newPostContent = ""
        }
    }

    fun reportPost(reporterId: String, postId: String, reason: String) {
        viewModelScope.launch {
            val report = Report(
                reporterId = reporterId,
                targetId = postId,
                targetType = "CommunityPost",
                reason = reason
            )
            repository.submitReport(report)
        }
    }
}
