package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val categoria: String,
    val estado: String = "PENDIENTE",
    val completada: Boolean = false,
    val enPapelera: Boolean = false,
    val recordatorioMillis: Long = 0L // Timestamp personalizado o 0 para automático (1 día antes)
)