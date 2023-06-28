package com.firelord.ytaudio.presentation.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firelord.ytaudio.R
import com.firelord.ytaudio.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var folderPathViewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        folderPathViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}