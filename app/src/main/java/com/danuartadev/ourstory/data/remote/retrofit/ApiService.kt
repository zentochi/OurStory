package com.danuartadev.ourstory.data.remote.retrofit

import com.danuartadev.ourstory.data.remote.response.RegisterResponse
import com.danuartadev.ourstory.data.remote.response.StoryResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

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
}