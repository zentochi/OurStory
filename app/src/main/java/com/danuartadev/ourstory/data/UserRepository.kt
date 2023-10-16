package com.danuartadev.ourstory.data

import android.util.Log
import androidx.lifecycle.liveData
import com.danuartadev.ourstory.data.pref.UserModel
import com.danuartadev.ourstory.data.pref.UserPreference
import com.danuartadev.ourstory.data.remote.response.FileUploadResponse
import com.danuartadev.ourstory.data.remote.retrofit.ApiService
import com.danuartadev.ourstory.utils.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


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

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.login(email, password)
            Log.d(TAG, "response: $successResponse")
            val token = successResponse.loginResult?.token ?: ""
            Log.d(TAG, "UserModel status: $email $token isLogin=true")
//            val userModel = UserModel(email, token, true)
//            userPreference.saveSession(userModel)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            Log.d(TAG, "response: ${errorResponse.message}")
            emit(Result.Error(errorResponse.message))
        } catch (e: Exception) {
            Log.e(TAG, "response: ${e.message}")
            emit(Result.Error("An unexpected error occurred. ${e.message}"))
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            Log.d(TAG, "response: $successResponse")
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            Log.d(TAG, "$errorResponse")
            emit(Result.Error(errorResponse.message))
        } catch (e: Exception) {
            Log.e(TAG, "response: ${e.message}")
            emit(Result.Error("An unexpected error occurred. ${e.message}"))
        }
    }

    fun getStories() = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.getStories()
            Log.d(TAG, "$successResponse")
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            Log.d(TAG, "$errorResponse")
            emit(Result.Error(errorResponse.message))
        } catch (e: Exception) {
            Log.e(TAG, "response: ${e.message}")
            emit(Result.Error("An unexpected error occurred. ${e.message}"))
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
            Log.d(TAG, "$successResponse")
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            Log.d(TAG, "$errorResponse")
            emit(Result.Error(errorResponse.message))
        } catch (e: Exception) {
            Log.e(TAG, "response: ${e.message}")
            emit(Result.Error("An unexpected error occurred. ${e.message}"))
        }
    }


    companion object {
        private const val TAG = "UserRepository"
//        @Volatile
//        private var instance: UserRepository? = null
        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
        UserRepository(userPreference, apiService)
//            instance ?: synchronized(this) {
//                instance ?: UserRepository(userPreference, apiService)
//            }.also { instance = it }
    }
}
