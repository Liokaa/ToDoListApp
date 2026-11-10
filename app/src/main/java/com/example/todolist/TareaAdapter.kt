package com.example.todolist

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TareaAdapter(
    private var listaTareas: List<Tarea>,
    private val onCheckChanged: (Tarea, Boolean) -> Unit,
    private val onDeleteClicked: (Tarea) -> Unit,
    private val onItemClicked: ((Tarea) -> Unit)? = null // Para abrir el diálogo de editar al hacer clic
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

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

        // 1. Título y Descripción
        holder.tvTitulo.text = tarea.titulo
        holder.tvDescripcion.text = tarea.descripcion

        // 2. Fecha (Formato DD/MM/YYYY)
        holder.tvFecha.text = if (tarea.fecha.isNotBlank()) "📅 ${tarea.fecha}" else "📅 Sin fecha"

        // 3. Categoría y Color dinámico
        val catNombre = if (tarea.categoria.isNotBlank()) tarea.categoria else "General"
        holder.tvCategoria.text = catNombre.uppercase()

        // Colores según tu categoría ("Cat 1", "Cat 2", "Cat 3" o por nombre)
        val colorHex = when (catNombre.lowercase()) {
            "cat 1", "trabajo" -> "#4A90E2"   // Azul
            "cat 2", "estudio" -> "#7E57C2"   // Morado
            "cat 3", "personal" -> "#66BB6A"  // Verde
            "hogar" -> "#FFA726"             // Naranja
            else -> "#70B5F6"                // Azul principal por defecto
        }

        val colorInt = Color.parseColor(colorHex)
        holder.viewColorCategoria.setBackgroundColor(colorInt)
        holder.tvCategoria.setTextColor(colorInt)

        // 4. Estado de CheckBox (Completada / Pendiente) y texto tachado
        holder.cbCompletada.setOnCheckedChangeListener(null)
        holder.cbCompletada.isChecked = tarea.completada

        if (tarea.completada) {
            holder.tvTitulo.paintFlags = holder.tvTitulo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTitulo.paintFlags = holder.tvTitulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // 5. Listeners de interacción
        holder.cbCompletada.setOnCheckedChangeListener { _, isChecked ->
            onCheckChanged(tarea, isChecked)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClicked(tarea)
        }

        holder.itemView.setOnClickListener {
            onItemClicked?.invoke(tarea)
        }
    }

    override fun getItemCount(): Int = listaTareas.size

    fun actualizarLista(nuevaLista: List<Tarea>) {
        listaTareas = nuevaLista
        notifyDataSetChanged()
    }
}