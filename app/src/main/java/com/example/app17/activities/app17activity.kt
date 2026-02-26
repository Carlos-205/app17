package com.example.app17.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app17.R

class app17activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app17activity)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, app17SecondScreen::class.java))
            finish()
        },5000)
    }
}