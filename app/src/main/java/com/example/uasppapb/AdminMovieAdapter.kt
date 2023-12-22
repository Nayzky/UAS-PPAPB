package com.example.uasppapb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uasppapb.databinding.MoviePosterAdminBinding
import com.squareup.picasso.Picasso

class AdminMovieAdapter (private val movieList: List<MovieData>)
    : RecyclerView.Adapter<AdminMovieAdapter.ImagesViewHolder>() {

    var onItemClick: ((MovieData) -> Unit)? = null

    inner class ImagesViewHolder(var binding: MoviePosterAdminBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = MoviePosterAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: AdminMovieAdapter.ImagesViewHolder, position: Int) {
        val currentMovie = movieList[position]

        // Memeriksa apakah path gambar tidak kosong atau null sebelum memuat gambar menggunakan Picasso
        if (currentMovie.gambar.isNotEmpty()) {
            Picasso.get().load(currentMovie.gambar).into(holder.binding.posterMovieAdmin)
        } else {
            // Jika path gambar kosong atau null, Anda dapat menetapkan gambar default atau placeholder
            Picasso.get().load(R.drawable.a).into(holder.binding.posterMovieAdmin)
        }

        // Lanjutkan dengan menetapkan data lainnya ke view lain di holder
        holder.binding.namaMovieAdmin.text = currentMovie.nama

        // Implementasikan logika lainnya seperti handling onClickListener jika diperlukan
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentMovie)
        }
    }
}