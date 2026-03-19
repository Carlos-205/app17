package com.example.app17.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.app17.R
import com.example.app17.auth.app17_screenlogin

class app17_screenstarting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app17_second_screen)

        val btn: Button = findViewById<Button>(R.id.button_Start)
        btn.setOnClickListener {
            val intent: Intent = Intent(this, app17_screenlogin::class.java)
            startActivity(intent)
        }
    }
}