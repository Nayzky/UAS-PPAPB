package com.example.uasppapb

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.uasppapb.databinding.ActivityHomeAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var homeAdminActivity: HomeAdminActivity
    private lateinit var movieAdapter: AdminMovieAdapter
    private val movieCollectionRef = FirebaseFirestore.getInstance().collection("Movies")
    private var movieList = ArrayList<MovieData>()

    companion object{
        const val EXTRA_ID = "extra_id"
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_DIREKTOR = "extra_direktur"
        const val EXTRA_STORY = "ektra_story"
        const val EXTRA_RATING = "ektra_rating"
        const val EXTRA_GENRE = "extra_genre"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVars()
        getImages()

        with(binding) {
            profil.setOnClickListener{
                startActivity(Intent(this@HomeAdminActivity, ProfileAdminActivity::class.java))
            }
            addMovie.setOnClickListener {
                startActivity(Intent(this@HomeAdminActivity, MovieAddActivity::class.java))
            }

            movieAdapter.onItemClick = {
                startActivity(Intent(this@HomeAdminActivity, EditMovieActivity::class.java).apply {
                    putExtra(EXTRA_ID, it.id)
                    putExtra(EXTRA_IMAGE, it.gambar)
                    putExtra(EXTRA_NAMA, it.nama)
                    putExtra(EXTRA_DIREKTOR, it.direktor)
                    putExtra(EXTRA_STORY, it.storyline)
                    putExtra(EXTRA_RATING, it.rating)
                    putExtra(EXTRA_GENRE, it.genre.toTypedArray())
                })
            }
        }
    }
    private fun initVars() {
        binding.layoutRV.setHasFixedSize(true)
        binding.layoutRV.layoutManager = GridLayoutManager(this@HomeAdminActivity,2)
        movieAdapter = AdminMovieAdapter(movieList)
        binding.layoutRV.adapter = movieAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages() {
        movieCollectionRef.get().addOnSuccessListener { querySnapshot ->
            movieList.clear()
            for (document in querySnapshot.documents) {
                val movieData = MovieData(
                    document.id,
                    document.getString("gambar") ?: "",
                    document.getString("nama") ?: "",
                    document.getString("rating") ?: "",
                    document.getString("direktor") ?: "",
                    document.get("genre") as List<String>? ?: listOf(),
                    document.getString("storyline") ?: ""
                )
                movieList.add(movieData)
            }
            movieAdapter.notifyDataSetChanged()
        }
    }
}
