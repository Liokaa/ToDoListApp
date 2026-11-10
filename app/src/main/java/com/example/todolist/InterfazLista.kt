package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class InterfazLista : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_lista)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val burguer = findViewById<MaterialToolbar>(R.id.Burguer)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val btnUsuario = findViewById<ImageButton>(R.id.btnUsuario)

        burguer.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_lista -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_calendario -> {
                    startActivity(Intent(this, InterfazCalendario::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_categoria -> {
                    startActivity(Intent(this, InterfazCategoria::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_papelera -> {
                    startActivity(Intent(this, InterfazPapelera::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_usuario -> {
                    startActivity(Intent(this, InterfazUsuario::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }

        btnUsuario.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }
    }
}