package com.fjrhlm.cineverse.data.remote

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val email: String,
    val password: String,
    val data: RegisterMetadata
)

data class RegisterMetadata(
    val username: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("expires_in") val expiresIn: Int?,
    @SerializedName("refresh_token") val refreshToken: String?,
    val user: SupabaseUser?
)

data class SupabaseUser(
    val id: String,
    val email: String,
    @SerializedName("user_metadata") val userMetadata: UserMetadata?
)

data class UserMetadata(
    val username: String?
)
