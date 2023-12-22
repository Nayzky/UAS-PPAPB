package com.example.uasppapb
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.uasppapb.databinding.ActivityMovieDetailsBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MovieDetails : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var genreAdapter: GenreAdapter
    private var genreList = ArrayList<MovieData>()
    private lateinit var prefManager: PrefManager
    private val usersCollectionRef = FirebaseFirestore.getInstance().collection("Users")
    private val channelId = "TEST_NOTIF"
    private val notifId = 90

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi prefManager sebelum digunakan
        prefManager = PrefManager.getInstance(this@MovieDetails)

        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        with(binding) {
            val id = intent.getStringExtra(ListMovieFragment.EXTRA_ID)
            val gambar = intent.getStringExtra(ListMovieFragment.EXTRA_IMAGE)
            val nama = intent.getStringExtra(ListMovieFragment.EXTRA_NAMA)
            val direktor = intent.getStringExtra(ListMovieFragment.EXTRA_DIREKTOR)
            val rate = intent.getStringExtra(ListMovieFragment.EXTRA_RATING)
            val story = intent.getStringExtra(ListMovieFragment.EXTRA_STORY)
            val genre = intent.getStringArrayExtra(ListMovieFragment.EXTRA_GENRE) ?: emptyArray()

            Picasso.get().load(gambar).into(posterMovie)
            titleMovie.text = nama
            dirMovie.text = direktor
            rateMovie.text = "$rate / 5"
            storyMovie.text = story

            val flexLayoutManager = FlexboxLayoutManager(this@MovieDetails)
            flexLayoutManager.flexDirection = FlexDirection.ROW
            flexLayoutManager.justifyContent = JustifyContent.FLEX_START

            genreRV.setHasFixedSize(true)
            genreRV.layoutManager = flexLayoutManager
            genreAdapter = GenreAdapter(genre)
            genreRV.adapter = genreAdapter

            genreAdapter.onItemClick = {
                Toast.makeText(this@MovieDetails, "${it[0]}", Toast.LENGTH_SHORT).show()
            }

            btnBack.setOnClickListener {
                startActivity(Intent(this@MovieDetails, HomeUserActivity::class.java))
                finish()
            }

            btnsave.setOnClickListener {
                usersCollectionRef.document(prefManager.getUsername()).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val storedFavorite = document.get("favorite") as MutableList<String>? ?: mutableListOf()
                            storedFavorite.add(id.toString())

                            val mapDocument = HashMap<String, Any>()
                            mapDocument["favorite"] = storedFavorite

                            usersCollectionRef.document(prefManager.getUsername()).update(mapDocument).addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    Toast.makeText(this@MovieDetails, "Sucsessfuly Marked $nama", Toast.LENGTH_SHORT).show()
                                    btnsave.visibility = View.GONE
                                    btnunsave.visibility = View.VISIBLE
                                } else {
                                    Toast.makeText(this@MovieDetails, firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("MovieDetailsActivity", "Tidak ada koneksi internet", exception)
                        Toast.makeText(this@MovieDetails, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                    }
            }

            btnunsave.setOnClickListener {
                usersCollectionRef.document(prefManager.getUsername()).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val storedFavorite = document.get("favorite") as MutableList<String>? ?: mutableListOf()

                            if (storedFavorite.contains(id.toString())) {
                                storedFavorite.remove(id.toString())

                                val mapDocument = HashMap<String, Any>()
                                mapDocument["favorite"] = storedFavorite

                                usersCollectionRef.document(prefManager.getUsername()).update(mapDocument)
                                    .addOnCompleteListener { firestoreTask ->
                                        if (firestoreTask.isSuccessful) {
                                            Toast.makeText(this@MovieDetails, "Unsucsessfuly Marked $nama", Toast.LENGTH_SHORT).show()
                                            btnsave.visibility = View.VISIBLE
                                            btnunsave.visibility = View.GONE
                                        } else {
                                            Toast.makeText(this@MovieDetails, firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("MovieDetailsActivity", "Tidak ada koneksi internet", exception)
                        Toast.makeText(this@MovieDetails, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                    }
            }

            moreInfo.setOnClickListener {
                val notifImage = BitmapFactory.decodeResource(resources, R.drawable.pic2)

                val builder = NotificationCompat.Builder(this@MovieDetails, channelId)
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setContentTitle("Hey Cinema User!")
                    .setContentText("Here The Schedules of Movies Special December!!")
                    .setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(notifImage)
                    )
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notifChannel = NotificationChannel(
                        channelId,
                        "Test Notification",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notifManager.createNotificationChannel(notifChannel)
                }

                notifManager.notify(notifId, builder.build())
            }

        }
        }

    }