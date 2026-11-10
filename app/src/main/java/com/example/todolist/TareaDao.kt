package com.example.todolist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TareaDao {

    // Obtener tareas activas para la pantalla principal y categorías
    @Query("SELECT * FROM tabla_tareas WHERE enPapelera = 0 ORDER BY id DESC")
    suspend fun obtenerTodasActivas(): List<Tarea>

    // Obtener tareas enviadas a la papelera
    @Query("SELECT * FROM tabla_tareas WHERE enPapelera = 1 ORDER BY id DESC")
    suspend fun obtenerPapelera(): List<Tarea>

    // Obtener tareas por categoría activa
    @Query("SELECT * FROM tabla_tareas WHERE categoria = :cat AND enPapelera = 0 ORDER BY id DESC")
    suspend fun obtenerPorCategoria(cat: String): List<Tarea>

    // Obtener tareas por fecha activa (para el Calendario)
    @Query("SELECT * FROM tabla_tareas WHERE fecha = :fechaTxt AND enPapelera = 0 ORDER BY id DESC")
    suspend fun obtenerPorFecha(fechaTxt: String): List<Tarea>

    @Insert
    suspend fun insertarTarea(tarea: Tarea)

    @Update
    suspend fun actualizarTarea(tarea: Tarea)

    @Delete
    suspend fun eliminarDefinitivo(tarea: Tarea)
}