package com.example.uasppapb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uasppapb.databinding.FragmentRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding:FragmentRegisterBinding
    private val usersCollectionRef = FirebaseFirestore.getInstance().collection("Users")
    //menggunakan binding untuk mengakses elemen-elemen tampilan dalam layout XML dan usersCollectionRef untuk
    // berinteraksi dengan koleksi "Users" di Firestore, seperti menambahkan dokumen baru atau mengambil data pengguna.

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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        with(binding) {
            btnRegister.setOnClickListener {
                //Mengambil teks dari elemen UI dengan ID
                val savedUsername = titleUsername.text.toString().lowercase()
                val savedEmail = titleEmail.text.toString()
                val savedPhone = titlePhone.text.toString()
                val savedPassword = titlePassword.text.toString()

                // Memastikan semua kolom input terisi
                if (savedUsername.isNotEmpty() && savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
                        if (checkbox.isChecked) {
                            // Checkbox dicentang
                            val newUser = Users (
                                username = savedUsername,
                                email = savedEmail,
                                phone = savedPhone,
                                password = savedPassword,
                                role = "user"
                            )
                            addUser(newUser)
                        }
                        else {
                            // Checkbox tidak dicentang
                            Toast.makeText(requireContext(), "Checkbox harus dicentang", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    Toast.makeText(requireContext(), "Kolom kosong tidak diizinkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addUser(user: Users) {
        usersCollectionRef.document(user.username).get()
            .addOnSuccessListener { document ->
                if (document.getString("username") == null) {
                    usersCollectionRef.document(user.username).set(user)
                    resetForm()
                    Toast.makeText(activity, "Berhasil menyimpan data User", Toast.LENGTH_SHORT).show()
                }
                else {
                    // Memastikan username tidak sama dengan user lain
                    Toast.makeText(activity, "Username ${document?.getString("username")} sudah digunakan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("RegisterFragment", "Tidak ada koneksi internet", exception)
                Toast.makeText(activity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resetForm() {
        //menjadi string kosong, sehingga mengosongkan kolom username pada formulir.
        with(binding){
            titleUsername.setText("")
            titleEmail.setText("")
            titlePhone.setText("")
            titlePassword.setText("")
        }
    }
}
