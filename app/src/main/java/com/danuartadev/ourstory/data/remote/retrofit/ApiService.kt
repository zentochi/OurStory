package com.danuartadev.ourstory.data.remote.retrofit

import com.danuartadev.ourstory.data.remote.response.FileUploadResponse
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

interface ApiService {
    @GET("stories")
    suspend fun getStories(): StoryResponse

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
        @Part file: MultipartBody.Part, //photo
        @Part("description") description: RequestBody
    ) : FileUploadResponse
}