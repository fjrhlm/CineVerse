package com.fjrhlm.cineverse.data.api

import com.google.gson.annotations.SerializedName

// Response wrapper for lists of movies/tv shows (Trending, Popular, Top Rated, Search)
data class MovieResponse(
    @SerializedName("results") val results: List<Movie>
)

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?, // Nullable because TV Shows use "name"
    @SerializedName("name") val name: String?,   // Used for TV Shows/Series
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String?, // Nullable because TV Shows use first_air_date
    @SerializedName("first_air_date") val firstAirDate: String?, // Used for TV Shows/Series
    @SerializedName("media_type") val mediaType: String? // "movie" or "tv"
) {
    // Helper fields to get uniform values
    val displayTitle: String
        get() = title ?: name ?: "Unknown Title"

    val displayDate: String
        get() = releaseDate ?: firstAirDate ?: "N/A"
}

// Response wrapper for Genres
data class GenreResponse(
    @SerializedName("genres") val genres: List<Genre>
)

data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

// Response wrapper for Cast Credits
data class CreditsResponse(
    @SerializedName("cast") val cast: List<Cast>
)

data class Cast(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("character") val character: String?,
    @SerializedName("profile_path") val profilePath: String?
)

// Response wrapper for Movie/TV Reviews
data class ReviewsResponse(
    @SerializedName("results") val results: List<Review>
)

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("author") val author: String,
    @SerializedName("author_details") val authorDetails: AuthorDetails?,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val createdAt: String?
)

data class AuthorDetails(
    @SerializedName("username") val username: String?,
    @SerializedName("avatar_path") val avatarPath: String?,
    @SerializedName("rating") val rating: Double?
)
