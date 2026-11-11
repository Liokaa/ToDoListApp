package com.example.todolist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoriaAdapter(
    private var listaCategorias: List<Categoria>,
    private val onItemClick: (Categoria) -> Unit,
    private val onEditClick: (Categoria) -> Unit,
    private val onDeleteClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewColorBarra: View = view.findViewById(R.id.viewColorBarra)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val cat = listaCategorias[position]
        holder.tvNombre.text = cat.nombre
        holder.tvDescripcion.text = cat.descripcion

        // Pintar la barra de color pastel
        try {
            holder.viewColorBarra.setBackgroundColor(Color.parseColor(cat.colorHex))
        } catch (e: Exception) {
            holder.viewColorBarra.setBackgroundColor(Color.parseColor("#C3B1E1"))
        }

        // Clic en la tarjeta: Ver las tareas asociadas
        holder.itemView.setOnClickListener {
            onItemClick(cat)
        }

        // Clic en el botón Editar
        holder.btnEditar.setOnClickListener {
            onEditClick(cat)
        }

        // Clic en el botón Eliminar
        holder.btnEliminar.setOnClickListener {
            onDeleteClick(cat)
        }
    }

    override fun getItemCount(): Int = listaCategorias.size

    fun actualizarLista(nuevaLista: List<Categoria>) {
        listaCategorias = nuevaLista
        notifyDataSetChanged()
    }
}