package com.danuartadev.ourstory.data

import androidx.lifecycle.liveData
import com.danuartadev.ourstory.data.pref.UserModel
import com.danuartadev.ourstory.data.pref.UserPreference
import com.danuartadev.ourstory.data.remote.response.FileUploadResponse
import com.danuartadev.ourstory.data.remote.response.LoginResponse
import com.danuartadev.ourstory.data.remote.response.RegisterResponse
import com.danuartadev.ourstory.data.remote.response.StoryResponse
import com.danuartadev.ourstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import java.io.File
import com.danuartadev.ourstory.utils.Result
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException


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

//    suspend fun login(email: String, password: String): LoginResponse {
//        return apiService.login(email, password)
//    }
    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.login(email, password)
            emit(Result.Success(successResponse))
            val token = successResponse.loginResult?.token ?: ""
            val userModel = UserModel(email, token, true)
            userPreference.saveSession(userModel)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

//    suspend fun register(name: String, email: String, password: String): RegisterResponse {
//        return apiService.register(name, email, password)
//    }
    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

//    suspend fun getStories() : StoryResponse {
//        return apiService.getStories()
//    }
    fun getStories() = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.getStories()
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    fun uploadStory(imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadStory(multipartBody, requestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
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
