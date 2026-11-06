package com.example.todolist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class InterfazLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_login)

        val btnLogin = findViewById<View>(R.id.btnLogin)
        val btnRegister = findViewById<View>(R.id.btnRegister)
        val btnGoogle = findViewById<ImageView>(R.id.btnGoogle)
        val btnFacebook = findViewById<ImageView>(R.id.btnFacebook)
        val btnInstagram = findViewById<ImageView>(R.id.btnInstagram)

        btnLogin.setOnClickListener {
            val intent = Intent(this, InterfazLista::class.java)
            startActivity(intent)
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