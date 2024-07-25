package com.example.spoteam_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemLocationSearchBinding
import com.example.spoteam_android.login.LocationItem

class LocationSearchAdapter(
    private val dataList: List<LocationItem>,
    private val onItemClick: (LocationItem) -> Unit
) : RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>() {

    private var filteredList: MutableList<LocationItem> = dataList.toMutableList()

    inner class ViewHolder(val binding: ItemLocationSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationItem) {
            binding.itemLocationSearchTv.text = item.name
            binding.itemLocationConcreteTv.text = item.address

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLocationSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(dataList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (item in dataList) {
                if (item.address.lowercase().contains(lowerCaseQuery)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}
