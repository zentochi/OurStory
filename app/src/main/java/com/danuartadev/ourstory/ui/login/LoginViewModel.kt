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

//    private val _isLoadingLogin = MutableLiveData<Boolean>()
//    val isLoadingLogin: LiveData<Boolean> = _isLoadingLogin

//    private val _result: MutableLiveData<LoginResponse> = MutableLiveData()
//    val result: LiveData<LoginResponse> get() = _result
//    private val  _loginResponse = MutableLiveData<LoginResponse>()
//    val loginResponse: LiveData<LoginResponse> = _loginResponse
//
//    val loginStatus: MutableLiveData<Boolean> = MutableLiveData()
//
//    val errorMessage = MutableLiveData<String?>()
//
//    fun login(email: String, password: String) {
//        viewModelScope.launch {
//            try {
//                _isLoadingLogin.postValue(true)
//                val response = repository.login(email, password)
//                if (!response.error!!) {
//                    val token = response.loginResult?.token
//                    val userModel = UserModel(email, token ?: "missing token lur")
//                    saveSession(userModel)
//                    loginStatus.postValue(true) // Indicate successful login
//                } else {
//                    _isLoadingLogin.postValue(false)
//                    val message = response.message
//                    Log.d(TAG, message.toString())
//                    loginStatus.postValue(false) // Indicate failed login
//                }
//            } catch (e: HttpException) {
//                _isLoadingLogin.postValue(false)
//                val jsonInString = e.response()?.errorBody()?.string()
//                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
//                val errorMessageText = errorBody.message
//                Log.d(TAG, "e: $errorMessageText")
//                errorMessage.postValue(errorMessageText)
//                loginStatus.postValue(false) // Indicate failed login
//            } catch (e: Exception) {
//                _isLoadingLogin.postValue(false)
//                val errorMessageText = "An error occurred."
//                errorMessage.postValue(errorMessageText)
//                loginStatus.postValue(false) // Indicate failed login
//            }
//        }
//    }

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
