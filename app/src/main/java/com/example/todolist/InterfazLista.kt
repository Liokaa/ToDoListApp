package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var etBuscar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_lista)

        db = AppDatabase.getDatabase(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBurguer = findViewById<MaterialToolbar>(R.id.Burguer)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarTarea)
        etBuscar = findViewById(R.id.etBuscarTarea)
        rvTareas = findViewById(R.id.rvTareasMain)

        rvTareas.layoutManager = LinearLayoutManager(this)

        adapter = TareaAdapter(
            listaTareas = emptyList(),
            onCheckChanged = { tarea, estaCompletada ->
                actualizarEstadoTarea(tarea, estaCompletada)
            },
            onDeleteClicked = { tarea ->
                moverAPapelera(tarea)
            },
            onItemClicked = { tarea ->
                mostrarDialogoTarea(tareaParaEditar = tarea)
            }
        )
        rvTareas.adapter = adapter

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarTareas(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnBurguer.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_calendario -> startActivity(Intent(this, InterfazCalendario::class.java))
                R.id.nav_papelera -> startActivity(Intent(this, InterfazPapelera::class.java))
                R.id.nav_categoria -> startActivity(Intent(this, InterfazCategoria::class.java))
                R.id.nav_usuario -> startActivity(Intent(this, InterfazUsuario::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        btnAgregar.setOnClickListener {
            mostrarDialogoTarea()
        }

        cargarTareas()
    }

    override fun onResume() {
        super.onResume()
        cargarTareas()
    }

    private fun cargarTareas() {
        lifecycleScope.launch {
            val tareasActivas = db.tareaDao().obtenerTodasActivas()
            adapter.actualizarLista(tareasActivas)
        }
    }

    private fun filtrarTareas(query: String) {
        lifecycleScope.launch {
            val tareasActivas = db.tareaDao().obtenerTodasActivas()
            if (query.isBlank()) {
                adapter.actualizarLista(tareasActivas)
            } else {
                val filtradas = tareasActivas.filter {
                    it.titulo.contains(query, ignoreCase = true) ||
                            it.descripcion.contains(query, ignoreCase = true) ||
                            it.categoria.contains(query, ignoreCase = true) ||
                            it.fecha.contains(query, ignoreCase = true)
                }
                adapter.actualizarLista(filtradas)
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

    private fun mostrarDialogoTarea(tareaParaEditar: Tarea? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (tareaParaEditar == null) "Nueva Tarea" else "Editar Tarea")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val inputTitulo = EditText(this).apply {
            hint = "Título de la tarea"
            setText(tareaParaEditar?.titulo ?: "")
        }
        val inputDescripcion = EditText(this).apply {
            hint = "Descripción"
            setText(tareaParaEditar?.descripcion ?: "")
        }
        val inputFecha = EditText(this).apply {
            hint = "Fecha (ej. 15/11/2026)"
            setText(tareaParaEditar?.fecha ?: "")
        }
        val inputCategoria = EditText(this).apply {
            hint = "Categoría (ej. Cat 1, Cat 2, Cat 3)"
            setText(tareaParaEditar?.categoria ?: "Cat 1")
        }

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
                guardarTareaEnRoom(tareaParaEditar, titulo, descripcion, fecha, categoria)
            } else {
                Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun guardarTareaEnRoom(
        tareaParaEditar: Tarea?,
        titulo: String,
        descripcion: String,
        fecha: String,
        categoria: String
    ) {
        lifecycleScope.launch {
            try {
                if (tareaParaEditar == null) {
                    val nuevaTarea = Tarea(
                        id = 0,
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha,
                        categoria = if (categoria.isNotEmpty()) categoria else "Cat 1",
                        completada = false,
                        enPapelera = false
                    )
                    db.tareaDao().insertarTarea(nuevaTarea)
                    Toast.makeText(this@InterfazLista, "¡Tarea creada!", Toast.LENGTH_SHORT).show()
                } else {
                    val tareaActualizada = tareaParaEditar.copy(
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha,
                        categoria = if (categoria.isNotEmpty()) categoria else "Cat 1"
                    )
                    db.tareaDao().actualizarTarea(tareaActualizada)
                    Toast.makeText(this@InterfazLista, "¡Tarea actualizada!", Toast.LENGTH_SHORT).show()
                }
                cargarTareas()
            } catch (e: Exception) {
                Toast.makeText(this@InterfazLista, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}