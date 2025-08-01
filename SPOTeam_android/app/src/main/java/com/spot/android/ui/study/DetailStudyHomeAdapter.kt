package com.spot.android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spot.android.databinding.ItemDetailFragmentSceduleRvBinding

        class DetailStudyHomeAdapter(private val itemList: ArrayList<SceduleItem>) :
            RecyclerView.Adapter<DetailStudyHomeAdapter.SceduleViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SceduleViewHolder {
                val binding = ItemDetailFragmentSceduleRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SceduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SceduleViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class SceduleViewHolder(private val binding: ItemDetailFragmentSceduleRvBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SceduleItem) {
            binding.itemDetailFragmentScheduleDdayTv.text = item.dday
            binding.itemDetailFragmentScheduleDayTv.text = item.day
            binding.itemDetailFragmentScheduleContentTv.text = item.scheduleContent
            binding.itemDetailFragmentScheduleConcreteTimeTv.text = item.concreteTime
            binding.itemDetailFragmentScheduleConcretePlaceTv.text = item.place
        }
    }

    fun updateList(newList: ArrayList<SceduleItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}
