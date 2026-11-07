package com.example.todolist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InterfazPapelera : AppCompatActivity() {
    private lateinit var rvPapelera: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var adapter: PapeleraAdapter

    private val listaOriginal = mutableListOf<ElementoPapelera>()
    private val listaFiltrada = mutableListOf<ElementoPapelera>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_papelera)

        rvPapelera = findViewById(R.id.rvPapelera)
        etBuscar = findViewById(R.id.etBuscar)

        rvPapelera.layoutManager = LinearLayoutManager(this)

        adapter = PapeleraAdapter(listaFiltrada) { itemAEliminar ->
            listaOriginal.remove(itemAEliminar)
            filtrar(etBuscar.text.toString())
        }
        rvPapelera.adapter = adapter

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        agregarElementoDinamico("Nombre Tarea", "Descripción de la tarea")
        agregarElementoDinamico("Ejemplo", "Esto es un ejemplo")
        agregarElementoDinamico("Ejemplo 2", "Esto es un ejemplo 2")
    }

    fun agregarElementoDinamico(nombre: String, descripcion: String) {
        val nuevoItem = ElementoPapelera(nombre, descripcion)
        listaOriginal.add(nuevoItem)
        filtrar(etBuscar.text.toString())
    }

    private fun filtrar(texto: String) {
        val busqueda = texto.trim().lowercase()
        listaFiltrada.clear()

        if (busqueda.isEmpty()) {
            listaFiltrada.addAll(listaOriginal)
        } else {
            val resultado = listaOriginal.filter { item ->
                item.nombre.lowercase().contains(busqueda) ||
                item.descripcion.lowercase().contains(busqueda)
            }
            listaFiltrada.addAll(resultado)
        }
        adapter.notifyDataSetChanged()
    }
}