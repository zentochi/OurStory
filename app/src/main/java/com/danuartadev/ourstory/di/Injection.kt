package com.danuartadev.ourstory.di

import android.content.Context
import com.danuartadev.ourstory.data.UserRepository
import com.danuartadev.ourstory.data.pref.UserPreference
import com.danuartadev.ourstory.data.pref.dataStore
import com.danuartadev.ourstory.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking {
            pref.getSession().first()
        }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}