package com.example.todolist

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
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
import java.util.Calendar

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

        // 1. Selector de fecha con DatePickerDialog (Pop-up de Calendario)
        var fechaSeleccionada = tareaParaEditar?.fecha ?: ""
        val btnFecha = Button(this).apply {
            text = if (fechaSeleccionada.isEmpty()) "Seleccionar fecha 📅" else "Fecha: $fechaSeleccionada"
        }

        btnFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                    btnFecha.text = "Fecha: $fechaSeleccionada"
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // 2. Desplegable Spinner para seleccionar Categoría
        val tvCategoriaLabel = TextView(this).apply {
            text = "Categoría:"
            textSize = 14f
            setPadding(0, 16, 0, 4)
        }
        val spinnerCategoria = Spinner(this)

        layout.addView(inputTitulo)
        layout.addView(inputDescripcion)
        layout.addView(btnFecha)
        layout.addView(tvCategoriaLabel)
        layout.addView(spinnerCategoria)

        // Cargar las categorías guardadas en la base de datos
        lifecycleScope.launch {
            val categoriasGuardadas = db.categoriaDao().obtenerTodas()
            val nombresCategorias = if (categoriasGuardadas.isEmpty()) {
                listOf("General")
            } else {
                categoriasGuardadas.map { it.nombre }
            }

            val adapterSpinner = ArrayAdapter(
                this@InterfazLista,
                android.R.layout.simple_spinner_dropdown_item,
                nombresCategorias
            )
            spinnerCategoria.adapter = adapterSpinner

            // Si se está editando una tarea, seleccionar la categoría previa en el Spinner
            tareaParaEditar?.let { tarea ->
                val index = nombresCategorias.indexOf(tarea.categoria)
                if (index >= 0) {
                    spinnerCategoria.setSelection(index)
                }
            }
        }

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = inputTitulo.text.toString().trim()
            val descripcion = inputDescripcion.text.toString().trim()
            val categoriaElegida = spinnerCategoria.selectedItem?.toString() ?: "General"

            // Si el usuario no eligió fecha, asignar por defecto la fecha de HOY
            if (fechaSeleccionada.isEmpty()) {
                val cal = Calendar.getInstance()
                fechaSeleccionada = String.format("%02d/%02d/%d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
            }

            if (titulo.isNotEmpty()) {
                guardarTareaEnRoom(tareaParaEditar, titulo, descripcion, fechaSeleccionada, categoriaElegida)
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
                        categoria = categoria,
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
                        categoria = categoria
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