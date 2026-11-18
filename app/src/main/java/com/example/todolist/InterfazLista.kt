package com.example.todolist

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private var idUsuarioLogueado: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_lista)

        solicitarPermisoNotificaciones()

        db = AppDatabase.getDatabase(this)

        val prefs = getSharedPreferences("MiSesionApp", Context.MODE_PRIVATE)
        idUsuarioLogueado = prefs.getInt("ID_USUARIO_LOGUEADO", -1)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBurguer = findViewById<MaterialToolbar>(R.id.Burguer)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarTarea)
        etBuscar = findViewById(R.id.etBuscarTarea)
        rvTareas = findViewById(R.id.rvTareasMain)

        val isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val burguerColor = if (isDark) Color.parseColor("#92ECF6") else Color.parseColor("#FFFFFF")
        btnBurguer.setNavigationIconTint(burguerColor)

        rvTareas.layoutManager = LinearLayoutManager(this)

        adapter = TareaAdapter(
            listaTareas = emptyList(),
            onEstadoCambiado = { tareaActualizada ->
                actualizarTareaEnRoom(tareaActualizada)
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

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarTareas()
    }

    private fun cargarTareas() {
        lifecycleScope.launch {
            if (idUsuarioLogueado != -1) {
                val tareasActivas = db.tareaDao().obtenerTodasActivasDeUsuario(idUsuarioLogueado)
                adapter.actualizarLista(tareasActivas)
            }
        }
    }

    private fun filtrarTareas(query: String) {
        lifecycleScope.launch {
            if (idUsuarioLogueado != -1) {
                val tareasActivas = db.tareaDao().obtenerTodasActivasDeUsuario(idUsuarioLogueado)
                if (query.isBlank()) {
                    adapter.actualizarLista(tareasActivas)
                } else {
                    val filtradas = tareasActivas.filter {
                        it.titulo.contains(query, ignoreCase = true) ||
                                it.descripcion.contains(query, ignoreCase = true) ||
                                it.categoria.contains(query, ignoreCase = true) ||
                                it.estado.contains(query, ignoreCase = true) ||
                                it.fecha.contains(query, ignoreCase = true)
                    }
                    adapter.actualizarLista(filtradas)
                }
            }
        }
    }

    private fun actualizarTareaEnRoom(tarea: Tarea) {
        lifecycleScope.launch {
            db.tareaDao().actualizarTarea(tarea)
            cargarTareas()
        }
    }

    private fun moverAPapelera(tarea: Tarea) {
        lifecycleScope.launch {
            val tareaEnPapelera = tarea.copy(enPapelera = true)
            db.tareaDao().actualizarTarea(tareaEnPapelera)
            NotificationScheduler.cancelarNotificacion(this@InterfazLista, tarea.id)
            cargarTareas()
            Toast.makeText(this@InterfazLista, getString(R.string.trash), Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoTarea(tareaParaEditar: Tarea? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (tareaParaEditar == null) getString(R.string.new_task) else getString(R.string.edit_task))

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val inputTitulo = EditText(this).apply {
            hint = getString(R.string.task_title_hint)
            setText(tareaParaEditar?.titulo ?: "")
        }

        val inputDescripcion = EditText(this).apply {
            hint = getString(R.string.task_desc_hint)
            setText(tareaParaEditar?.descripcion ?: "")
        }

        // Selector de fecha de la tarea
        var fechaSeleccionada = tareaParaEditar?.fecha ?: ""
        val btnFecha = Button(this).apply {
            text = if (fechaSeleccionada.isEmpty()) getString(R.string.select_date) else "${getString(R.string.select_date)}: $fechaSeleccionada"
        }

        btnFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                    btnFecha.text = "${getString(R.string.select_date)}: $fechaSeleccionada"
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Selector de Recordatorio Personalizado traducido
        var recordatorioCustomMillis: Long = tareaParaEditar?.recordatorioMillis ?: 0L
        val btnCustomReminder = Button(this).apply {
            text = if (recordatorioCustomMillis > 0L) {
                getString(R.string.reminder_custom_btn)
            } else {
                getString(R.string.reminder_default_btn)
            }
        }

        btnCustomReminder.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val timePicker = TimePickerDialog(
                        this,
                        { _, hour, minute ->
                            val customCal = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth, hour, minute, 0)
                            }
                            recordatorioCustomMillis = customCal.timeInMillis
                            btnCustomReminder.text = String.format("%s (%02d/%02d %02d:%02d)", getString(R.string.reminder_custom_btn), dayOfMonth, month + 1, hour, minute)
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    )
                    timePicker.show()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Selector de Categoría
        val tvCategoriaLabel = TextView(this).apply {
            text = getString(R.string.category_label)
            textSize = 14f
            setPadding(0, 12, 0, 4)
        }
        val spinnerCategoria = Spinner(this)

        // Selector de Estado
        val tvEstadoLabel = TextView(this).apply {
            text = getString(R.string.status_label)
            textSize = 14f
            setPadding(0, 12, 0, 4)
        }
        val spinnerEstado = Spinner(this)
        val opcionesEstado = listOf("PENDIENTE", "EN_PROGRESO", "COMPLETADA")
        val adapterEstado = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesEstado)
        spinnerEstado.adapter = adapterEstado

        tareaParaEditar?.let {
            val idx = opcionesEstado.indexOf(it.estado)
            if (idx >= 0) spinnerEstado.setSelection(idx)
        }

        layout.addView(inputTitulo)
        layout.addView(inputDescripcion)
        layout.addView(btnFecha)
        layout.addView(btnCustomReminder)
        layout.addView(tvCategoriaLabel)
        layout.addView(spinnerCategoria)
        layout.addView(tvEstadoLabel)
        layout.addView(spinnerEstado)

        lifecycleScope.launch {
            val categoriasGuardadas = db.categoriaDao().obtenerTodasDeUsuario(idUsuarioLogueado)
            val nombresCategorias = if (categoriasGuardadas.isEmpty()) {
                listOf("General")
            } else {
                categoriasGuardadas.map { it.nombre }
            }

            val adapterSpinnerCat = ArrayAdapter(
                this@InterfazLista,
                android.R.layout.simple_spinner_dropdown_item,
                nombresCategorias
            )
            spinnerCategoria.adapter = adapterSpinnerCat

            tareaParaEditar?.let { tarea ->
                val index = nombresCategorias.indexOf(tarea.categoria)
                if (index >= 0) {
                    spinnerCategoria.setSelection(index)
                }
            }
        }

        builder.setView(layout)

        builder.setPositiveButton(getString(R.string.save)) { dialog, _ ->
            val titulo = inputTitulo.text.toString().trim()
            val descripcion = inputDescripcion.text.toString().trim()
            val categoriaElegida = spinnerCategoria.selectedItem?.toString() ?: "General"
            val estadoElegido = spinnerEstado.selectedItem?.toString() ?: "PENDIENTE"

            if (fechaSeleccionada.isEmpty()) {
                val cal = Calendar.getInstance()
                fechaSeleccionada = String.format("%02d/%02d/%d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
            }

            if (titulo.isNotEmpty()) {
                guardarTareaEnRoom(
                    tareaParaEditar = tareaParaEditar,
                    titulo = titulo,
                    descripcion = descripcion,
                    fecha = fechaSeleccionada,
                    categoria = categoriaElegida,
                    estado = estadoElegido,
                    recordatorioMillis = recordatorioCustomMillis
                )
            } else {
                Toast.makeText(this, getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun guardarTareaEnRoom(
        tareaParaEditar: Tarea?,
        titulo: String,
        descripcion: String,
        fecha: String,
        categoria: String,
        estado: String,
        recordatorioMillis: Long
    ) {
        lifecycleScope.launch {
            try {
                val esCompletada = estado == "COMPLETADA"
                val idTareaGuardada: Int

                if (tareaParaEditar == null) {
                    val nuevaTarea = Tarea(
                        id = 0,
                        usuarioId = idUsuarioLogueado,
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha,
                        categoria = categoria,
                        estado = estado,
                        completada = esCompletada,
                        enPapelera = false,
                        recordatorioMillis = recordatorioMillis
                    )
                    idTareaGuardada = db.tareaDao().insertarTarea(nuevaTarea).toInt()
                    Toast.makeText(this@InterfazLista, getString(R.string.category_created, titulo), Toast.LENGTH_SHORT).show()
                } else {
                    val tareaActualizada = tareaParaEditar.copy(
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fecha,
                        categoria = categoria,
                        estado = estado,
                        completada = esCompletada,
                        recordatorioMillis = recordatorioMillis
                    )
                    db.tareaDao().actualizarTarea(tareaActualizada)
                    idTareaGuardada = tareaParaEditar.id
                    Toast.makeText(this@InterfazLista, getString(R.string.category_updated), Toast.LENGTH_SHORT).show()
                }

                val millisProgramados = if (recordatorioMillis > 0L) {
                    recordatorioMillis
                } else {
                    NotificationScheduler.calcularMillisUnDiaAntes(fecha)
                }

                if (millisProgramados != null) {
                    NotificationScheduler.programarNotificacion(
                        context = this@InterfazLista,
                        tareaId = idTareaGuardada,
                        titulo = titulo,
                        descripcion = descripcion,
                        triggerMillis = millisProgramados
                    )
                }

                cargarTareas()
            } catch (e: Exception) {
                Toast.makeText(this@InterfazLista, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}