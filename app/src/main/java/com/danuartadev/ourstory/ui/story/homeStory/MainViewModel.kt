package com.danuartadev.ourstory.ui.story.homeStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.danuartadev.ourstory.data.UserRepository
import com.danuartadev.ourstory.data.pref.UserModel
import kotlinx.coroutines.launch


class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStories() = repository.getStories()

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