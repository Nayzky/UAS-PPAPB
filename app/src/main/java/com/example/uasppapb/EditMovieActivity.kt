package com.example.uasppapb

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.uasppapb.databinding.ActivityEditMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditMovieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMovieBinding
    private val imageCollectionRef = FirebaseStorage.getInstance().reference.child("Images")
    private val movieCollectionRef = FirebaseFirestore.getInstance().collection("Movies")
    private var imageUri: Uri? = null
    private var gambarURL = ""
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.addPosterMovie.setImageURI(it)
    }
    private val selectedGenres = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            val id = intent.getStringExtra(HomeAdminActivity.EXTRA_ID)!!
            gambarURL = intent.getStringExtra(HomeAdminActivity.EXTRA_IMAGE)!!
            val nama = intent.getStringExtra(HomeAdminActivity.EXTRA_NAMA)
            val direktor = intent.getStringExtra(HomeAdminActivity.EXTRA_DIREKTOR)
            val story = intent.getStringExtra(HomeAdminActivity.EXTRA_STORY)
            val rate = intent.getStringExtra(HomeAdminActivity.EXTRA_RATING)
            val genre = intent.getStringArrayExtra(HomeAdminActivity.EXTRA_GENRE) ?: emptyArray()

            Picasso.get().load(gambarURL).into(addPosterMovie)
            inputMovieTitle.setText(nama)
            inputMovieDir.setText(direktor)
            inputStoryLine.setText(story)
            inputRating.setText(rate)

            for (i in genre) {
                if (i == "Horror") gHorror.isChecked = true
                if (i == "Thriller") gThriller.isChecked = true
                if (i == "Animasi") gAnimasi.isChecked = true
                if (i == "Dokumenter") gDokumenter.isChecked = true
                if (i == "Komedi") gKomedi.isChecked = true
                if (i == "Aksi") gAksi.isChecked = true
                if (i == "Drama") gDrama.isChecked = true
                if (i == "Romantis") gRomantis.isChecked = true
                if (i == "Misteri") gMisteri.isChecked = true
                if (i == "Keluarga") gKeluarga.isChecked = true
                if (i == "Fiksi Ilmiah") gFiksiilmiah.isChecked = true
                if (i == "Petualangan") gPetualangan.isChecked = true
                if (i == "Fantasi") gFantasi.isChecked = true
                if (i == "Musikal") gMusikal.isChecked = true
            }

            btnSubmitMovie.setOnClickListener {
                addMovieToFirebase(id)
                startActivity(Intent(this@EditMovieActivity, HomeAdminActivity::class.java))
            }

            btnDeleteMovie.setOnClickListener {
                deleteImageFromStorage(gambarURL)
                val movieID = id?.let { it1 -> MovieData(id = it1, "","","", "", listOf(""), "") }
                if (movieID != null) {
                    deleteMovieFromFirebase(movieID)
                }
                startActivity(Intent(this@EditMovieActivity, HomeAdminActivity::class.java))
            }

            addPosterMovie.setOnClickListener {
                resultLauncher.launch("image/*")
            }

            btnBack.setOnClickListener {
                startActivity(Intent(this@EditMovieActivity, HomeAdminActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        // Tambahkan logika yang diperlukan sebelum intent ke HomeAdminActivity
        // Contoh: Simpan data, lakukan validasi, dll.

        // Intent ke HomeAdminActivity
        startActivity(Intent(this@EditMovieActivity, HomeAdminActivity::class.java))

        // Panggil super.onBackPressed() jika ingin menjalankan perilaku default (tutup aktivitas saat tombol back ditekan)
        super.onBackPressed()
    }

    // Fungsi untuk menghapus gambar dari Firebase Storage
    private fun deleteImageFromStorage(imageUrl: String) {
        // Jika URL gambar kosong, keluar dari fungsi
        if (imageUrl.isEmpty()) {
            return
        }

        // Dapatkan referensi ke Firebase Storage
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

        // Hapus gambar dari Firebase Storage
        storageReference.delete().addOnSuccessListener {
            // Hapus berhasil
            Toast.makeText(this@EditMovieActivity, "Image Deleted Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // Gagal menghapus
            Toast.makeText(this@EditMovieActivity, "Error Deleting Image: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addMovieToFirebase(documentID: String) {
        with(binding) {
            if (gHorror.isChecked) selectedGenres.add("Horror")
            if (gThriller.isChecked) selectedGenres.add("Thriller")
            if (gAnimasi.isChecked) selectedGenres.add("Animasi")
            if (gDokumenter.isChecked) selectedGenres.add("Dokumenter")
            if (gKomedi.isChecked) selectedGenres.add("Komedi")
            if (gAksi.isChecked) selectedGenres.add("Aksi")
            if (gDrama.isChecked) selectedGenres.add("Drama")
            if (gRomantis.isChecked) selectedGenres.add("Romantis")
            if (gMisteri.isChecked) selectedGenres.add("Misteri")
            if (gKeluarga.isChecked) selectedGenres.add("Keluarga")
            if (gFiksiilmiah.isChecked) selectedGenres.add("Fiksi Ilmiah")
            if (gPetualangan.isChecked) selectedGenres.add("Petualangan")
            if (gFantasi.isChecked) selectedGenres.add("Fantasi")
            if (gMusikal.isChecked) selectedGenres.add("Musikal")

            // Memeriksa jika movie diupdate tanpa mengubah gambar
            if (imageUri == null) withOutImageUri(documentID)

            // Memeriksa jika movie diupdate dengan mengubah gambar
            if (imageUri != null) {
                deleteImageFromStorage(gambarURL)
                withImageUri(documentID)
            }
        }
    }

    private fun deleteMovieFromFirebase(movieData: MovieData) {
        if (movieData.id.isEmpty()) {
            Log.d("MovieEditActivity", "Error delete data empty ID", return)
        }

        movieCollectionRef.document(movieData.id).delete()
            .addOnFailureListener{
                Log.d("MovieEditActivity", "Error delete data movie")
            }
    }

    private fun withImageUri(documentID: String) {
        with(binding) {
            val takeImage = imageCollectionRef.child(System.currentTimeMillis().toString()) // Memberikan nama unik pada gambar yang diupload berupa angka waktu
            imageUri?.let { takeImage.putFile(it).addOnCompleteListener { task -> // Menyimpan gambar pada Firebase Storage
                if (task.isSuccessful) { // Jika gambar sudah berhasil disimpan pada Firebase Storage, maka ...
                    progressBar.visibility = View.VISIBLE
                    takeImage.downloadUrl.addOnSuccessListener { uri -> // Mengambil link URL gambar untuk alamat akses
                        val mapDocument = HashMap<String, Any>() // Membuat kode acak untuk nama document

                        mapDocument["gambar"] = uri.toString()
                        mapDocument["nama"] = inputMovieTitle.text.toString()
                        mapDocument["rating"] = inputRating.text.toString() // Mengambil nilai dari Slider dan mengubahnya menjadi Double
                        mapDocument["direktor"] = inputMovieDir.text.toString()
                        mapDocument["genre"] = selectedGenres
                        mapDocument["storyline"] = inputStoryLine.text.toString()

                        // Menggunakan nilai id sebagai nama dokumen
                        movieCollectionRef.document(documentID).set(mapDocument).addOnCompleteListener { firestoreTask ->
                            if (firestoreTask.isSuccessful) {
                                Toast.makeText(this@EditMovieActivity, "Movie Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@EditMovieActivity, firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                            resetForm()
                            addPosterMovie.setImageResource(R.drawable.upload_image)
                        }
                    }
                    progressBar.visibility = View.GONE
                }
                else {
                    Toast.makeText(this@EditMovieActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                    addPosterMovie.setImageResource(R.drawable.upload_image)
                }
            } }
        }
    }

    private fun withOutImageUri(documentID: String) {
        with(binding) {
            val mapDocument = HashMap<String, Any>() // Membuat kode acak untuk nama document

            mapDocument["gambar"] = gambarURL
            mapDocument["nama"] = inputMovieTitle.text.toString()
            mapDocument["rating"] = inputRating.text.toString() // Mengambil nilai dari Slider dan mengubahnya menjadi Double
            mapDocument["direktor"] = inputMovieDir.text.toString()
            mapDocument["genre"] = selectedGenres
            mapDocument["storyline"] = inputStoryLine.text.toString()

            // Menggunakan nilai id sebagai nama dokumen
            movieCollectionRef.document(documentID).set(mapDocument).addOnCompleteListener { firestoreTask ->
                if (firestoreTask.isSuccessful) {
                    Toast.makeText(this@EditMovieActivity, "Movie Uploaded Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EditMovieActivity, firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()
                }
                resetForm()
                addPosterMovie.setImageResource(R.drawable.upload_image)
            }
            progressBar.visibility = View.GONE
        }
    }


    private fun resetForm() {
        with(binding){
            inputMovieTitle.setText("")
            inputMovieDir.setText("")
            inputStoryLine.setText("") // Perlu ditambahkan checkbox dan slider nantinya
        }
    }
}