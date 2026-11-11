package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        val tvClose = findViewById<View>(R.id.tvClose)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
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

                    tvTitle?.text = "¡Hola, ${usuarioActual.usuario}!"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Cerrar ventana con la '✕'
        tvClose?.setOnClickListener {
            finish()
        }

        // Guardar cambios usando registrarUsuario
        btnCambiar.setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            val nuevoCorreo = etCorreo.text.toString().trim()
            val nuevaContrasena = etContrasena.text.toString().trim()

            if (nuevoNombre.isNotEmpty() && nuevoCorreo.isNotEmpty() && nuevaContrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val usuarios = db.usuarioDao().obtenerTodos()
                        if (usuarios.isNotEmpty()) {
                            val usuarioActualizado = usuarios.last().copy(
                                usuario = nuevoNombre,
                                correo = nuevoCorreo,
                                contrasena = nuevaContrasena
                            )
                            // AQUÍ USAMOS registrarUsuario (que reemplaza al existir la misma PK)
                            db.usuarioDao().registrarUsuario(usuarioActualizado)
                            Toast.makeText(this@InterfazUsuario, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@InterfazUsuario, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Cerrar Sesión
        btnLogout.setOnClickListener {
            val intent = Intent(this, InterfazLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}