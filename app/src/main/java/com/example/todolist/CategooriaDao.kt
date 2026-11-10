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

    @Query("SELECT * FROM tabla_categorias ORDER BY id ASC")
    suspend fun obtenerTodas(): List<Categoria>

    @Query("SELECT * FROM tabla_categorias WHERE nombre LIKE '%' || :query || '%'")
    suspend fun buscarCategorias(query: String): List<Categoria>
}