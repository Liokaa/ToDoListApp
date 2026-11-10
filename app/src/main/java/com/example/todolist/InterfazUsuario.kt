package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InterfazUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_usuario)

        val tvMenu = findViewById<TextView>(R.id.tvMenu)
        val tvClose = findViewById<TextView>(R.id.tvClose)
        val btnCambiar = findViewById<Button>(R.id.btnCambiar)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        tvMenu.setOnClickListener {
            startActivity(Intent(this, InterfazLista::class.java))
        }

        tvClose.setOnClickListener {
            finish()
        }

        btnCambiar.setOnClickListener {
            // Acción de guardar cambios / cambiar perfil
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, InterfazLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
