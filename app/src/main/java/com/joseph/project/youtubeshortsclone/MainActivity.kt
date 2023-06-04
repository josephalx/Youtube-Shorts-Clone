package com.joseph.project.youtubeshortsclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joseph.project.youtubeshortsclone.databinding.ActivityMainBinding
import com.joseph.project.youtubeshortsclone.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            val homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(binding.flFragment.id, homeFragment)
                commit()
            }
        }

    }
}