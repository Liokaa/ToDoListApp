package com.example.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class InterfazCategoria : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvCategorias: RecyclerView
    private lateinit var adapter: CategoriaAdapter
    private lateinit var etBuscar: EditText
    private lateinit var drawerLayout: DrawerLayout

    private var idUsuarioLogueado: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_categoria)

        db = AppDatabase.getDatabase(this)

        // Obtener el ID del usuario con la sesión activa
        val prefs = getSharedPreferences("MiSesionApp", Context.MODE_PRIVATE)
        idUsuarioLogueado = prefs.getInt("ID_USUARIO_LOGUEADO", -1)

        drawerLayout = findViewById(R.id.drawerLayout)
        val tvMenu = findViewById<MaterialToolbar>(R.id.tvMenu)
        val tvUser = findViewById<ImageButton>(R.id.tvUser)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        etBuscar = findViewById(R.id.etBuscar)
        rvCategorias = findViewById(R.id.rvCategorias)

        // Configuración del RecyclerView usando las referencias corregidas del adapter
        rvCategorias.layoutManager = LinearLayoutManager(this)
        adapter = CategoriaAdapter(
            listaCategorias = emptyList(),
            onItemClick = { categoria -> mostrarTareasDeCategoria(categoria) },
            onEditClick = { categoria -> mostrarDialogoEditarCategoria(categoria) },
            onDeleteClick = { categoria -> confirmarEliminarCategoria(categoria) }
        )
        rvCategorias.adapter = adapter

        // Buscador filtrando solo entre categorías del usuario activo
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarCategoriasEnRoom(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tvMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        tvMenu.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_calendario -> startActivity(Intent(this, InterfazCalendario::class.java))
                R.id.nav_papelera -> startActivity(Intent(this, InterfazPapelera::class.java))
                R.id.nav_categoria -> { /* Ya estamos aquí */ }
                R.id.nav_usuario -> startActivity(Intent(this, InterfazUsuario::class.java))
                else -> startActivity(Intent(this, InterfazLista::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        tvUser.setOnClickListener {
            startActivity(Intent(this, InterfazUsuario::class.java))
        }

        fabAdd.setOnClickListener {
            mostrarDialogoNuevaCategoria()
        }

        cargarCategorias()
    }

    private fun cargarCategorias() {
        lifecycleScope.launch {
            if (idUsuarioLogueado != -1) {
                val lista = db.categoriaDao().obtenerTodasDeUsuario(idUsuarioLogueado)
                adapter.actualizarLista(lista)
            }
        }
    }

    private fun buscarCategoriasEnRoom(query: String) {
        lifecycleScope.launch {
            if (query.isBlank()) {
                cargarCategorias()
            } else if (idUsuarioLogueado != -1) {
                val listaFiltrada = db.categoriaDao().buscarCategorias(idUsuarioLogueado, query)
                adapter.actualizarLista(listaFiltrada)
            }
        }
    }

    private fun mostrarDialogoNuevaCategoria() {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_nueva_categoria, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreCategoria)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionCategoria)

        var colorHexSeleccionado = "#C3B1E1"

        val coloresMap = mapOf(
            R.id.c1 to "#C3B1E1", R.id.c2 to "#FDFD96", R.id.c3 to "#F8C8DC",
            R.id.c4 to "#A2DEFF", R.id.c5 to "#B2FBA5", R.id.c6 to "#FFDAC1",
            R.id.c7 to "#E2F0CB", R.id.c8 to "#FFB7B2", R.id.c9 to "#D0F4DE",
            R.id.c10 to "#FCF6BD", R.id.c11 to "#D9C5F2", R.id.c12 to "#B5EAD7"
        )

        coloresMap.forEach { (btnId, hex) ->
            dialogView.findViewById<Button>(btnId)?.setOnClickListener {
                colorHexSeleccionado = hex
                Toast.makeText(this, "Color seleccionado", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nombre = etNombre.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()

                if (nombre.isNotEmpty()) {
                    lifecycleScope.launch {
                        val existentes = db.categoriaDao().obtenerTodasDeUsuario(idUsuarioLogueado)
                        val colorUsado = existentes.any { it.colorHex.equals(colorHexSeleccionado, ignoreCase = true) }

                        if (colorUsado) {
                            Toast.makeText(this@InterfazCategoria, "Ese color ya lo usa otra categoría, elige otro.", Toast.LENGTH_LONG).show()
                        } else {
                            val nuevaCat = Categoria(
                                usuarioId = idUsuarioLogueado,
                                nombre = nombre,
                                descripcion = descripcion,
                                colorHex = colorHexSeleccionado
                            )
                            db.categoriaDao().insertar(nuevaCat)
                            Toast.makeText(this@InterfazCategoria, "Categoría '$nombre' creada", Toast.LENGTH_SHORT).show()
                            cargarCategorias()
                        }
                    }
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun mostrarDialogoEditarCategoria(categoria: Categoria) {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_nueva_categoria, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreCategoria)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionCategoria)

        etNombre.setText(categoria.nombre)
        etDescripcion.setText(categoria.descripcion)

        var colorHexSeleccionado = categoria.colorHex

        val coloresMap = mapOf(
            R.id.c1 to "#C3B1E1", R.id.c2 to "#FDFD96", R.id.c3 to "#F8C8DC",
            R.id.c4 to "#A2DEFF", R.id.c5 to "#B2FBA5", R.id.c6 to "#FFDAC1",
            R.id.c7 to "#E2F0CB", R.id.c8 to "#FFB7B2", R.id.c9 to "#D0F4DE",
            R.id.c10 to "#FCF6BD", R.id.c11 to "#D9C5F2", R.id.c12 to "#B5EAD7"
        )

        coloresMap.forEach { (btnId, hex) ->
            dialogView.findViewById<Button>(btnId)?.setOnClickListener {
                colorHexSeleccionado = hex
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Categoría")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { dialog, _ ->
                val nuevoNombre = etNombre.text.toString().trim()
                val nuevaDesc = etDescripcion.text.toString().trim()

                if (nuevoNombre.isNotEmpty()) {
                    lifecycleScope.launch {
                        val existentes = db.categoriaDao().obtenerTodasDeUsuario(idUsuarioLogueado)
                        val colorUsadoPorOtra = existentes.any {
                            it.id != categoria.id && it.colorHex.equals(colorHexSeleccionado, ignoreCase = true)
                        }

                        if (colorUsadoPorOtra) {
                            Toast.makeText(this@InterfazCategoria, "Ese color ya pertenece a otra categoría", Toast.LENGTH_LONG).show()
                        } else {
                            val catActualizada = categoria.copy(
                                nombre = nuevoNombre,
                                descripcion = nuevaDesc,
                                colorHex = colorHexSeleccionado
                            )
                            db.categoriaDao().actualizar(catActualizada)
                            cargarCategorias()
                            Toast.makeText(this@InterfazCategoria, "Categoría actualizada", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminarCategoria(categoria: Categoria) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Categoría")
            .setMessage("¿Estás seguro de eliminar la categoría '${categoria.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    db.categoriaDao().eliminar(categoria)
                    cargarCategorias()
                    Toast.makeText(this@InterfazCategoria, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarTareasDeCategoria(categoria: Categoria) {
        lifecycleScope.launch {
            val tareas = db.categoriaDao().obtenerTareasPorCategoria(idUsuarioLogueado, categoria.nombre)
            val listaTextos = if (tareas.isNotEmpty()) {
                tareas.map { "• ${it.titulo}" }.toTypedArray()
            } else {
                arrayOf("No hay tareas asignadas a esta categoría.")
            }

            AlertDialog.Builder(this@InterfazCategoria)
                .setTitle("Tareas en '${categoria.nombre}'")
                .setItems(listaTextos, null)
                .setPositiveButton("Aceptar", null)
                .show()
        }
    }
}