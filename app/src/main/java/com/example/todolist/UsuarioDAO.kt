package com.example.todolist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrarUsuario(usuario: Usuario)

    @Query("SELECT * FROM tabla_usuarios WHERE (usuario = :userOrEmail OR correo = :userOrEmail) AND contrasena = :pass LIMIT 1")
    suspend fun login(userOrEmail: String, pass: String): Usuario?

    @Query("SELECT * FROM tabla_usuarios WHERE usuario = :user OR correo = :email LIMIT 1")
    suspend fun existeUsuario(user: String, email: String): Usuario?

    // Método agregado para obtener todos los usuarios
    @Query("SELECT * FROM tabla_usuarios")
    suspend fun obtenerTodos(): List<Usuario>
}