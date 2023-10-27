package com.danuartadev.ourstory.data.remote.retrofit

import com.danuartadev.ourstory.data.remote.response.FileUploadResponse
import com.danuartadev.ourstory.data.remote.response.ListStoryItem
import com.danuartadev.ourstory.data.remote.response.LoginResponse
import com.danuartadev.ourstory.data.remote.response.RegisterResponse
import com.danuartadev.ourstory.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
//        @Query("page") page: Int = 1,
//        @Query("size") size: Int = 20,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): StoryResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ) : FileUploadResponse
}