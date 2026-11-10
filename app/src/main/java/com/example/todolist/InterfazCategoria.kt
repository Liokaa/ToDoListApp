package com.example.todolist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class InterfazCategoria : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var rvCategorias: RecyclerView
    private lateinit var adapter: CategoriaAdapter
    private lateinit var etBuscar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_categoria)

        db = AppDatabase.getDatabase(this)

        val tvMenu = findViewById<TextView>(R.id.tvMenu)
        val tvUser = findViewById<TextView>(R.id.tvUser)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        etBuscar = findViewById(R.id.etBuscar)
        rvCategorias = findViewById(R.id.rvCategorias)

        // Configurar RecyclerView usando CategoriaAdapter
        rvCategorias.layoutManager = LinearLayoutManager(this)
        adapter = CategoriaAdapter(
            listaCategorias = emptyList(),
            onItemClick = { categoria ->
                Toast.makeText(this, "Categoría: ${categoria.nombre}", Toast.LENGTH_SHORT).show()
            }
        )
        rvCategorias.adapter = adapter

        // Búsqueda en tiempo real de categorías
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarCategoriasEnRoom(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tvMenu?.setOnClickListener { finish() }

        tvUser?.setOnClickListener {
            Toast.makeText(this, "Perfil de usuario", Toast.LENGTH_SHORT).show()
        }

        // Botón '+' para agregar nueva categoría con colores pastel
        fabAdd.setOnClickListener {
            mostrarDialogoNuevaCategoria()
        }

        cargarCategorias()
    }

    private fun cargarCategorias() {
        lifecycleScope.launch {
            val lista = db.categoriaDao().obtenerTodas()
            adapter.actualizarLista(lista)
        }
    }

    private fun buscarCategoriasEnRoom(query: String) {
        lifecycleScope.launch {
            if (query.isBlank()) {
                cargarCategorias()
            } else {
                val listaFiltrada = db.categoriaDao().buscarCategorias(query)
                adapter.actualizarLista(listaFiltrada)
            }
        }
    }

    private fun mostrarDialogoNuevaCategoria() {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_nueva_categoria, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreCategoria)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionCategoria)

        val btnMorado = dialogView.findViewById<Button>(R.id.btnColorMorado)
        val btnAmarillo = dialogView.findViewById<Button>(R.id.btnColorAmarillo)
        val btnRosa = dialogView.findViewById<Button>(R.id.btnColorRosa)
        val btnAzul = dialogView.findViewById<Button>(R.id.btnColorAzul)
        val btnVerde = dialogView.findViewById<Button>(R.id.btnColorVerde)

        // Color morado pastel por defecto
        var colorHexSeleccionado = "#C3B1E1"

        btnMorado.setOnClickListener {
            colorHexSeleccionado = "#C3B1E1"
            Toast.makeText(this, "Color Morado Pastel seleccionado", Toast.LENGTH_SHORT).show()
        }
        btnAmarillo.setOnClickListener {
            colorHexSeleccionado = "#FDFD96"
            Toast.makeText(this, "Color Amarillo Pastel seleccionado", Toast.LENGTH_SHORT).show()
        }
        btnRosa.setOnClickListener {
            colorHexSeleccionado = "#F8C8DC"
            Toast.makeText(this, "Color Rosa Pastel seleccionado", Toast.LENGTH_SHORT).show()
        }
        btnAzul.setOnClickListener {
            colorHexSeleccionado = "#A2DEFF"
            Toast.makeText(this, "Color Azul Pastel seleccionado", Toast.LENGTH_SHORT).show()
        }
        btnVerde.setOnClickListener {
            colorHexSeleccionado = "#B2FBA5"
            Toast.makeText(this, "Color Verde Pastel seleccionado", Toast.LENGTH_SHORT).show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nombre = etNombre.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()

                if (nombre.isNotEmpty()) {
                    lifecycleScope.launch {
                        val nuevaCat = Categoria(
                            nombre = nombre,
                            descripcion = descripcion,
                            colorHex = colorHexSeleccionado
                        )
                        db.categoriaDao().insertar(nuevaCat)
                        Toast.makeText(this@InterfazCategoria, "Categoría '$nombre' creada", Toast.LENGTH_SHORT).show()
                        cargarCategorias()
                    }
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .show()
    }
}