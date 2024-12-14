package com.example.spoteam_android.ui.study.todolist

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemTodoListBinding

// MyTodoAdapter
class TodoAdapter(
    private val context: Context,
    private var todoList: MutableList<TodoTask>,       // TodoTask 리스트로 변경
    private val onAddTodo: (String) -> Unit,
    private val onCheckTodo: (Int) -> Unit            // 체크 시 ID를 전달하는 콜백
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemTodoListBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        if (todoList.isEmpty()) {
            Toast.makeText(context, "No items to display", Toast.LENGTH_SHORT).show()
            return
        }

        val item = todoList[position]

        // 텍스트 및 체크박스 초기 상태 설정
        holder.binding.tvTodoText.text = item.content
        holder.binding.cbTodo.visibility = if (item.content.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.tvTodoText.visibility = if (item.content.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.todoMoreButton.visibility = if (item.content.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.etTodo.visibility = if (item.content.isEmpty()) View.VISIBLE else View.GONE
        holder.binding.etTodo.setText(item.content)

        // EditText 변경 이벤트
        holder.binding.etTodo.setOnEditorActionListener { _, _, _ ->
            val newText = holder.binding.etTodo.text.toString()
            if (newText.isNotEmpty()) {
                todoList[position] = item.copy(content = newText)
                onAddTodo(newText)
                holder.binding.etTodo.post { notifyItemChanged(position) }
                true
            } else {
                false
            }
        }

        // 체크박스의 초기 상태에 따른 텍스트 스타일 설정
        holder.binding.tvTodoText.paintFlags = if (item.done) {
            holder.binding.tvTodoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.tvTodoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // 기존 리스너 제거 후 체크박스 상태 반영
        holder.binding.cbTodo.setOnCheckedChangeListener(null)  // 기존 리스너 제거
        holder.binding.cbTodo.isChecked = item.done             // 체크 상태 반영
        holder.binding.cbTodo.setOnCheckedChangeListener { _, isChecked ->
            if (item.done != isChecked) {
                todoList[position] = item.copy(done = isChecked)   // 상태 업데이트
                onCheckTodo(item.id)  // 체크 상태 변경 시 콜백 호출
            }
        }
    }

    override fun getItemCount(): Int = todoList.size

    fun updateData(newTodoList: List<TodoTask>) {
        todoList.clear()
        todoList.addAll(newTodoList)
        notifyDataSetChanged() // 데이터 변경 후 RecyclerView 갱신
    }

    fun getCurrentData(): List<TodoTask> {
        return todoList
    }

    fun addTodo() {
        todoList.add(TodoTask(0, "", "", false)) // 새로운 할 일 추가 (기본값 설정)
        notifyItemInserted(todoList.size - 1)
    }
}
