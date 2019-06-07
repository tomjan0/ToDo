package com.example.todo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.single_task.view.*

class TaskAdapter(val items: ArrayList<Task>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    private val TAG = "TaskAdapter"

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.single_task,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        holder.nameTextView.text = item.name
        holder.dateTextView.text = item.date
        when (item.priority) {
            0 -> holder.priorityTextView.text = "mało ważne"
            1 -> holder.priorityTextView.text = "normalne"
            2 -> holder.priorityTextView.text = "bardzo ważne"
            else -> holder.priorityTextView.text = "nieokreślony"
        }
        when (item.type) {
            "praca" -> holder.typeImageView.setImageResource(R.drawable.ic_work_black_24dp)
            "uczelnia" -> holder.typeImageView.setImageResource(R.drawable.ic_local_library_black_24dp)
            "zakupy" -> holder.typeImageView.setImageResource(R.drawable.ic_local_grocery_store_black_24dp)
            "auto" -> holder.typeImageView.setImageResource(R.drawable.ic_directions_car_black_24dp)
        }

        holder.itemView.setOnLongClickListener {
            (context as MainActivity).itemClick(position)
            true
        }

    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val nameTextView = view.taskName
    val dateTextView = view.taskDate
    val priorityTextView = view.taskPriority
    val typeImageView = view.typeImage

}


