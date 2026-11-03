package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InterfazLista : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_lista)

        val btnCalendario = findViewById<Button>(R.id.btnCalendario)
        val btnCategoria = findViewById<Button>(R.id.btnCategoria)
        val btnPapelera = findViewById<Button>(R.id.btnPapelera)
        val btnUsuario = findViewById<Button>(R.id.btnUsuario)

        btnCalendario.setOnClickListener {
            startActivity(Intent(this, InterfazCalendario::class.java))
        }

        btnCategoria.setOnClickListener {
            startActivity(Intent(this, InterfazCategoria::class.java))
        }

        btnPapelera.setOnClickListener {
            startActivity(Intent(this, InterfazPapelera::class.java))
        }

        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }
    }
}