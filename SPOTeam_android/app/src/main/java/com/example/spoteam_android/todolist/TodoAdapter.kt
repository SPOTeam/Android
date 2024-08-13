package com.example.spoteam_android.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R

class TodoAdapter(private var todos: List<String>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todolist_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.bind(todo)
    }

    override fun getItemCount(): Int = todos.size

    fun updateTodos(newTodos: List<String>) {
        todos = newTodos
        notifyDataSetChanged()
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
//        private val textView: TextView = itemView.findViewById(R.id.tv_todo_item)

        fun bind(todo: String) {
//            checkBox.isChecked = false
//            textView.text = todo
        }
    }
}

