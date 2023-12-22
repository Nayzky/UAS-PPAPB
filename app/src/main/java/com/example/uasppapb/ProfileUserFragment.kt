package com.example.uasppapb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uasppapb.HomeUserActivity
import com.example.uasppapb.ListMovieFragment
import com.example.uasppapb.PrefManager
import com.example.uasppapb.R
import com.example.uasppapb.databinding.FragmentProfileUserBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentProfileUserBinding
    private lateinit var homeUserActivity: HomeUserActivity
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_user, container, false)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Inisialisasi homeUserActivity saat Fragment terhubung dengan Activity
        if (context is HomeUserActivity) {
            homeUserActivity = context
        } else {
            throw IllegalStateException("Activity harus merupakan instance dari HomeUserActivity")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileUserBinding.bind(view)

        // Inisialisasi prefManager saat Fragment terhubung dengan Activity
        prefManager = PrefManager.getInstance(requireContext())

        var username = prefManager.getUsername()
        var email = prefManager.getEmail()


        with(binding) {
            txtUser.setText(username)
            txtEmail.setText(email)

            btnLogout.setOnClickListener {
                // Call the logOut() method in HomeUserActivity
                homeUserActivity.logOut()

                // Display a toast message
                showToast("Logout successful")

            }
        }
    }
    // Function to show a toast message
    private fun showToast(message: String) {
        val context: Context = requireContext()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    class HomeUserActivity : AppCompatActivity() {
        // ...

        fun logOut() {
            // Lakukan proses logout di sini
            // Misalnya, membersihkan preferensi, menghapus token, atau tindakan logout lainnya

            // Setelah proses logout selesai, arahkan kembali ke halaman login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Menutup HomeUserActivity sehingga tidak dapat kembali lagi saat tombol back ditekan
        }
    }

}