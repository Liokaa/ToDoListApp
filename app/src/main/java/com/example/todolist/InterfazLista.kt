package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
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

class InterfazLista : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvTareas: RecyclerView
    private lateinit var adapter: TareaAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_lista)

        db = AppDatabase.getDatabase(this)

        // Referencias con los IDs reales de tu XML
        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBurguer = findViewById<MaterialToolbar>(R.id.Burguer)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarTarea)
        rvTareas = findViewById(R.id.rvTareasMain)

        // Configurar RecyclerView usando TareaAdapter
        rvTareas.layoutManager = LinearLayoutManager(this)
        adapter = TareaAdapter(
            listaTareas = emptyList(),
            onCheckChanged = { tarea, estaCompletada ->
                actualizarEstadoTarea(tarea, estaCompletada)
            },
            onDeleteClicked = { tarea ->
                moverAPapelera(tarea)
            }
        )
        rvTareas.adapter = adapter

        // Abrir Drawer al presionar el menú de barritas
        btnBurguer.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Abrir Perfil al presionar el icono de usuario
        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }

        // Navegación dentro del menú lateral
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_calendario -> {
                    startActivity(Intent(this, InterfazCalendario::class.java))
                }
                R.id.nav_papelera -> {
                    startActivity(Intent(this, InterfazPapelera::class.java))
                }
                R.id.nav_usuario -> {
                    startActivity(Intent(this, InterfazUsuario::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Botón '+' para agregar tarea
        btnAgregar.setOnClickListener {
            mostrarDialogoNuevaTarea()
        }

        // Cargar tareas al iniciar
        cargarTareas()
    }

    override fun onResume() {
        super.onResume()
        cargarTareas()
    }

    private fun cargarTareas() {
        lifecycleScope.launch {
            // Usamos obtenerTodasActivas() de tu TareaDao
            val tareasActivas = db.tareaDao().obtenerTodasActivas()
            adapter.actualizarLista(tareasActivas)
        }
    }

    private fun mostrarDialogoNuevaTarea() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nueva Tarea")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val inputTitulo = EditText(this).apply { hint = "Título de la tarea" }
        val inputDescripcion = EditText(this).apply { hint = "Descripción" }
        val inputFecha = EditText(this).apply { hint = "Fecha (ej. 15/11/2026)" }
        val inputCategoria = EditText(this).apply { hint = "Categoría (ej. Escuela, Trabajo)" }

        layout.addView(inputTitulo)
        layout.addView(inputDescripcion)
        layout.addView(inputFecha)
        layout.addView(inputCategoria)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = inputTitulo.text.toString().trim()
            val descripcion = inputDescripcion.text.toString().trim()
            val fecha = inputFecha.text.toString().trim()
            val categoria = inputCategoria.text.toString().trim()

            if (titulo.isNotEmpty()) {
                guardarTareaEnRoom(titulo, descripcion, fecha, categoria)
            } else {
                Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun guardarTareaEnRoom(titulo: String, descripcion: String, fecha: String, categoria: String) {
        lifecycleScope.launch {
            try {
                val nuevaTarea = Tarea(
                    id = 0,
                    titulo = titulo,
                    descripcion = descripcion,
                    completada = false,
                    enPapelera = false
                )

                db.tareaDao().insertarTarea(nuevaTarea)
                cargarTareas()
                Toast.makeText(this@InterfazLista, "¡Tarea guardada!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@InterfazLista, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun actualizarEstadoTarea(tarea: Tarea, estaCompletada: Boolean) {
        lifecycleScope.launch {
            val tareaActualizada = tarea.copy(completada = estaCompletada)
            db.tareaDao().actualizarTarea(tareaActualizada)
        }
    }

    private fun moverAPapelera(tarea: Tarea) {
        lifecycleScope.launch {
            val tareaEnPapelera = tarea.copy(enPapelera = true)
            db.tareaDao().actualizarTarea(tareaEnPapelera)
            cargarTareas()
            Toast.makeText(this@InterfazLista, "Tarea enviada a la papelera", Toast.LENGTH_SHORT).show()
        }
    }
}