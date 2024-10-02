package com.example.spoteam_android.todolist

import android.content.Context
import android.graphics.Paint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.TodolistItemBinding

class TodoAdapter(private val context: Context, private val todoList: MutableList<String>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(val binding: TodolistItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = TodolistItemBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = todoList[position]

        // 초기 상태 설정
        holder.binding.cbTodo.text = item
        holder.binding.cbTodo.visibility = if (item.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.etTodo.visibility = if (item.isEmpty()) View.VISIBLE else View.GONE

        holder.binding.etTodo.setText(item)

        // 체크박스의 초기 체크 상태에 따른 텍스트 스타일 및 색상 설정
        if (holder.binding.cbTodo.isChecked) {
            holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.cbTodo.setTextColor(ContextCompat.getColor(context, R.color.gray_03))
        } else {
            holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.cbTodo.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        // EditText에서 엔터를 치거나 입력이 완료되었을 때 처리
        holder.binding.etTodo.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {

                val text = holder.binding.etTodo.text.toString()
                if (text.isNotBlank()) {
                    todoList[position] = text
                    notifyItemChanged(position)
                    hideKeyboard(v)  // 키보드 숨김
                }
                true
            } else {
                false
            }
        }

        // 체크박스 클릭 시 텍스트 스타일 및 색상 변경
        holder.binding.cbTodo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.cbTodo.setTextColor(ContextCompat.getColor(context, R.color.gray_03))
            } else {
                holder.binding.cbTodo.paintFlags = holder.binding.cbTodo.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.binding.cbTodo.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun addTodo() {
        todoList.add("") // 빈 항목 추가
        notifyItemInserted(todoList.size - 1)
    }

    private fun hideKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
