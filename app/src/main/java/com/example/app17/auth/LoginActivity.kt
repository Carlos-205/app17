package com.example.app17.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app17.R
import com.example.app17.SupabaseClient
import com.example.app17.main.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import android.widget.ScrollView
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import java.nio.channels.spi.AsynchronousChannelProvider.provider

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvRegistro: TextView
    private lateinit var btnGoogle: Button
    private lateinit var tvRecuperarContrasena: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //Configuración de rootview de teclado
        val rootView = findViewById<LinearLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            insets
        }

        etCorreo = findViewById(R.id.txt_incorreo)
        etContrasena = findViewById(R.id.txt_inpassword)
        btnIngresar = findViewById(R.id.button_ingresar)
        tvRecuperarContrasena = findViewById(R.id.txt_recuperarcontra)
        btnGoogle = findViewById(R.id.button_google)
        tvRegistro = findViewById(R.id.txt_register)

        //Inicio de sesion con informacion de SupaBase
        btnIngresar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            //Validaciones de campos
            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor complete la información", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (contrasena.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Validacion - Consulta en supabase
            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signInWith(Email) {
                        email = correo
                        password = contrasena
                    }
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread{
                        Toast.makeText(this@LoginActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //Inicio de sesion con informacion de Google
        btnGoogle.setOnClickListener {
            iniciarSesionConGoogle()

        }

        findViewById<TextView>(R.id.txt_register).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        findViewById<Button>(R.id.button_ingresar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    private fun iniciarSesionConGoogle() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("103754154037-cohtjl0tq2bio32sd78rvml5ovsq76bo.apps.googleusercontent.com")
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val credentialManager = CredentialManager.create(this@LoginActivity)
                val result = credentialManager.getCredential(this@LoginActivity, request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

                SupabaseClient.client.auth.signInWith(IDToken){
                    idToken = googleIdTokenCredential.idToken
                    provider = Google
                }
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()

                }
            }catch (e: Exception){
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}