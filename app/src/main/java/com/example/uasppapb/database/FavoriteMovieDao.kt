package com.example.uasppapb.database

import androidx.room.Dao
import androidx.room.Query
import com.example.uasppapb.database.FavoriteMovie

// Anotasi @Dao menandai bahwa kelas ini adalah Data Access Object (DAO) yang digunakan untuk mengakses data dalam tabel favorite_movies di database Room
@Dao
interface FavoriteMovieDao {
    // Menggunakan anotasi @Query untuk menjalankan perintah SQL SELECT dan mendapatkan semua data dari tabel favorite_movies
    @Query("SELECT * FROM favorite_movies")
    fun getAllFavoriteMovies(): List<FavoriteMovie>

    // Menggunakan anotasi @Insert untuk melakukan operasi INSERT data ke dalam tabel favorite_movies
    // Jika terjadi konflik pada primary key (movieId), data yang sudah ada akan digantikan (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(movie: FavoriteMovie)

    // Menggunakan anotasi @Query untuk menjalankan perintah SQL DELETE untuk menghapus data dari tabel favorite_movies berdasarkan movieId
    suspend fun deleteFavoriteMovie(movieId: String)
}
