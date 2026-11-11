package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

class InterfazCalendario : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvTareas: RecyclerView
    private lateinit var adapter: TareaAdapter
    private lateinit var calendarView: CalendarView
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var drawerLayout: DrawerLayout

    private var fechaFiltroActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_calendario)

        db = AppDatabase.getDatabase(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBurguer = findViewById<MaterialToolbar>(R.id.Burguer)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        calendarView = findViewById(R.id.calendarView2)
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada)
        rvTareas = findViewById(R.id.rvTareasCalendario)

        rvTareas.layoutManager = LinearLayoutManager(this)

        // Configuración del TareaAdapter con el nuevo parámetro 'onEstadoCambiado'
        adapter = TareaAdapter(
            listaTareas = emptyList(),
            onEstadoCambiado = { tareaActualizada ->
                lifecycleScope.launch {
                    db.tareaDao().actualizarTarea(tareaActualizada)
                    cargarTareasPorFecha(fechaFiltroActual)
                }
            },
            onDeleteClicked = { tarea ->
                lifecycleScope.launch {
                    val tareaEnPapelera = tarea.copy(enPapelera = true)
                    db.tareaDao().actualizarTarea(tareaEnPapelera)
                    cargarTareasPorFecha(fechaFiltroActual)
                    Toast.makeText(this@InterfazCalendario, "Tarea movida a la papelera", Toast.LENGTH_SHORT).show()
                }
            },
            onItemClicked = { tarea ->
                Toast.makeText(this, "Tarea: ${tarea.titulo}", Toast.LENGTH_SHORT).show()
            }
        )
        rvTareas.adapter = adapter

        btnBurguer.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_lista -> startActivity(Intent(this, InterfazLista::class.java))
                R.id.nav_papelera -> startActivity(Intent(this, InterfazPapelera::class.java))
                R.id.nav_categoria -> startActivity(Intent(this, InterfazCategoria::class.java))
                R.id.nav_usuario -> startActivity(Intent(this, InterfazUsuario::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Seleccionar por defecto la fecha de HOY
        val cal = Calendar.getInstance()
        fechaFiltroActual = String.format("%02d/%02d/%d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
        tvFechaSeleccionada.text = "Tareas para el $fechaFiltroActual"

        // Evento cuando el usuario toca un día en el CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            fechaFiltroActual = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            tvFechaSeleccionada.text = "Tareas para el $fechaFiltroActual"
            cargarTareasPorFecha(fechaFiltroActual)
        }

        cargarTareasPorFecha(fechaFiltroActual)
    }

    override fun onResume() {
        super.onResume()
        cargarTareasPorFecha(fechaFiltroActual)
    }

    private fun cargarTareasPorFecha(fechaStr: String) {
        lifecycleScope.launch {
            val tareasActivas = db.tareaDao().obtenerTodasActivas()
            // Filtra las tareas que coincidan exactamente con la fecha seleccionada
            val tareasFiltradas = tareasActivas.filter { it.fecha == fechaStr }
            adapter.actualizarLista(tareasFiltradas)
        }
    }
}