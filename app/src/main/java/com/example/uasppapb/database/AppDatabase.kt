package com.example.uasppapb.database

import androidx.room.Database
import androidx.room.RoomDatabase

// Mendefinisikan kelas Database menggunakan Room, yang merupakan bagian dari Android Jetpack
@Database(entities = [FavoriteMovie::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // Fungsi abstrak yang memberikan akses ke Dao yang terkait dengan entitas Favorit Movie
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}
