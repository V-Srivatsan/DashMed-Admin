package com.dashmed.admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.dashmed.admin.databinding.ActivityMainBinding
import com.dashmed.admin.fragments.Attendance
import com.dashmed.admin.fragments.Employees
import com.dashmed.admin.fragments.Inventory
import com.dashmed.admin.fragments.Settings

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currId: Int = R.id.nav_employees

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Utils.getUID(this) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener {
            replaceFragment(it.itemId)
            true
        }

        binding.bottomNav.setOnItemReselectedListener { false }
    }

    private fun replaceFragment(itemId: Int) {
        supportFragmentManager.beginTransaction().apply {
            when (itemId) {
                R.id.nav_employees -> {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    replace(R.id.main_container, Employees())
                }
                R.id.nav_attendance -> {
                    if (currId == R.id.nav_employees)
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    else
                        setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    replace(R.id.main_container, Attendance())
                }
                R.id.nav_inventory -> {
                    if (currId == R.id.nav_settings)
                        setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    else
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    replace(R.id.main_container, Inventory())
                }
                R.id.nav_settings -> {
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    replace(R.id.main_container, Settings())
                }
            }
        }.commit()
        currId = itemId
    }
}