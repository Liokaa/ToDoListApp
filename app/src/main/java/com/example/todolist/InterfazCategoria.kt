package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InterfazCategoria : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_categoria)

        val tvMenu = findViewById<TextView>(R.id.tvMenu)
        val tvUser = findViewById<TextView>(R.id.tvUser)

        tvMenu.setOnClickListener {
            startActivity(Intent(this, InterfazLista::class.java))
        }

        tvUser.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }
    }
}
