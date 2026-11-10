package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
data class ElementoPapelera(
    val nombre: String,
    val descripcion: String
)

class PapeleraAdapter(
    private val lista: MutableList<ElementoPapelera>,
    private val onItemClickDelete: (ElementoPapelera) -> Unit
) : RecyclerView.Adapter<PapeleraAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreItem)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionItem)
        val btnEliminar: ImageView = view.findViewById(R.id.btnEliminarItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_papelera, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.nombre
        holder.tvDescripcion.text = item.descripcion
        holder.btnEliminar.setOnClickListener {
            onItemClickDelete(item)
        }
    }

    override fun getItemCount(): Int = lista.size
}