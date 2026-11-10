package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String = "Cat 1", // Cat 1, Cat 2, Cat 3
    val fecha: String = "",          // Formato: DD/MM/YYYY
    val completada: Boolean = false,
    val enPapelera: Boolean = false  // false = Pantalla principal / true = Papelera
)