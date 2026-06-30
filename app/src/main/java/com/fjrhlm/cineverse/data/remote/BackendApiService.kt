package com.fjrhlm.cineverse.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendApiService {
    @POST("auth/v1/signup")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
