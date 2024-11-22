package com.example.spoteam_android.todolist

import com.example.spoteam_android.SceduleItem
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.DetailStudyHomeAdapter
import com.example.spoteam_android.databinding.ItemDetailFragmentSceduleRvBinding

class TodoMemberProfileAdapter(private val itemList: MutableList<SceduleItem>) :
    RecyclerView.Adapter<TodoMemberProfileAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemDetailFragmentSceduleRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = itemList.size

    class ScheduleViewHolder(private val binding: ItemDetailFragmentSceduleRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SceduleItem) {
            binding.itemDetailFragmentScheduleDdayTv.text = item.dday ?: ""
            binding.itemDetailFragmentScheduleDayTv.text = item.day ?: ""
            binding.itemDetailFragmentScheduleContentTv.text = item.scheduleContent ?: ""
            binding.itemDetailFragmentScheduleConcreteTimeTv.text = item.concreteTime ?: ""
            binding.itemDetailFragmentScheduleConcretePlaceTv.text = item.place ?: ""
        }
    }

    fun updateList(newList: List<SceduleItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}
