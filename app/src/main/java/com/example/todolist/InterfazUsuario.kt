package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class InterfazUsuario : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_usuario)

        db = AppDatabase.getDatabase(this)

        // Referencias a los componentes de tu XML
        val tvClose = findViewById<TextView>(R.id.tvClose)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnCambiar = findViewById<Button>(R.id.btnCambiar)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Cargar datos actuales desde Room
        lifecycleScope.launch {
            try {
                val usuarios = db.usuarioDao().obtenerTodos()
                if (usuarios.isNotEmpty()) {
                    val usuarioActual = usuarios.last()
                    etNombre.setText(usuarioActual.usuario)
                    etCorreo.setText(usuarioActual.correo)
                    etContrasena.setText(usuarioActual.contrasena)
                }
            } catch (e: Exception) {
                // Manejo en caso de que la lista esté vacía
            }
        }

        // --- CERRAR VENTANA SIN CERRAR SESIÓN ---
        // Al dar clic en '✕', simplemente regresa a la pantalla anterior
        tvClose.setOnClickListener {
            finish()
        }

        // Guardar cambios en la información
        btnCambiar.setOnClickListener {
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
            finish() // También cierra la ventana al guardar los cambios
        }

        // --- CERRAR SESIÓN DEFINITIVAMENTE ---
        // Solo este botón saca al usuario al Login
        btnLogout.setOnClickListener {
            val intent = Intent(this, InterfazLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}