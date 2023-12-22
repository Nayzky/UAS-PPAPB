package com.example.uasppapb.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Menggunakan anotasi @Entity untuk menandai bahwa kelas ini merupakan entitas dalam database Room
@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey val movieId: String, // Menandai primary key dari entitas
    val title: String, // Menyimpan judul film
    val imageUrl: String, // Menyimpan URL gambar film
    val rating: Double, // Menyimpan nilai rating film
    val direktor: String, // Menyimpan nama direktur film
    val genre: List<String>, // Menyimpan daftar genre film
    val storyline: String
    // Menambahkan properti lain yang diperlukan sesuai kebutuhan
)
