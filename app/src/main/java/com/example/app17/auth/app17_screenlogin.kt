package com.example.app17.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.app17.R

class app17_screenlogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app17_screenlogin)

        val registrate = findViewById<TextView>(R.id.txt_register)

        registrate.setOnClickListener {

            val intent = Intent(this, app17_screenregister::class.java)
            startActivity(intent)
        }
    }
}