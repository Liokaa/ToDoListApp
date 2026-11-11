package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int, // 👈 Clave para aislar la tarea por usuario
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val categoria: String,
    val estado: String = "PENDIENTE", // Puede ser: "PENDIENTE", "EN_PROGRESO", "COMPLETADA"
    val completada: Boolean = false,   // Lo dejamos por compatibilidad si lo usas
    val enPapelera: Boolean = false
)