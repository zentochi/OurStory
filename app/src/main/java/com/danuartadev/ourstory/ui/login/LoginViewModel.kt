package com.danuartadev.ourstory.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danuartadev.ourstory.data.UserRepository
import com.danuartadev.ourstory.data.pref.UserModel
import com.danuartadev.ourstory.data.remote.response.ErrorResponse
import com.danuartadev.ourstory.data.remote.response.LoginResponse
import com.danuartadev.ourstory.ui.signup.SignupViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
            Log.d(TAG, "Token saved: ${user.token}")
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}