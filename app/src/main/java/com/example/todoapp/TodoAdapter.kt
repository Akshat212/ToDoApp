package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val list : List<ToDoModel>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false))
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun getItemCount(): Int = list.size

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(toDoModel: ToDoModel) {
            with(itemView) {
                val colors = resources.getIntArray(R.array.random_color)
                val randomColor = colors[Random().nextInt(colors.size)]

                viewColorTag.setBackgroundColor(randomColor)
                tvTitle.text = toDoModel.title
                tvTitleDesc.text = toDoModel.description
                tvCategory.text = toDoModel.category
                tvDate.text = getDate(toDoModel.date)
                tvTime.text = getTime(toDoModel.time)
            }
        }

        private fun getTime(time: Long): CharSequence? {
            val tm = "h:m a"
            val sdf = SimpleDateFormat(tm)

            return sdf.format(Date(time))
        }

        private fun getDate(date: Long): CharSequence? {
            val format = "EEE, d MMM yyyy"
            val sdf = SimpleDateFormat(format)

            return sdf.format(Date(date))
        }
    }
}