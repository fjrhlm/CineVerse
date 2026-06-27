package com.fjrhlm.cineverse.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cineverse.db"
        private const val DATABASE_VERSION = 2 // Upgraded from 1

        // Table Users
        const val TABLE_USERS = "users"
        const val KEY_USER_ID = "id"
        const val KEY_USER_NAME = "username"
        const val KEY_USER_EMAIL = "email"
        const val KEY_USER_PASSWORD = "password"

        // Table Watchlist
        const val TABLE_WATCHLIST = "watchlist"
        const val KEY_WATCH_ID = "id"
        const val KEY_WATCH_MOVIE_ID = "movie_id"
        const val KEY_WATCH_TITLE = "title"
        const val KEY_WATCH_POSTER = "poster_path"
        const val KEY_WATCH_RATING = "rating"
        const val KEY_WATCH_DATE = "release_date"
        const val KEY_WATCH_MEDIA_TYPE = "media_type" // Added in version 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_EMAIL + " TEXT UNIQUE,"
                + KEY_USER_PASSWORD + " TEXT" + ")")
        
        val createWatchlistTable = ("CREATE TABLE " + TABLE_WATCHLIST + "("
                + KEY_WATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_WATCH_MOVIE_ID + " INTEGER UNIQUE,"
                + KEY_WATCH_TITLE + " TEXT,"
                + KEY_WATCH_POSTER + " TEXT,"
                + KEY_WATCH_RATING + " REAL,"
                + KEY_WATCH_DATE + " TEXT,"
                + KEY_WATCH_MEDIA_TYPE + " TEXT DEFAULT 'movie'" + ")")

        db.execSQL(createUsersTable)
        db.execSQL(createWatchlistTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE $TABLE_WATCHLIST ADD COLUMN $KEY_WATCH_MEDIA_TYPE TEXT DEFAULT 'movie'")
            } catch (e: Exception) {
                // In case column already exists or migration fails, recreate
                db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
                db.execSQL("DROP TABLE IF EXISTS $TABLE_WATCHLIST")
                onCreate(db)
            }
        }
    }

    // --- USER AUTHENTICATION QUERIES ---

    fun registerUser(username: String, email: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_USER_NAME, username)
            put(KEY_USER_EMAIL, email)
            put(KEY_USER_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUserCredentials(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $KEY_USER_EMAIL = ? AND $KEY_USER_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getUsernameByEmail(email: String): String {
        val db = this.readableDatabase
        val query = "SELECT $KEY_USER_NAME FROM $TABLE_USERS WHERE $KEY_USER_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var username = "User"
        if (cursor.moveToFirst()) {
            username = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return username
    }

    // --- WATCHLIST QUERIES ---

    fun addToWatchlist(movieId: Int, title: String, posterPath: String?, rating: Double, releaseDate: String, mediaType: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_WATCH_MOVIE_ID, movieId)
            put(KEY_WATCH_TITLE, title)
            put(KEY_WATCH_POSTER, posterPath)
            put(KEY_WATCH_RATING, rating)
            put(KEY_WATCH_DATE, releaseDate)
            put(KEY_WATCH_MEDIA_TYPE, mediaType)
        }
        val result = db.insertWithOnConflict(TABLE_WATCHLIST, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result != -1L
    }

    fun removeFromWatchlist(movieId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_WATCHLIST, "$KEY_WATCH_MOVIE_ID = ?", arrayOf(movieId.toString()))
        db.close()
        return result > 0
    }

    fun isInWatchlist(movieId: Int): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_WATCHLIST WHERE $KEY_WATCH_MOVIE_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(movieId.toString()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getWatchlistMovies(): List<LocalMovie> {
        val list = ArrayList<LocalMovie>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_WATCHLIST", null)
        
        if (cursor.moveToFirst()) {
            val movieIdIdx = cursor.getColumnIndex(KEY_WATCH_MOVIE_ID)
            val titleIdx = cursor.getColumnIndex(KEY_WATCH_TITLE)
            val posterIdx = cursor.getColumnIndex(KEY_WATCH_POSTER)
            val ratingIdx = cursor.getColumnIndex(KEY_WATCH_RATING)
            val dateIdx = cursor.getColumnIndex(KEY_WATCH_DATE)
            val mediaTypeIdx = cursor.getColumnIndex(KEY_WATCH_MEDIA_TYPE)

            do {
                val movieId = cursor.getInt(movieIdIdx)
                val title = cursor.getString(titleIdx)
                val poster = cursor.getString(posterIdx)
                val rating = cursor.getDouble(ratingIdx)
                val date = cursor.getString(dateIdx)
                // Default to 'movie' if column doesn't exist yet or is null
                val mediaType = if (mediaTypeIdx != -1) cursor.getString(mediaTypeIdx) ?: "movie" else "movie"
                
                list.add(LocalMovie(movieId, title, poster, rating, date, mediaType))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}

// Data class representing movie stored locally (to display in Watchlist adapter)
data class LocalMovie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val mediaType: String
)
