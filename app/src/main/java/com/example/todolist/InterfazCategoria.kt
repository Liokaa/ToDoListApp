package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InterfazCategoria : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_categoria)

        val btnLista = findViewById<Button>(R.id.btnLista)
        val btnCalendario = findViewById<Button>(R.id.btnCalendario)
        val btnPapelera = findViewById<Button>(R.id.btnPapelera)
        val btnUsuario = findViewById<Button>(R.id.btnUsuario)

        btnLista.setOnClickListener {
            startActivity(Intent(this, InterfazLista::class.java))
        }

        btnCalendario.setOnClickListener {
            startActivity(Intent(this, InterfazCalendario::class.java))
        }

        btnPapelera.setOnClickListener {
            startActivity(Intent(this, InterfazPapelera::class.java))
        }

        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }
    }
}