package com.example.app17.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.app17.R
import com.example.app17.SupabaseClient
import com.example.app17.auth.LoginActivity
import com.example.app17.data.UsuarioRepository
import com.example.app17.main.admin.admin_fragment
import com.example.app17.main.admin.usuarios_fragment
import com.example.app17.main.perfil.perfil_fragment
import com.example.app17.main.productos.carrito_fragment
import com.example.app17.main.productos.catalogo_fragment
import com.example.app17.main.productos.favoritos_fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.example.app17.main.productos.home_fragment
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar = findViewById <Toolbar> (R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navView = findViewById<NavigationView>(R.id.nav_view)


        setSupportActionBar(toolbar)

        val toogle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        toogle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black)

        cargarFragment(home_fragment())
        bottomNav.selectedItemId = R.id.nav_home

        configurarMenuPorRol(navView.menu)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> cargarFragment(home_fragment())
                R.id.nav_catalogo -> cargarFragment(catalogo_fragment())
                R.id.nav_carrito -> cargarFragment(carrito_fragment())
                R.id.nav_perfil -> cargarFragment(perfil_fragment())
            }
            true
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_favoritos -> cargarFragment(favoritos_fragment())
                R.id.nav_admin -> cargarFragment(admin_fragment())
                R.id.nav_usuarios -> cargarFragment(usuarios_fragment())
                R.id.nav_logout -> cerrarSesion()

            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun configurarMenuPorRol(menu: Menu) {
        lifecycleScope.launch {
            val rol = UsuarioRepository.obtenerRolActual()
            runOnUiThread {
                when (rol) {
                    "admin"->{
                        menu.findItem(R.id.nav_admin).isVisible = true
                        menu.findItem(R.id.nav_usuarios).isVisible = true
                    }
                    "vendedor" ->{
                        menu.findItem(R.id.nav_admin).isVisible = true
                        menu.findItem(R.id.nav_usuarios).isVisible = false
                    }
                    else ->{
                        menu.findItem(R.id.nav_admin).isVisible = false
                        menu.findItem(R.id.nav_usuarios).isVisible = false
                    }
                }
            }
        }
    }

    private fun cargarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun cerrarSesion(){
        lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }catch (e: Exception){
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error al cerrar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}