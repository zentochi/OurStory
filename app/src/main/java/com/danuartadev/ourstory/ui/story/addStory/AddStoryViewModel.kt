package com.danuartadev.ourstory.ui.story.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danuartadev.ourstory.data.UserRepository
import com.google.android.gms.maps.model.LatLng
import java.io.File

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {
    fun uploadImage(file: File, description: String, lat: Double?, lon: Double?) = repository.uploadStory(file, description, lat, lon)
}