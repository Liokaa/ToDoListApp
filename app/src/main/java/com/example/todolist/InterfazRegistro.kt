package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class InterfazRegistro : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_registro)

        db = AppDatabase.getDatabase(this)

        val etNombre = findViewById<EditText>(R.id.reNombre)
        val etCorreo = findViewById<EditText>(R.id.reCorreo)
        val etContrasena = findViewById<EditText>(R.id.reContrasena)
        val btnRegistrar = findViewById<Button>(R.id.btnCreateAccount)
        val btnCancelar = findViewById<Button>(R.id.btnCancel)

        btnRegistrar.setOnClickListener {
            val nombreInput = etNombre.text.toString().trim()
            val correoInput = etCorreo.text.toString().trim()
            val contrasenaInput = etContrasena.text.toString().trim()

            if (nombreInput.isEmpty() || correoInput.isEmpty() || contrasenaInput.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Usamos las variables id, usuario, correo y contrasena tal como están en Usuario.kt
                    val nuevoUsuario = Usuario(
                        id = 0,
                        usuario = nombreInput,
                        correo = correoInput,
                        contrasena = contrasenaInput
                    )

                    db.usuarioDao().registrarUsuario(nuevoUsuario)
                    Toast.makeText(this@InterfazRegistro, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@InterfazRegistro, InterfazLogin::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@InterfazRegistro, "Error al registrar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}