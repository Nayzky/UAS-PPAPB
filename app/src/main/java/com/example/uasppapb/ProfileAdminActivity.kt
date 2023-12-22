package com.example.uasppapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.uasppapb.databinding.ActivityProfileAdminBinding

private lateinit var binding: ActivityProfileAdminBinding
private lateinit var homeUserActivity: HomeUserActivity
private lateinit var prefManager: PrefManager

class ProfileAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi prefManager saat Fragment terhubung dengan Activity
        prefManager = PrefManager.getInstance(this@ProfileAdminActivity)

        var username = prefManager.getUsername()
        var email = prefManager.getEmail()

        with(binding) {
            txtUser.setText(username)
            txtEmail.setText(email)

            btnBack.setOnClickListener {
                startActivity(Intent(this@ProfileAdminActivity, HomeAdminActivity::class.java))
                finish()
            }

            btnLogout.setOnClickListener {
                prefManager.setLoggedIn(false)
                prefManager.clear()
                startActivity(Intent(this@ProfileAdminActivity, MainActivity::class.java))
                finish()
                // Show a toast message
                showToast("Logout successful")
            }
        }
    }

            // Function to show a toast message
            private fun showToast(message: String) {
                Toast.makeText(this@ProfileAdminActivity, message, Toast.LENGTH_SHORT).show()
            }
}




