package com.example.todolist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoriaAdapter(
    private var listaCategorias: List<Categoria>,
    private val onItemClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val viewColorBarra: View = view.findViewById(R.id.viewColorBarra)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = listaCategorias[position]

        holder.tvNombre.text = categoria.nombre
        holder.tvDescripcion.text = if (categoria.descripcion.isNotBlank()) categoria.descripcion else "Sin descripción"

        // Pintar la barra con el color guardado en la base de datos
        try {
            val colorInt = Color.parseColor(categoria.colorHex)
            holder.viewColorBarra.setBackgroundColor(colorInt)
        } catch (e: Exception) {
            holder.viewColorBarra.setBackgroundColor(Color.parseColor("#4A90E2"))
        }

        holder.itemView.setOnClickListener {
            onItemClick(categoria)
        }
    }

    override fun getItemCount(): Int = listaCategorias.size

    fun actualizarLista(nuevaLista: List<Categoria>) {
        listaCategorias = nuevaLista
        notifyDataSetChanged()
    }
}