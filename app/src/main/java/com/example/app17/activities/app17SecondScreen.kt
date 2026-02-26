package com.example.app17.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app17.R

class app17SecondScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app17_second_screen)

        val btn: Button = findViewById<Button>(R.id.button_Start)
        btn.setOnClickListener {
            val intent: Intent = Intent(this, app17ThirdScreen:: class.java)
            startActivity(intent)
        }
    }
}