package com.example.uasppapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.uasppapb.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this@MainActivity)

        val isLoggedIn = prefManager.isLoggedIn()
        if (!isLoggedIn) {
            val loginFragment = LoginFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, loginFragment)
                .commit()
        } else {
            intentToHomeActivity(prefManager.getRole())
        }
        val tabAdapter = TabAdapter(this)
        binding.viewPager.adapter = tabAdapter

        with(binding) {
            viewPager.adapter = TabAdapter(this@MainActivity)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Login"
                    1 -> "Register"
                    else -> ""
                }
            }.attach()
        }
    }

    fun intentToHomeActivity(role: String) {
        if (role == "user") {
            startActivity(Intent(this@MainActivity, HomeUserActivity::class.java))
        } else if (role == "admin") {
            startActivity(Intent(this@MainActivity, HomeAdminActivity::class.java))
        }
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }
}
