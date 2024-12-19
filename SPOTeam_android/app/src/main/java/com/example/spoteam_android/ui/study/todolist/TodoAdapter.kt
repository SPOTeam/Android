package com.example.spoteam_android.ui.study.todolist

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.CustomPopupMenuBinding
import com.example.spoteam_android.databinding.ItemTodoListBinding

// TodoAdapter
class TodoAdapter(
    private val context: Context,
    private var todoList: MutableList<TodoTask>,       // TodoTask 리스트로 변경
    private val onAddTodo: (String) -> Unit,
    private val onCheckTodo: (Int) -> Unit,           // 체크 시 ID를 전달하는 콜백
    private val repository: TodoRepository,
    private val studyId: Int                          // 추가된 스터디 ID
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemTodoListBinding.inflate(inflater, parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = todoList[position]

        // 텍스트 및 체크박스 초기 상태 설정
        holder.binding.tvTodoText.text = item.content
        holder.binding.cbTodo.visibility = if (item.content.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.tvTodoText.visibility = if (item.content.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.todoMoreButton.visibility = if (item.content.isEmpty()) View.GONE else View.VISIBLE
        holder.binding.etTodo.visibility = if (item.content.isEmpty()) View.VISIBLE else View.GONE
        holder.binding.etTodo.setText(item.content)

        // "더보기" 버튼 클릭 시 PopupWindow 표시
        holder.binding.todoMoreButton.setOnClickListener { view ->
            showPopupWindow(view, item) // 현재 TodoTask 전달
        }

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

        // 체크박스 상태 반영
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

    fun showPopupWindow(anchorView: View, todoTask: TodoTask) {
        val inflater = LayoutInflater.from(context)
        val popupBinding = CustomPopupMenuBinding.inflate(inflater)

        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // 배경 클릭 시 닫힘 설정
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.custom_popup_background))
        popupWindow.isOutsideTouchable = true

        // 삭제 버튼 클릭 이벤트
        popupBinding.deleteTodos.setOnClickListener {
            repository.deleteTodo(studyId, todoTask.id) { response -> // studyId와 todoId 전달
                if (response != null) {
                    Toast.makeText(context, "삭제 성공!", Toast.LENGTH_SHORT).show()
                    removeTodoFromList(todoTask) // RecyclerView 갱신
                } else {
                    Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
                }
                popupWindow.dismiss()
            }
        }

        // 수정 버튼 클릭 이벤트
        popupBinding.editTodos.setOnClickListener {
            Toast.makeText(context, "수정할 아이디: ${todoTask.id}", Toast.LENGTH_SHORT).show()
            // 수정 로직 추가
            popupWindow.dismiss()
        }

        // PopupWindow 표시
        popupWindow.showAsDropDown(anchorView, -290, -20)
    }

    private fun removeTodoFromList(todoTask: TodoTask) {
        val position = todoList.indexOf(todoTask)
        if (position != -1) {
            todoList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
