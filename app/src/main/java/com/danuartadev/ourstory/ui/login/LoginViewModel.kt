package com.danuartadev.ourstory.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danuartadev.ourstory.data.UserRepository
import com.danuartadev.ourstory.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
            Log.d(TAG, "Token saveSession: ${user.token}")
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}