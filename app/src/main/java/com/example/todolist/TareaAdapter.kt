package com.example.todolist

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareaAdapter(
    private var listaTareas: List<Tarea>,
    private val onEstadoCambiado: (Tarea) -> Unit,
    private val onDeleteClicked: (Tarea) -> Unit,
    private val onItemClicked: ((Tarea) -> Unit)? = null
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    private val cacheColores = mutableMapOf<String, String>()

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColorCategoria: View = view.findViewById(R.id.viewColorCategoria)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoriaTarea)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaTarea)
        val cbCompletada: CheckBox = view.findViewById(R.id.cbCompletada)
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloTarea)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionTarea)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarTarea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = listaTareas[position]

        // 1. Manejo visual de estados: PENDIENTE, EN_PROGRESO, COMPLETADA
        holder.cbCompletada.setOnCheckedChangeListener(null)

        when (tarea.estado) {
            "EN_PROGRESO" -> {
                holder.cbCompletada.isChecked = false
                holder.tvTitulo.text = "⏳ ${tarea.titulo} (En Progreso)"
                holder.tvTitulo.paintFlags = holder.tvTitulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            "COMPLETADA" -> {
                holder.cbCompletada.isChecked = true
                holder.tvTitulo.text = tarea.titulo
                holder.tvTitulo.paintFlags = holder.tvTitulo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            else -> { // PENDIENTE
                holder.cbCompletada.isChecked = false
                holder.tvTitulo.text = tarea.titulo
                holder.tvTitulo.paintFlags = holder.tvTitulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        // 2. Descripción y Fecha
        holder.tvDescripcion.text = tarea.descripcion
        holder.tvFecha.text = if (tarea.fecha.isNotBlank()) "📅 ${tarea.fecha}" else "📅 Sin fecha"

        // 3. Cargar Categoría y su Color Pastel dinámico desde Room asociándolo al usuarioId de la tarea
        val catNombre = if (tarea.categoria.isNotBlank()) tarea.categoria else "General"
        holder.tvCategoria.text = catNombre.uppercase()

        val context = holder.itemView.context
        val activity = context as? AppCompatActivity

        if (cacheColores.containsKey(catNombre.lowercase())) {
            val colorHex = cacheColores[catNombre.lowercase()] ?: "#C3B1E1"
            aplicarColor(holder, colorHex)
        } else {
            activity?.lifecycleScope?.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(context)
                // Usamos la nueva función del DAO filtrando por el usuarioId de la tarea
                val listaCategorias = db.categoriaDao().obtenerTodasDeUsuario(tarea.usuarioId)
                val match = listaCategorias.find { it.nombre.equals(catNombre, ignoreCase = true) }

                val colorHex = match?.colorHex ?: "#C3B1E1"
                cacheColores[catNombre.lowercase()] = colorHex

                withContext(Dispatchers.Main) {
                    aplicarColor(holder, colorHex)
                }
            }
        }

        // 4. Cambiar de estado cíclicamente al pulsar el CheckBox: PENDIENTE -> EN_PROGRESO -> COMPLETADA -> PENDIENTE
        holder.cbCompletada.setOnClickListener {
            val siguienteEstado = when (tarea.estado) {
                "PENDIENTE" -> "EN_PROGRESO"
                "EN_PROGRESO" -> "COMPLETADA"
                else -> "PENDIENTE"
            }
            val esCompletada = siguienteEstado == "COMPLETADA"
            val tareaActualizada = tarea.copy(estado = siguienteEstado, completada = esCompletada)
            onEstadoCambiado(tareaActualizada)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClicked(tarea)
        }

        holder.itemView.setOnClickListener {
            onItemClicked?.invoke(tarea)
        }
    }

    private fun aplicarColor(holder: TareaViewHolder, colorHex: String) {
        try {
            val colorInt = Color.parseColor(colorHex)
            holder.viewColorCategoria.setBackgroundColor(colorInt)
            holder.tvCategoria.setTextColor(colorInt)
        } catch (e: Exception) {
            val colorDefault = Color.parseColor("#C3B1E1")
            holder.viewColorCategoria.setBackgroundColor(colorDefault)
            holder.tvCategoria.setTextColor(colorDefault)
        }
    }

    override fun getItemCount(): Int = listaTareas.size

    fun actualizarLista(nuevaLista: List<Tarea>) {
        listaTareas = nuevaLista
        cacheColores.clear()
        notifyDataSetChanged()
    }
}