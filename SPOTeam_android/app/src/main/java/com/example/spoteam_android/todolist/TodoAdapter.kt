package com.example.spoteam_android.todolist

import android.content.Context
import android.graphics.Paint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.TodolistItemBinding

class TodoAdapter(
    private val context: Context,
    private val todoList: MutableList<String>,
    private val onContentEntered: (String) -> Unit // 입력된 텍스트를 전달하는 콜백
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val isEditableList = MutableList(todoList.size) { true } // 각 항목이 EditText인지 여부를 저장

    inner class TodoViewHolder(val binding: TodolistItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = TodolistItemBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = todoList[position]
        val isEditable = isEditableList[position]

        if (isEditable) {
            holder.binding.etTodo.visibility = View.VISIBLE
            holder.binding.cbTodo.visibility = View.GONE
            holder.binding.etTodo.setText(item)

            holder.binding.etTodo.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {

                    val text = holder.binding.etTodo.text.toString()
                    if (text.isNotBlank()) {
                        todoList[position] = text
                        onContentEntered(text)  // 입력된 텍스트를 콜백으로 전달
                        isEditableList[position] = false // API 요청 후 TextView로 전환
                        notifyItemChanged(position) // 상태 업데이트
                        hideKeyboard(v)
                    }
                    true
                } else {
                    false
                }
            }
        } else {
            holder.binding.etTodo.visibility = View.GONE
            holder.binding.cbTodo.visibility = View.VISIBLE
            holder.binding.cbTodo.text = item
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

    override fun getItemCount(): Int = todoList.size

    fun addTodo() {
        todoList.add("")
        isEditableList.add(true) // 새 항목은 EditText 상태로 추가
        notifyItemInserted(todoList.size - 1)
    }

    private fun hideKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
