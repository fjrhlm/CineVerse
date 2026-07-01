package com.fjrhlm.cineverse.data.api

import com.google.gson.annotations.SerializedName

// Response wrapper for lists of movies/tv shows (Trending, Popular, Top Rated, Search)
data class MovieResponse(
    @SerializedName("results") val results: List<Movie>
)

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int? = null,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int? = null
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

// --- VIDEO / TRAILER MODELS ---
data class VideoResponse(
    @SerializedName("results") val results: List<Video>
)

data class Video(
    @SerializedName("id") val id: String,
    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String,
    @SerializedName("site") val site: String,
    @SerializedName("type") val type: String
)

// --- TV SEASONS & EPISODES MODELS ---
data class TvSeasonDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("episodes") val episodes: List<TvEpisode>
)

data class TvEpisode(
    @SerializedName("id") val id: Int,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?
)
