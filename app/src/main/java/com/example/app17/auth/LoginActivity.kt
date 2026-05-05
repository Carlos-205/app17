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

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.app17.data.CredencialesManager
import com.example.app17.data.UsuarioRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvRegistro: TextView
    private lateinit var btnGoogle: Button
    private lateinit var tvRecuperarContrasena: TextView

    private lateinit var tvHuella: TextView


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

                    // Guardamos las credenciales aquí después de un intento de login
                    CredencialesManager.guardarCredenciales(this@LoginActivity, correo, contrasena)
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

        // Referencia al boton de huella
        tvHuella = findViewById(R.id.in_huella)
        // Llamar en onCreate para verificar al crear la Activity
        configurarVisibilidadHuella()
        // Listener del boton de huella
        tvHuella.setOnClickListener { mostrarDialogoHuella() }
    }
    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()
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

                //Verificar si es el primer login
                val user = SupabaseClient.client.auth.currentUserOrNull()
                if(user != null){
                    val existe = UsuarioRepository.existeUsuario(user.id)
                    if(!existe){
                        // Extraer el nombre completo de los metadatos de Google
                        val nombreCompleto = user.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: "Usuario Google"
                        val correoGoogle = user.email ?: ""
                        val numero = 0

                        // CORRECCIÓN: Llamada con 4 parámetros: id, nombres, numero, correo
                        UsuarioRepository.insertarUsuario(user.id, nombreCompleto, numero, correoGoogle)
                    }
                }

                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                    irAPantallaPrincipal()
                }
            }catch (e: Exception){
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun irAPantallaPrincipal() {
        runOnUiThread {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finishAffinity()
        }
    }

    private fun configurarVisibilidadHuella() {
        // Verificar si hay credenciales guardadas localmente
        val huellaActiva = CredencialesManager.huellaActiva(this)

        // Verificar si el dispositivo tiene sensor biometrico disponible
        val biometricManager = BiometricManager.from(this)
        val biometriaDisponible = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS

        // Mostrar solo si AMBAS condiciones son verdaderas
        tvHuella.visibility = if (huellaActiva && biometriaDisponible)
            android.view.View.VISIBLE
        else
            android.view.View.GONE
    }

    private fun mostrarDialogoHuella() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // Huella reconocida correctamente
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    val correo     = CredencialesManager.obtenerCorreo(this@LoginActivity)
                    val contrasena =
                        CredencialesManager.obtenerContrasena(this@LoginActivity)

                    if (correo != null && contrasena != null) {
                        // Hacer signIn real con las credenciales guardadas
                        lifecycleScope.launch {
                            try {
                                SupabaseClient.client.auth.signInWith(Email) {
                                    email    = correo
                                    password = contrasena
                                }
                                irAPantallaPrincipal()
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Error al iniciar sesion: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } else {
                        // No hay credenciales, limpiar y ocultar la huella
                        Toast.makeText(
                            this@LoginActivity,
                            "Sesion expirada. Inicia sesion con tu correo.",
                            Toast.LENGTH_LONG
                        ).show()
                        CredencialesManager.limpiarCredenciales(this@LoginActivity)
                        configurarVisibilidadHuella()
                    }
                }

                // Error irrecuperable del sensor
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    // Ignorar si el usuario cancelo voluntariamente
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error biometrico: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // Huella leida pero no reconocida, puede reintentar
                override fun onAuthenticationFailed() {
                    Toast.makeText(
                        this@LoginActivity,
                        "Huella no reconocida, intenta de nuevo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        // Configuracion visual del dialogo nativo de Android
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella")
            .setSubtitle("Usa tu huella dactilar para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}