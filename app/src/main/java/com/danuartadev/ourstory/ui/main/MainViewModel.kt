package com.danuartadev.ourstory.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.danuartadev.ourstory.data.UserRepository
import com.danuartadev.ourstory.data.pref.UserModel
import com.danuartadev.ourstory.data.remote.response.ErrorResponse
import com.danuartadev.ourstory.data.remote.response.StoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException


class MainViewModel(private val repository: UserRepository) : ViewModel() {
//    private val _isLoadingStories = MutableLiveData<Boolean>()
//    val isLoadingStories: LiveData<Boolean> = _isLoadingStories
    val isLoadingStories: MutableLiveData<Boolean> = MutableLiveData()

    private val _getStoriesResponse = MutableLiveData<StoryResponse>()
    val getStoriesResponse: LiveData<StoryResponse> = _getStoriesResponse

    val errorMessage = MutableLiveData<String?>()

    fun getStories() {
        viewModelScope.launch {
            try {
                isLoadingStories.postValue(true)
                // Log the token to verify if it's retrieved correctly
                val response = repository.getStories()
                Log.d(TAG, "Data Fetched: ${response.listStory.size} items")
                _getStoriesResponse.postValue(response)
                isLoadingStories.postValue(false)
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessageText = errorBody.message
                errorMessage.postValue(errorMessageText)
                isLoadingStories.postValue(false)
            } catch (E: Exception) {
                val errorMessageText = "An error occurred: ${E.message}"
                errorMessage.postValue(errorMessageText)
                isLoadingStories.postValue(false)
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}
