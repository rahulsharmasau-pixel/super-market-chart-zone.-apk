package com.marketchartzone.app.ui.profile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.marketchartzone.app.data.model.UserProfile
import com.marketchartzone.app.data.repository.MarketRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MarketRepository(application)

    var isEditProfileOpen by mutableStateOf(false)
    var isHelpOpen by mutableStateOf(false)
    var isTermsOpen by mutableStateOf(false)
    var isPrivacyOpen by mutableStateOf(false)
    var isDeleteConfirmOpen by mutableStateOf(false)

    var editName by mutableStateOf("")

    fun initEdit(user: UserProfile) {
        editName = user.displayName
    }

    fun updateProfile(user: UserProfile, onComplete: () -> Unit) {
        viewModelScope.launch {
            val updated = user.copy(displayName = editName)
            repository.saveUserProfile(updated)
            isEditProfileOpen = false
            onComplete()
        }
    }

    fun deleteAccount(user: UserProfile, onSignedOut: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUser(user.uid)
            Firebase.auth.signOut()
            onSignedOut()
        }
    }
}
