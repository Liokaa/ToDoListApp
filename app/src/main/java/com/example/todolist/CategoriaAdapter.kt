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

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val viewColorBarra: View = itemView.findViewById(R.id.viewColorBarra)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = listaCategorias[position]

        holder.tvNombre.text = categoria.nombre
        holder.tvDescripcion.text = categoria.descripcion

        // Asignar el color Hexadecimal a la barra lateral del item
        try {
            holder.viewColorBarra.setBackgroundColor(Color.parseColor(categoria.colorHex))
        } catch (e: Exception) {
            holder.viewColorBarra.setBackgroundColor(Color.parseColor("#C3B1E1"))
        }

        // Eventos de clic
        holder.itemView.setOnClickListener { onItemClick(categoria) }
        holder.btnEditar.setOnClickListener { onEditClick(categoria) }
        holder.btnEliminar.setOnClickListener { onDeleteClick(categoria) }
    }

    override fun getItemCount(): Int = listaCategorias.size

    fun actualizarLista(nuevaLista: List<Categoria>) {
        listaCategorias = nuevaLista
        notifyDataSetChanged()
    }
}