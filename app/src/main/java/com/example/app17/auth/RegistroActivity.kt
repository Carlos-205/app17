package com.example.app17.auth

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app17.R
import com.example.app17.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioData(
    val id: String,
    val nombres: String,
    val numero: Int
)

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etNumero: EditText
    private lateinit var btnContinuar: Button
    private lateinit var tvRegresar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_registro)

        val rootView = findViewById<ViewGroup>(R.id.main)
        if (rootView != null) { //Esta linea es nueva
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
        }

        //Referenciación en los campos de vista
        etNombre = findViewById(R.id.editTextNombre)
        etCorreo = findViewById(R.id.editTextCorreo)
        etContrasena = findViewById(R.id.editTextPassword)
        etNumero = findViewById(R.id.txtNumeroRegistro)
        btnContinuar = findViewById(R.id.btn_registro)
        tvRegresar = findViewById(R.id.textViewRegresar)

        //Listener del btn de registro
        btnContinuar.setOnClickListener {
            val nombres = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val numero = etNumero.text.toString().trim()

            //Validaciones de campos - CORREGIDO: ahora valida si están VACÍOS
            if (nombres.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || numero.isEmpty()){
                Toast.makeText(this, "Por favor complete la información", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (contrasena.length < 8){
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Registro en Supabase
            lifecycleScope.launch {
                try {
                    //Paso 1: Registrar Correo y Contraseña en el AuthUser de Supabase
                    SupabaseClient.client.auth.signUpWith(Email){
                        email = correo
                        password = contrasena
                    }
                    //Paso 2: UUID y adicionales
                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id?:""
                    SupabaseClient.client.postgrest["usuarios"].insert(
                        UsuarioData(
                            id = userId,
                            nombres = nombres,
                            numero = numero.toInt()
                        )
                    )
                    //Paso 3. Registro Exitoso
                    runOnUiThread {
                        Toast.makeText(
                            this@RegistroActivity,
                            "Registro exitoso",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                        finish()
                    }
                } catch (e: Exception){
                    runOnUiThread {
                        Toast.makeText(
                            this@RegistroActivity,
                            "Error al registrar: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        tvRegresar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}