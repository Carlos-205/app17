package com.example.app17.main.productos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app17.R

class home_fragment : Fragment() {
    private val listaProductos = listOf(
        Product ("Bolsa Estilo Medival", 7.99, R.drawable.bolsa_medieval),
        Product ("Gaban Color Caqui", 27.99, R.drawable.gaban_caqui),
        Product ("Botas Asthetic", 12.99, R.drawable.botas_aesthetic),
        Product ("Steampunk Glasses", 4.99, R.drawable.steampunk_glasses)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_productos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = ProductoAdapter(listaProductos)

        return view
    }
}