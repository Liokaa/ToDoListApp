package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InterfazUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_usuario)

        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnCalendario = findViewById<Button>(R.id.btnCalendario)
        val btnCategoria = findViewById<Button>(R.id.btnCategoria)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLista.setOnClickListener {
            startActivity(Intent(this, InterfazLista::class.java))
        }

        btnCalendario.setOnClickListener {
            startActivity(Intent(this, InterfazCalendario::class.java))
        }

        btnCategoria.setOnClickListener {
            startActivity(Intent(this, InterfazCategoria::class.java))
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, InterfazLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}