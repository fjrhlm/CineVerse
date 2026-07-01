package com.fjrhlm.cineverse.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    companion object {
        const val API_KEY = "296877db24b6e9a1416b722995ccec92"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"
    }

    // --- MOVIE ENDPOINTS ---

    @GET("movie/now_playing")
    fun getNowPlaying(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("movie/popular")
    fun getPopular(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("movie/top_rated")
    fun getTopRated(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Call<Movie>

    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): Call<CreditsResponse>

    @GET("movie/{movie_id}/reviews")
    fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int = 1
    ): Call<ReviewsResponse>


    // --- TV SHOWS (SERIES) ENDPOINTS ---

    @GET("tv/popular")
    fun getPopularTv(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("tv/top_rated")
    fun getTopRatedTv(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("tv/{tv_id}")
    fun getTvDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Call<Movie>

    @GET("tv/{tv_id}/credits")
    fun getTvCredits(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): Call<CreditsResponse>

    @GET("tv/{tv_id}/reviews")
    fun getTvReviews(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int = 1
    ): Call<ReviewsResponse>


    // --- COMBINED / SEARCH / DISCOVER ENDPOINTS ---

    @GET("search/multi")
    fun searchMulti(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("discover/movie")
    fun discoverMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("with_genres") genreId: String? = null,
        @Query("with_original_language") originalLanguage: String? = null,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("discover/tv")
    fun discoverTv(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("with_genres") genreId: String? = null,
        @Query("with_original_language") originalLanguage: String? = null,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    @GET("genre/movie/list")
    fun getGenres(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Call<GenreResponse>

    // --- VIDEO ENDPOINTS ---
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): Call<VideoResponse>

    @GET("tv/{tv_id}/videos")
    fun getTvVideos(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): Call<VideoResponse>

    // --- TV SEASON DETAILS ENDPOINTS ---
    @GET("tv/{tv_id}/season/{season_number}")
    fun getTvSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): Call<TvSeasonDetails>
}
