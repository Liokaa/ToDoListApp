package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InterfazRegistro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_registro)

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnCreateAccount.setOnClickListener {
            val intent = Intent(this, InterfazLogin::class.java)
            startActivity(intent)
            finish()
        }

        btnCancel.setOnClickListener {
            val intent = Intent(this, InterfazLogin::class.java)
            startActivity(intent)
            finish()
        }
    }
}