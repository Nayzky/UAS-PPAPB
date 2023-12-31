package com.example.uasppapb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uasppapb.MovieAdapter
import com.example.uasppapb.MovieData
import com.example.uasppapb.MovieDetails
import com.example.uasppapb.R
import com.example.uasppapb.databinding.FragmentListMovieBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListMovieFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListMovieFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentListMovieBinding
    private lateinit var movieAdapter: MovieAdapter
    private val movieCollectionRef = FirebaseFirestore.getInstance().collection("Movies")
    private var movieList = ArrayList<MovieData>()
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_movie, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListMovieFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val EXTRA_ID = "extra_id"
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_DIREKTOR = "extra_direktur"
        const val EXTRA_RATING = "extra_rating"
        const val EXTRA_STORY = "ektra_story"
        const val EXTRA_GENRE = "extra_genre"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListMovieBinding.bind(view)

        initVars()
        getImages()

        // Inisialisasi prefManager saat Fragment terhubung dengan Activity
        prefManager = PrefManager.getInstance(requireContext())
        var username = prefManager.getUsername()

        with(binding) {
            movieAdapter.onItemClick = {
                startActivity(Intent(activity, MovieDetails::class.java).apply {
                    putExtra(EXTRA_ID, it.id)
                    putExtra(EXTRA_IMAGE, it.gambar)
                    putExtra(EXTRA_NAMA, it.nama)
                    putExtra(EXTRA_DIREKTOR, it.direktor)
                    putExtra(EXTRA_RATING, it.rating)
                    putExtra(EXTRA_STORY, it.storyline)
                    putExtra(EXTRA_GENRE, it.genre.toTypedArray())
                })
            }
            welcome.setText("Hello, " + username + "!")
        }
    }

    private fun initVars() {
        binding.layoutRV.setHasFixedSize(true)
        binding.layoutRV.layoutManager = GridLayoutManager(activity, 2)
        movieAdapter = MovieAdapter(movieList)
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
                    document.getString("rating")?: "",
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