package com.example.spoteam_android.ui.study.todolist

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemTodoListBinding

// 다른 스터디원 Todo apapter
class OtherTodoAdapter(
    private val context: Context,
    private var todoList: MutableList<TodoTask>,
) : RecyclerView.Adapter<OtherTodoAdapter.OtherTodoViewHolder>() {

    // ViewHolder 정의
    inner class OtherTodoViewHolder(val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherTodoViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemTodoListBinding.inflate(inflater, parent, false)
        return OtherTodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OtherTodoViewHolder, position: Int) {
        val item = todoList[position]
        Log.d("OtherTodoAdapter", "Binding position: $position with data: $item")

        // 텍스트와 체크박스 설정
        holder.binding.todoContent.text = item.content
        holder.binding.todoCheckbox.paintFlags = if (item.done) {
            holder.binding.todoCheckbox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.todoCheckbox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.binding.todoCheckbox.isChecked = item.done
        holder.binding.todoCheckbox.isEnabled = false
    }

    override fun getItemCount(): Int = todoList.size

    fun updateData(newTodoList: List<TodoTask>) {
        todoList.clear()
        todoList.addAll(newTodoList)
        notifyDataSetChanged()
    }

    fun clearData(){
        todoList.clear()
        Log.d("OtherTodoAdapter","clearData 실행")
        notifyDataSetChanged()
    }


}
