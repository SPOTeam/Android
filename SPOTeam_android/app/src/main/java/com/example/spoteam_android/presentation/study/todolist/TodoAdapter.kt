package com.example.spoteam_android.presentation.study.todolist

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

        // 수정 상태에 따른 UI 표시
        if (item.isEditing) {
            holder.binding.etTodo.visibility = View.VISIBLE
            holder.binding.tvTodoText.visibility = View.GONE
            holder.binding.etTodo.setText(item.content) // 기존 내용 설정
            holder.binding.etTodo.requestFocus()
        } else {
            holder.binding.etTodo.visibility = View.GONE
            holder.binding.tvTodoText.visibility = View.VISIBLE
            holder.binding.tvTodoText.text = item.content

            updateTodoStyle(holder, item.done)
        }



        // 키보드 "완료" 버튼 처리
        holder.binding.etTodo.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)) {

                val newContent = holder.binding.etTodo.text.toString().trim()

                if (newContent.isEmpty()) {
                    // 입력값이 비어있을 경우 Toast 메시지 표시
                    Toast.makeText(context, "내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
                    // EditText는 그대로 유지
                    return@setOnEditorActionListener true
                }

                if (newContent != item.content) {
                    if (item.isNew) {
                        // AddTodo API 호출
                        repository.addTodoItem(
                            studyId = studyId,
                            content = newContent,
                            date = item.date
                        ) { response ->
                            // 서버 응답 여부와 관계없이 EditText 닫기 처리
                            item.isNew = false
                            item.content = newContent
                            item.isEditing = false
                            notifyItemChanged(position)

                            if (response != null) {
                                Toast.makeText(context, "추가 성공!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "추가 실패: 서버 연결 오류", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // 기존 항목 -> UpdateTodo API 호출
                        repository.updateTodo(
                            studyId = studyId,
                            toDoId = item.id,
                            content = newContent,
                            date = item.date
                        ) { response ->
                            if (response != null) {
                                Toast.makeText(context, "수정 성공!", Toast.LENGTH_SHORT).show()
                                item.content = newContent
                                item.isEditing = false
                                notifyItemChanged(position)
                            } else {
                                Toast.makeText(context, "수정 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // 내용이 변경되지 않은 경우
                    item.isEditing = false
                    notifyItemChanged(position)
                }
                true
            } else {
                false
            }
        }


        // "더보기" 버튼 클릭 이벤트
        holder.binding.todoMoreButton.setOnClickListener { view ->
            showPopupWindow(view, item)
        }

        // 체크박스 상태 처리
        holder.binding.cbTodo.setOnCheckedChangeListener(null) // 기존 리스너 제거
        holder.binding.cbTodo.isChecked = item.done
        holder.binding.cbTodo.setOnCheckedChangeListener { _, isChecked ->
            if (item.done != isChecked) {
                item.done = isChecked
                onCheckTodo(item.id) // 체크 상태 변경 시 콜백 호출
                updateTodoStyle(holder, isChecked)
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

    private fun updateTodoStyle(holder: TodoViewHolder, isDone: Boolean) {
        val colorRes = if (isDone) R.color.g400 else R.color.black
        holder.binding.tvTodoText.setTextColor(ContextCompat.getColor(context, colorRes))
    }

    fun addTodo(selectedDate: String) {
        val newTodo = TodoTask(
            id = 0,
            content = "",
            date = selectedDate, // 필요시 초기 날짜 설정
            done = false,
            isEditing = true,
            isNew = true // 새로운 항목
        )
        todoList.add(newTodo)
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

        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.custom_popup_background))
        popupWindow.isOutsideTouchable = true

        popupBinding.deleteTodos.setOnClickListener {
            repository.deleteTodo(studyId, todoTask.id) { response ->
                if (response != null) {
                    Toast.makeText(context, "삭제 성공!", Toast.LENGTH_SHORT).show()
                    removeTodoFromList(todoTask)
                } else {
                    Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
                }
                popupWindow.dismiss()
            }
        }

        popupBinding.editTodos.setOnClickListener {
            popupWindow.dismiss()
            val position = todoList.indexOf(todoTask)
            if (position != -1) {
                todoTask.isEditing = true
                notifyItemChanged(position) // 수정 상태 활성화
            }
        }

        popupWindow.showAsDropDown(anchorView, -290, -20)
    }


    private fun removeTodoFromList(todoTask: TodoTask) {
        val position = todoList.indexOf(todoTask)
        if (position != -1) {
            todoList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun cancelIfEditing() {
        val editingIndex = todoList.indexOfFirst { it.isEditing && it.isNew }
        if (editingIndex != -1) {
            todoList.removeAt(editingIndex)
            notifyItemRemoved(editingIndex)
        }
    }
}
