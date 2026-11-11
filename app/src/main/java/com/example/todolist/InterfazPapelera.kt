package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class InterfazPapelera : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvPapelera: RecyclerView
    private lateinit var etBuscar: EditText
    private lateinit var adapter: PapeleraAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_papelera)

        db = AppDatabase.getDatabase(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBurguer = findViewById<MaterialToolbar>(R.id.Burguer)
        val btnPapeleraIcon = findViewById<ImageView>(R.id.btnUsuario)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        rvPapelera = findViewById(R.id.rvPapelera)
        etBuscar = findViewById(R.id.etBuscar)

        rvPapelera.layoutManager = LinearLayoutManager(this)
        adapter = PapeleraAdapter(
            listaTareas = emptyList(),
            onItemClick = { tarea ->
                mostrarDialogoRestaurar(tarea)
            },
            onDeleteClick = { tarea ->
                eliminarDefinitivamente(tarea)
            }
        )
        rvPapelera.adapter = adapter

        // 1. ABRIR EL DRAWER DE FORMA DIRECTA Y ROBUSTA
        btnBurguer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        btnBurguer.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 2. NAVEGACIÓN COMPLETA DEL DRAWER LATERAL
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_calendario -> {
                    startActivity(Intent(this, InterfazCalendario::class.java))
                }
                R.id.nav_categoria -> {
                    startActivity(Intent(this, InterfazCategoria::class.java))
                }
                R.id.nav_usuario -> {
                    startActivity(Intent(this, InterfazUsuario::class.java))
                }
                R.id.nav_papelera -> {
                    // Ya estamos en Papelera
                }
                else -> {
                    // Si tienes una opción de "Inicio" o "Lista" en el menú:
                    startActivity(Intent(this, InterfazLista::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Acción al presionar el icono de basura en la esquina superior derecha
        btnPapeleraIcon.setOnClickListener {
            Toast.makeText(this, "Estás en la Papelera de tareas", Toast.LENGTH_SHORT).show()
        }

        // Escuchador de texto para filtrar la papelera en tiempo real
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarPapelera(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        cargarTareasPapelera()
    }

    override fun onResume() {
        super.onResume()
        cargarTareasPapelera()
    }

    private fun cargarTareasPapelera() {
        lifecycleScope.launch {
            val tareasEnPapelera = db.tareaDao().obtenerPapelera()
            adapter.actualizarLista(tareasEnPapelera)
        }
    }

    private fun filtrarPapelera(query: String) {
        lifecycleScope.launch {
            val tareasEnPapelera = db.tareaDao().obtenerPapelera()
            if (query.isBlank()) {
                adapter.actualizarLista(tareasEnPapelera)
            } else {
                val filtradas = tareasEnPapelera.filter {
                    it.titulo.contains(query, ignoreCase = true) ||
                            it.descripcion.contains(query, ignoreCase = true)
                }
                adapter.actualizarLista(filtradas)
            }
        }
    }

    private fun mostrarDialogoRestaurar(tarea: Tarea) {
        AlertDialog.Builder(this)
            .setTitle("Restaurar Tarea")
            .setMessage("¿Deseas devolver \"${tarea.titulo}\" a tu lista principal?")
            .setPositiveButton("Restaurar") { _, _ ->
                lifecycleScope.launch {
                    val tareaRestaurada = tarea.copy(enPapelera = false)
                    db.tareaDao().actualizarTarea(tareaRestaurada)
                    cargarTareasPapelera()
                    Toast.makeText(this@InterfazPapelera, "Tarea restaurada", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarDefinitivamente(tarea: Tarea) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Permanentemente")
            .setMessage("Esta acción no se puede deshacer. ¿Eliminar \"${tarea.titulo}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    db.tareaDao().eliminarDefinitivo(tarea)
                    cargarTareasPapelera()
                    Toast.makeText(this@InterfazPapelera, "Tarea eliminada por completo", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}