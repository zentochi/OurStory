package com.danuartadev.ourstory.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danuartadev.ourstory.data.UserRepository
import com.danuartadev.ourstory.data.remote.response.ErrorResponse
import com.danuartadev.ourstory.data.remote.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    private val _isLoadingSignup = MutableLiveData<Boolean>()
    val isLoadingSignup: LiveData<Boolean> = _isLoadingSignup

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    val registerStatus: MutableLiveData<Boolean> = MutableLiveData()

    val errorMessage = MutableLiveData<String?>()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoadingSignup.value = true
                val response = repository.register(name, email, password)
                _registerResponse.value = response
                _isLoadingSignup.value = false
                registerStatus.postValue(true)
                Log.d(TAG, "response: $response")
            } catch (e: HttpException) {
                _isLoadingSignup.value = false
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessageText = errorBody.message
                Log.d(TAG, "HttpException: $errorMessageText")
                errorMessage.postValue(errorMessageText)
                registerStatus.postValue(false)
            } catch (e: Exception) {
                _isLoadingSignup.value = false
                registerStatus.postValue(false)
                Log.d(TAG, "Exception: An error occurred.")
                errorMessage.postValue("An error occurred.")
            }
        }
    }

    companion object {
        private const val TAG = "SignupViewModel"
    }
}

