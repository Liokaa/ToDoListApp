package com.example.todolist

import androidx.room.*

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: Categoria): Long

    @Update
    suspend fun actualizar(categoria: Categoria)

    @Delete
    suspend fun eliminar(categoria: Categoria)

    // Consulta todas las categorías pertenecientes ÚNICAMENTE al usuario logueado
    @Query("SELECT * FROM tabla_categorias WHERE usuarioId = :userId ORDER BY id ASC")
    suspend fun obtenerTodasDeUsuario(userId: Int): List<Categoria>

    // Busca categorías asociadas ÚNICAMENTE al usuario logueado
    @Query("SELECT * FROM tabla_categorias WHERE usuarioId = :userId AND nombre LIKE '%' || :query || '%'")
    suspend fun buscarCategorias(userId: Int, query: String): List<Categoria>

    // Consulta las tareas asociadas a esa categoría y pertenecientes al usuario logueado
    @Query("SELECT * FROM tabla_tareas WHERE usuarioId = :userId AND categoria = :nombreCategoria AND enPapelera = 0")
    suspend fun obtenerTareasPorCategoria(userId: Int, nombreCategoria: String): List<Tarea>
}