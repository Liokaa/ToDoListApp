package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InterfazPapelera : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_papelera)

        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnCalendario = findViewById<Button>(R.id.btnCalendario)
        val btnCategoria = findViewById<Button>(R.id.btnCategoria)
        val btnUsuario = findViewById<Button>(R.id.btnUsuario)

        btnLista.setOnClickListener {
            startActivity(Intent(this, InterfazLista::class.java))
        }

        btnCalendario.setOnClickListener {
            startActivity(Intent(this, InterfazCalendario::class.java))
        }

        btnCategoria.setOnClickListener {
            startActivity(Intent(this, InterfazCategoria::class.java))
        }

        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }
    }
}