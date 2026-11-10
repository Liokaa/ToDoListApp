package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
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

class InterfazCalendario : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_calendario)

        db = AppDatabase.getDatabase(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        val btnBurguer = findViewById<MaterialToolbar?>(R.id.Burguer)
        val btnUsuario = findViewById<ImageButton?>(R.id.btnUsuario)
        val navigationView = findViewById<NavigationView?>(R.id.navigationView)
        val calendarView = findViewById<CalendarView?>(R.id.calendarView2)

        // 1. Abrir menú de barritas
        btnBurguer?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }

        // 2. Abrir perfil de usuario
        btnUsuario?.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }

        // 3. Menú Lateral
        navigationView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_lista -> {
                    startActivity(Intent(this, InterfazLista::class.java))
                    finish()
                }
                R.id.nav_papelera -> {
                    startActivity(Intent(this, InterfazPapelera::class.java))
                }
                R.id.nav_usuario -> {
                    startActivity(Intent(this, InterfazUsuario::class.java))
                }
            }
            drawerLayout?.closeDrawer(GravityCompat.START)
            true
        }

        // 4. Al seleccionar una fecha en el Calendario
        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"

            lifecycleScope.launch {
                val tareasDelDia = db.tareaDao().obtenerPorFecha(fechaSeleccionada)
                if (tareasDelDia.isEmpty()) {
                    Toast.makeText(this@InterfazCalendario, "No hay tareas para el $fechaSeleccionada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@InterfazCalendario, "Tienes ${tareasDelDia.size} tareas el $fechaSeleccionada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}