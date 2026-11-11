package com.example.todolist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InterfazLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // VERIFICACIÓN DE AUTO-LOGIN
        // Si el usuario ya tiene sesión activa, lo mandamos directo a la pantalla principal
        val prefs = getSharedPreferences("MiSesionApp", Context.MODE_PRIVATE)
        val idUsuarioLogueado = prefs.getInt("ID_USUARIO_LOGUEADO", -1)

        if (idUsuarioLogueado != -1) {
            startActivity(Intent(this, InterfazLista::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_interfaz_login)

        val etUser = findViewById<EditText>(R.id.ingresaUsuario)
        val etPassword = findViewById<EditText>(R.id.Password)
        val btnLogin = findViewById<View>(R.id.btnLogin)
        val btnRegister = findViewById<View>(R.id.btnRegister)
        val btnGoogle = findViewById<ImageView>(R.id.btnGoogle)
        val btnFacebook = findViewById<ImageView>(R.id.btnFacebook)
        val btnInstagram = findViewById<ImageView>(R.id.btnInstagram)

        val db = AppDatabase.getDatabase(this)

        btnLogin.setOnClickListener {
            val userOrEmail = etUser.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (userOrEmail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Ingresa tu usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val usuarioEncontrado = db.usuarioDao().login(userOrEmail, pass)
                withContext(Dispatchers.Main) {
                    if (usuarioEncontrado != null) {
                        // 💾 GUARDAR LA SESIÓN DEL USUARIO QUE ACABA DE INGRESAR
                        prefs.edit().putInt("ID_USUARIO_LOGUEADO", usuarioEncontrado.id).apply()

                        Toast.makeText(this@InterfazLogin, "¡Bienvenido ${usuarioEncontrado.usuario}!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@InterfazLogin, InterfazLista::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@InterfazLogin, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, InterfazRegistro::class.java)
            startActivity(intent)
        }

        btnGoogle.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            startActivity(intent)
        }

        btnFacebook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
            startActivity(intent)
        }

        btnInstagram.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"))
            startActivity(intent)
        }
    }
}