package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class InterfazCarga : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_carga)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, InterfazLogin::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}