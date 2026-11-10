package com.example.todolist

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
    private val onDeleteClicked: (Tarea) -> Unit
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.tvTitulo.text = tarea.titulo
        holder.tvDescripcion.text = tarea.descripcion

        holder.cbCompletada.setOnCheckedChangeListener(null)
        holder.cbCompletada.isChecked = tarea.completada

        if (tarea.completada) {
            holder.tvTitulo.paintFlags = holder.tvTitulo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTitulo.paintFlags = holder.tvTitulo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.cbCompletada.setOnCheckedChangeListener { _, isChecked ->
            onCheckChanged(tarea, isChecked)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClicked(tarea)
        }
    }

    override fun getItemCount(): Int = listaTareas.size

    fun actualizarLista(nuevaLista: List<Tarea>) {
        listaTareas = nuevaLista
        notifyDataSetChanged()
    }
}