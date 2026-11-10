package com.example.todolist

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class InterfazCategoria : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvTareas: RecyclerView
    private lateinit var adapter: TareaAdapter
    private lateinit var etBuscar: EditText

    private lateinit var btnCat1: Button
    private lateinit var btnCat2: Button
    private lateinit var btnCat3: Button

    private var categoriaActual: String = "Cat 1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_categoria)

        db = AppDatabase.getDatabase(this)

        val tvMenu = findViewById<TextView>(R.id.tvMenu)
        val tvUser = findViewById<TextView>(R.id.tvUser)
        btnCat1 = findViewById(R.id.btnCat1)
        btnCat2 = findViewById(R.id.btnCat2)
        btnCat3 = findViewById(R.id.btnCat3)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        etBuscar = findViewById(R.id.etBuscar)
        rvTareas = findViewById(R.id.rvTareasCategoria)

        // Configurar RecyclerView
        rvTareas.layoutManager = LinearLayoutManager(this)
        adapter = TareaAdapter(
            listaTareas = emptyList(),
            onCheckChanged = { tarea, estaCompletada ->
                lifecycleScope.launch {
                    val actualizada = tarea.copy(completada = estaCompletada)
                    db.tareaDao().actualizarTarea(actualizada)
                }
            },
            onDeleteClicked = { tarea ->
                lifecycleScope.launch {
                    val enPapelera = tarea.copy(enPapelera = true)
                    db.tareaDao().actualizarTarea(enPapelera)
                    cargarTareasPorCategoria(categoriaActual)
                }
            }
        )
        rvTareas.adapter = adapter

        // Búsqueda en tiempo real
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarCategorias(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tvMenu?.setOnClickListener { finish() }

        tvUser?.setOnClickListener {
            Toast.makeText(this, "Perfil de usuario", Toast.LENGTH_SHORT).show()
        }

        // Filtros de categoría totalmente funcionales
        btnCat1.setOnClickListener {
            seleccionarTab("Cat 1", btnCat1)
        }

        btnCat2.setOnClickListener {
            seleccionarTab("Cat 2", btnCat2)
        }

        btnCat3.setOnClickListener {
            seleccionarTab("Cat 3", btnCat3)
        }

        // Botón FAB para crear categoría en la DB
        fabAdd.setOnClickListener {
            mostrarDialogoNuevaCategoria()
        }

        // Cargar por defecto la primera categoría
        seleccionarTab("Cat 1", btnCat1)
    }

    private fun seleccionarTab(nombreCategoria: String, botonSeleccionado: Button) {
        categoriaActual = nombreCategoria

        // Cambiar colores visuales de los tabs
        btnCat1.setBackgroundColor(Color.WHITE)
        btnCat1.setTextColor(Color.parseColor("#333333"))
        btnCat2.setBackgroundColor(Color.WHITE)
        btnCat2.setTextColor(Color.parseColor("#333333"))
        btnCat3.setBackgroundColor(Color.WHITE)
        btnCat3.setTextColor(Color.parseColor("#333333"))

        botonSeleccionado.setBackgroundColor(Color.parseColor("#5B73A2"))
        botonSeleccionado.setTextColor(Color.WHITE)

        cargarTareasPorCategoria(categoriaActual)
    }

    private fun cargarTareasPorCategoria(cat: String) {
        lifecycleScope.launch {
            // Usa la consulta obtenerPorCategoria de tu TareaDao
            val tareas = db.tareaDao().obtenerPorCategoria(cat)
            adapter.actualizarLista(tareas)
        }
    }

    private fun filtrarCategorias(query: String) {
        lifecycleScope.launch {
            val tareasCat = db.tareaDao().obtenerPorCategoria(categoriaActual)
            if (query.isBlank()) {
                adapter.actualizarLista(tareasCat)
            } else {
                val filtradas = tareasCat.filter {
                    it.titulo.contains(query, ignoreCase = true) ||
                            it.descripcion.contains(query, ignoreCase = true)
                }
                adapter.actualizarLista(filtradas)
            }
        }
    }

    private fun mostrarDialogoNuevaCategoria() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nueva Categoría")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val inputNombre = EditText(this).apply { hint = "Nombre de la Categoría" }
        val inputDescripcion = EditText(this).apply { hint = "Descripción" }

        layout.addView(inputNombre)
        layout.addView(inputDescripcion)
        builder.setView(layout)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombre = inputNombre.text.toString().trim()
            val descripcion = inputDescripcion.text.toString().trim()

            if (nombre.isNotEmpty()) {
                lifecycleScope.launch {
                    val nuevaCat = Categoria(
                        nombre = nombre,
                        descripcion = descripcion,
                        colorHex = "#5B73A2"
                    )
                    db.categoriaDao().insertar(nuevaCat)
                    Toast.makeText(this@InterfazCategoria, "Categoría '$nombre' creada", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}