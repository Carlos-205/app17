package com.example.app17.main.perfil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.app17.EditarPerfilFragment
import com.example.app17.R
import com.example.app17.data.UsuarioRepository
import kotlinx.coroutines.launch

class perfil_fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivFotoPerfil = view.findViewById<ImageView>(R.id.iv_foto_perfil)
        val tvNombre = view.findViewById<TextView>(R.id.tv_perfil_nombre)
        val tvRol = view.findViewById<TextView>(R.id.tv_perfil_rol)
        val tvCorreo = view.findViewById<TextView>(R.id.tv_perfil_correo)
        val btnEditar = view.findViewById<Button>(R.id.btn_editar_perfil)

        Log.d("DEBUG_PERFIL", "Fragmento de perfil cargado")

        // 1. Cargar datos del usuario al entrar
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val usuario = UsuarioRepository.obtenerUsuarioActual()
                if (usuario != null) {
                    tvNombre.text = usuario.nombres
                    tvRol.text = "Rol: ${usuario.rol}"
                    tvCorreo.text = usuario.correo ?: ""

                    if (!usuario.foto_url.isNullOrEmpty()) {
                        ivFotoPerfil.load(usuario.foto_url) {
                            crossfade(true)
                            transformations(CircleCropTransformation())
                            placeholder(R.mipmap.ic_launcher_round)
                            error(R.mipmap.ic_launcher_round)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DEBUG_PERFIL", "Error al cargar datos: ${e.message}")
            }
        }

        // 2. Configuración del botón para ir a la edición
        btnEditar.setOnClickListener {
            Log.d("DEBUG_PERFIL", "Botón Editar presionado")

            try {
                val fragmentEditar = EditarPerfilFragment()

                // Realizamos la transacción para cambiar al fragmento de edición
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentEditar)
                    .addToBackStack(null) // Esto permite volver al perfil al darle atrás
                    .commit()

                Log.d("DEBUG_PERFIL", "Navegación iniciada hacia EditarPerfilFragment")
            } catch (e: Exception) {
                Log.e("DEBUG_PERFIL", "Error al navegar: ${e.message}")
                Toast.makeText(requireContext(), "Error al abrir edición", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = perfil_fragment()
    }
}