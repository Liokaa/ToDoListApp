package com.example.todolist

import android.content.Context
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
    private var idUsuarioLogueado: Int = -1

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

        // 1. Obtener el ID del usuario actualmente logueado desde SharedPreferences
        val prefs = getSharedPreferences("MiSesionApp", Context.MODE_PRIVATE)
        idUsuarioLogueado = prefs.getInt("ID_USUARIO_LOGUEADO", -1)

        // 2. Cargar ÚNICAMENTE los datos del usuario logueado
        lifecycleScope.launch {
            try {
                if (idUsuarioLogueado != -1) {
                    val usuarioActual = db.usuarioDao().obtenerPorId(idUsuarioLogueado)
                    usuarioActual?.let { user ->
                        etNombre.setText(user.usuario)
                        etCorreo.setText(user.correo)
                        etContrasena.setText(user.contrasena)
                        tvTitle?.text = "¡Hola, ${user.usuario}!"
                    }
                } else {
                    // Si por alguna razón no hay ID, tomamos el último por respaldo
                    val usuarios = db.usuarioDao().obtenerTodos()
                    if (usuarios.isNotEmpty()) {
                        val user = usuarios.last()
                        idUsuarioLogueado = user.id
                        etNombre.setText(user.usuario)
                        etCorreo.setText(user.correo)
                        etContrasena.setText(user.contrasena)
                        tvTitle?.text = "¡Hola, ${user.usuario}!"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Cerrar ventana con la '✕'
        tvClose?.setOnClickListener {
            finish()
        }

        // Guardar cambios del usuario logueado
        btnCambiar.setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            val nuevoCorreo = etCorreo.text.toString().trim()
            val nuevaContrasena = etContrasena.text.toString().trim()

            if (nuevoNombre.isNotEmpty() && nuevoCorreo.isNotEmpty() && nuevaContrasena.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val usuarioActual = db.usuarioDao().obtenerPorId(idUsuarioLogueado)
                        if (usuarioActual != null) {
                            val usuarioActualizado = usuarioActual.copy(
                                usuario = nuevoNombre,
                                correo = nuevoCorreo,
                                contrasena = nuevaContrasena
                            )
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

        // Cerrar Sesión y LIMPIAR datos de sesión
        btnLogout.setOnClickListener {
            // Borrar el ID del usuario de SharedPreferences
            val editor = prefs.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, InterfazLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}