package com.example.spoteam_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding

class BoardAdapter(private val itemList: ArrayList<BoardItem>) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class BoardViewHolder(private val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BoardItem) {
            binding.tvTime.text = item.studyname
            binding.tvTitle.text = item.studyobject
            binding.tvName.text = item.studyto.toString()
            binding.tvName2.text = item.studypo.toString()
            binding.tvName3.text = item.like.toString()
            binding.tvName4.text = item.watch.toString()
        }
    }

    fun filterList(filteredList: ArrayList<BoardItem>) {
        itemList.clear()
        itemList.addAll(filteredList)
        notifyDataSetChanged()
    }
}

