package com.chi.serverapplication.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chi.serverapplication.R

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
