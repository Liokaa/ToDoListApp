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
    private lateinit var adapter: TareaAdapter
    private lateinit var rvTareasCalendario: RecyclerView
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_calendario)

        db = AppDatabase.getDatabase(this)

        // Referencias a los componentes de tu XML
        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBurguer = findViewById<MaterialToolbar>(R.id.Burguer)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val calendarView = findViewById<CalendarView>(R.id.calendarView2)
        rvTareasCalendario = findViewById(R.id.rvTareasCalendario)
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada)

        // Configuración del RecyclerView
        rvTareasCalendario.layoutManager = LinearLayoutManager(this)
        adapter = TareaAdapter(
            listaTareas = emptyList(),
            onCheckChanged = { tarea, estaCompletada ->
                lifecycleScope.launch {
                    val tareaActualizada = tarea.copy(completada = estaCompletada)
                    db.tareaDao().actualizarTarea(tareaActualizada)
                }
            },
            onDeleteClicked = { tarea ->
                lifecycleScope.launch {
                    val tareaEnPapelera = tarea.copy(enPapelera = true)
                    db.tareaDao().actualizarTarea(tareaEnPapelera)
                    val fechaActual = tvFechaSeleccionada.text.toString().replace("Tareas del ", "")
                    cargarTareasPorFecha(fechaActual)
                }
            }
        )
        rvTareasCalendario.adapter = adapter

        // Drawer y Navegación Lateral
        btnBurguer.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_papelera -> startActivity(Intent(this, InterfazPapelera::class.java))
                R.id.nav_usuario -> startActivity(Intent(this, InterfazUsuario::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Cargar las tareas del día de HOY por defecto
        val cal = Calendar.getInstance()
        val diaHoy = String.format("%02d/%02d/%d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
        tvFechaSeleccionada.text = "Tareas del $diaHoy"
        cargarTareasPorFecha(diaHoy)

        // Escuchar selección de fechas en el CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val fechaFormateada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            tvFechaSeleccionada.text = "Tareas del $fechaFormateada"
            cargarTareasPorFecha(fechaFormateada)
        }
    }

    private fun cargarTareasPorFecha(fechaStr: String) {
        lifecycleScope.launch {
            val tareasDelDia = db.tareaDao().obtenerPorFecha(fechaStr)
            adapter.actualizarLista(tareasDelDia)

            if (tareasDelDia.isEmpty()) {
                Toast.makeText(this@InterfazCalendario, "No hay tareas para $fechaStr", Toast.LENGTH_SHORT).show()
            }
        }
    }
}