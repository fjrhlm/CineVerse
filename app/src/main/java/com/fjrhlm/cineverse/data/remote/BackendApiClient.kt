package com.fjrhlm.cineverse.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BackendApiClient {
    private const val BASE_URL = "https://wpzokcynrevjizxqxzrw.supabase.co/"
    private const val SUPABASE_KEY = "sb_publishable_BBqD2TZlEHaShNCMOyKCzA_7fje56n_"

    val instance: BackendApiService by lazy {
        // Interceptor untuk menyisipkan header wajib Supabase
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer $SUPABASE_KEY")
                    .header("Content-Type", "application/json")
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(BackendApiService::class.java)
    }
}
