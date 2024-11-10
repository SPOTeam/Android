package com.example.spoteam_android.todolist

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.TodolistItemBinding

class TodoAdapter(
    private val context: Context,
    private var todoList: MutableList<String>,
    private val onAddTodo: (String) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(val binding: TodolistItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = TodolistItemBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        if (position >= todoList.size || todoList.isEmpty()) {
            // 데이터가 비어 있을 경우 리턴
            Toast.makeText(context, "No items to display", Toast.LENGTH_SHORT).show()
            return
        }

        val item = todoList[position]

        // 초기 상태 설정
        holder.binding.cbTodo.text = item
        holder.binding.cbTodo.visibility = if (item.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.etTodo.visibility = if (item.isEmpty()) View.VISIBLE else View.GONE

        holder.binding.etTodo.setText(item)

        holder.binding.etTodo.setOnEditorActionListener { v, actionId, _ ->
            val newText = holder.binding.etTodo.text.toString()
            if (newText.isNotEmpty()) {
                todoList[position] = newText  // 리스트 항목 업데이트
                onAddTodo(newText)  // 콜백 실행 (Todo 추가 이벤트)
                notifyItemChanged(position)  // UI 업데이트
                true
            } else {
                false
            }
        }

        // 체크박스의 초기 체크 상태에 따른 텍스트 스타일 및 색상 설정
        if (holder.binding.cbTodo.isChecked) {
            holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.binding.cbTodo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    override fun getItemCount(): Int = todoList.size

    fun updateData(newTodoList: List<String>) {
        todoList.clear()
        todoList.addAll(newTodoList)
        notifyDataSetChanged() // 데이터 변경 후 RecyclerView 갱신
    }

    fun getCurrentData(): List<String> {
        return todoList
    }

    fun addTodo() {
        todoList.add("")
        notifyItemInserted(todoList.size - 1)
    }
}
