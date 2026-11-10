package com.example.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String = "",
    val colorHex: String = "#4A90E2" // Guardará el color en formato Hexadecimal (ej: #4A90E2)
)