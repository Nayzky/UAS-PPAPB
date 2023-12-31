package com.example.uasppapb

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uasppapb.databinding.FragmentLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollectionRef = firestore.collection("Users")

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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Inisialisasi mainActivity saat Fragment terhubung dengan Activity
        if (context is MainActivity) {
            mainActivity = context
        } else {
            throw IllegalStateException("Activity harus merupakan instance dari MainActivity")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        // Inisialisasi prefManager saat Fragment terhubung dengan Activity
        prefManager = PrefManager.getInstance(requireContext())

        with(binding){
            btnLogin.setOnClickListener{
                val inputUsername = usernameEditText.text.toString()
                val inputPassword = passwordEditText.text.toString()

                if (inputUsername != "") {
                    // Memastikan username tidak kosong
                    usersCollectionRef.document(inputUsername).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Memastikan dokumen pada firestore ditemukan
                                val storedUsername = document.getString("username") ?: ""
                                val storedPassword = document.getString("password") ?: ""
                                val storedEmail = document.getString("email") ?: ""
                                val storedRole = document.getString("role") ?: ""

                                if (storedUsername == inputUsername && storedPassword == inputPassword) {
                                    // Memastikan validasi autentikasi

                                    prefManager.saveUsername(storedUsername)
                                    prefManager.savePassword(storedPassword)
                                    prefManager.saveEmail(storedEmail)
                                    prefManager.saveRole(storedRole)
                                    prefManager.setLoggedIn(true)
                                    resetForm()

                                    // Menghubungkan ke Activity lain sesuai dengan role-nya
                                    mainActivity.intentToHomeActivity(storedRole)
                                } else {
                                    Toast.makeText(activity, "Data login salah", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // Dokumen tidak ditemukan
                                Toast.makeText(activity, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("LoginFragment", "Tidak ada koneksi internet", exception)
                            Toast.makeText(activity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    Toast.makeText(activity, "Username masih kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resetForm() {
        with(binding){
            usernameEditText.setText("")
            passwordEditText.setText("")
        }
    }
}