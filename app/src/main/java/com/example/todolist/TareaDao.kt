package com.example.todolist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TareaDao {

    // Obtener tareas activas del usuario logueado para la pantalla principal
    @Query("SELECT * FROM tabla_tareas WHERE usuarioId = :userId AND enPapelera = 0 ORDER BY id DESC")
    suspend fun obtenerTodasActivasDeUsuario(userId: Int): List<Tarea>

    // Obtener tareas en la papelera del usuario logueado
    @Query("SELECT * FROM tabla_tareas WHERE usuarioId = :userId AND enPapelera = 1 ORDER BY id DESC")
    suspend fun obtenerPapeleraDeUsuario(userId: Int): List<Tarea>

    // Obtener tareas por categoría del usuario logueado
    @Query("SELECT * FROM tabla_tareas WHERE usuarioId = :userId AND categoria = :cat AND enPapelera = 0 ORDER BY id DESC")
    suspend fun obtenerPorCategoria(userId: Int, cat: String): List<Tarea>

    // Obtener tareas por fecha del usuario logueado (para el Calendario)
    @Query("SELECT * FROM tabla_tareas WHERE usuarioId = :userId AND fecha = :fechaTxt AND enPapelera = 0 ORDER BY id DESC")
    suspend fun obtenerPorFecha(userId: Int, fechaTxt: String): List<Tarea>

    @Insert
    suspend fun insertarTarea(tarea: Tarea)

    @Update
    suspend fun actualizarTarea(tarea: Tarea)

    @Delete
    suspend fun eliminarDefinitivo(tarea: Tarea)
}