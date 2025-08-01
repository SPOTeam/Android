package com.umcspot.android.ui.study.todolist

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umcspot.android.R
import com.umcspot.android.databinding.ItemViewTodoListBinding

// 다른 스터디원 Todo apapter
class OtherTodoAdapter(
    private val context: Context,
    private var todoList: MutableList<TodoTask>,
) : RecyclerView.Adapter<OtherTodoAdapter.OtherTodoViewHolder>() {

    // ViewHolder 정의
    inner class OtherTodoViewHolder(val binding: ItemViewTodoListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherTodoViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemViewTodoListBinding.inflate(inflater, parent, false)
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

        val textColorRes = if (item.done) R.color.g400 else R.color.black
        holder.binding.todoContent.setTextColor(context.getColor(textColorRes))

        holder.binding.todoCheckbox.isChecked = item.done
        holder.binding.todoCheckbox.isEnabled = false
    }

    override fun getItemCount(): Int = todoList.size

    fun updateData(newTodoList: List<TodoTask>) {
        todoList.clear()
        todoList.addAll(newTodoList)
        notifyDataSetChanged()
        Log.d("TodoFramgment_other","updateData complete")

    }

    fun clearData(){
        todoList.clear()
        Log.d("OtherTodoAdapter","clearData 실행")
        notifyDataSetChanged()
    }



}