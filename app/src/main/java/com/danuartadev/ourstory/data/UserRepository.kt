package com.danuartadev.ourstory.data

import android.provider.ContactsContract.CommonDataKinds.Email
import com.danuartadev.ourstory.data.pref.UserModel
import com.danuartadev.ourstory.data.pref.UserPreference
import com.danuartadev.ourstory.data.remote.response.LoginResponse
import com.danuartadev.ourstory.data.remote.response.RegisterResponse
import com.danuartadev.ourstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow


class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
