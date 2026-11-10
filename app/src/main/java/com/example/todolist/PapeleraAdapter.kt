package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PapeleraAdapter(
    private var listaTareas: List<Tarea>,
    private val onItemClick: (Tarea) -> Unit,
    private val onDeleteClick: (Tarea) -> Unit
) : RecyclerView.Adapter<PapeleraAdapter.PapeleraViewHolder>() {

    class PapeleraViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreItem)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionItem)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminarItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PapeleraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_papelera, parent, false)
        return PapeleraViewHolder(view)
    }

    override fun onBindViewHolder(holder: PapeleraViewHolder, position: Int) {
        val tarea = listaTareas[position]

        holder.tvNombre.text = tarea.titulo
        holder.tvDescripcion.text = if (tarea.descripcion.isNotBlank()) tarea.descripcion else "Sin descripción"

        // Clic en la tarjeta para restaurar
        holder.itemView.setOnClickListener {
            onItemClick(tarea)
        }

        // Clic en el icono 'X' para eliminar definitivamente
        holder.btnEliminar.setOnClickListener {
            onDeleteClick(tarea)
        }
    }

    override fun getItemCount(): Int = listaTareas.size

    fun actualizarLista(nuevaLista: List<Tarea>) {
        listaTareas = nuevaLista
        notifyDataSetChanged()
    }
}