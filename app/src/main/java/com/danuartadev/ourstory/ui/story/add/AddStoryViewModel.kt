package com.danuartadev.ourstory.ui.story.add

import androidx.lifecycle.ViewModel
import com.danuartadev.ourstory.data.UserRepository
import java.io.File

class AddStoryViewModel(private val provideRepository: UserRepository): ViewModel() {
    fun uploadImage(file: File, description: String) = provideRepository.uploadStory(file, description)
}
