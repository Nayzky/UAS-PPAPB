package com.example.uasppapb.database

import android.app.Application
import androidx.room.Room

// Kelas Application adalah kelas turunan yang mewakili aplikasi Android. Kelas ini digunakan untuk menginisialisasi komponen aplikasi saat aplikasi dimulai.
class Myapp : Application() {
    lateinit var database: AppDatabase

    // Metode onCreate() dipanggil ketika aplikasi dimulai. Di sini, kita menginisialisasi database Room menggunakan Room.databaseBuilder.
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "my-database").build()
    }
}
