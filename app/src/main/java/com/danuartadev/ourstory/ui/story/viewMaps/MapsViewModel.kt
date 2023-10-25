package com.danuartadev.ourstory.ui.story.viewMaps

import androidx.lifecycle.ViewModel
import com.danuartadev.ourstory.data.UserRepository

class MapsViewModel(private val repository: UserRepository) : ViewModel(){
    fun getStoriesWithLocation() = repository.getStoriesWithLocation()
}