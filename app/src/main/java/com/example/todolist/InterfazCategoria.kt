package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InterfazCategoria : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_categoria)

        val tvMenu = findViewById<TextView>(R.id.tvMenu)
        val tvUser = findViewById<TextView>(R.id.tvUser)
        val btnCat1 = findViewById<Button>(R.id.btnCat1)
        val btnCat2 = findViewById<Button>(R.id.btnCat2)
        val btnCat3 = findViewById<Button>(R.id.btnCat3)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        tvMenu?.setOnClickListener {
            finish()
        }

        tvUser?.setOnClickListener {
            Toast.makeText(this, "Perfil de usuario", Toast.LENGTH_SHORT).show()
        }

        btnCat1?.setOnClickListener {
            Toast.makeText(this, "Categoría 1 seleccionada", Toast.LENGTH_SHORT).show()
        }

        btnCat2?.setOnClickListener {
            Toast.makeText(this, "Categoría 2 seleccionada", Toast.LENGTH_SHORT).show()
        }

        btnCat3?.setOnClickListener {
            Toast.makeText(this, "Categoría 3 seleccionada", Toast.LENGTH_SHORT).show()
        }

        fabAdd?.setOnClickListener {
            Toast.makeText(this, "Agregar nueva categoría", Toast.LENGTH_SHORT).show()
        }
    }
}
