package com.example.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class InterfazPapelera : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvPapelera: RecyclerView
    private lateinit var adapter: PapeleraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_papelera)

        db = AppDatabase.getDatabase(this)
        rvPapelera = findViewById(R.id.rvPapelera)

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

        cargarTareasPapelera()
    }

    private fun cargarTareasPapelera() {
        lifecycleScope.launch {
            val tareasEnPapelera = db.tareaDao().obtenerPapelera()
            adapter.actualizarLista(tareasEnPapelera)
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