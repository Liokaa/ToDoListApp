package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InterfazRegistro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_registro)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        val db = AppDatabase.getDatabase(this)

        btnCreateAccount.setOnClickListener {
            val usuarioText = etName.text.toString().trim()
            val correoText = etEmail.text.toString().trim()
            val passText = etPassword.text.toString().trim()

            if (usuarioText.isEmpty() || correoText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val existente = db.usuarioDao().existeUsuario(usuarioText, correoText)
                if (existente != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@InterfazRegistro, "El usuario o correo ya está registrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val nuevoUsuario = Usuario(
                        usuario = usuarioText,
                        correo = correoText,
                        contrasena = passText
                    )
                    db.usuarioDao().registrarUsuario(nuevoUsuario)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@InterfazRegistro, "¡Usuario registrado con éxito!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@InterfazRegistro, InterfazLogin::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        btnCancel.setOnClickListener {
            val intent = Intent(this, InterfazLogin::class.java)
            startActivity(intent)
            finish()
        }
    }
}