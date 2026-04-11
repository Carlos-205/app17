package com.example.app17.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.app17.R
import com.example.app17.main.MainActivity

class app17_screenlogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        findViewById<TextView>(R.id.txt_register).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        findViewById<Button>(R.id.button_ingresar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}