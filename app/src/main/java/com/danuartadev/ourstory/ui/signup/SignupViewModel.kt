package com.danuartadev.ourstory.ui.signup

import androidx.lifecycle.ViewModel
import com.danuartadev.ourstory.data.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {


    fun register(name: String, email: String, password: String) = repository.register(name, email, password)

    companion object {
        private const val TAG = "SignupViewModel"
    }
}

