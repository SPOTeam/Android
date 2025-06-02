package com.example.spoteam_android.presentation.login.checklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemLocationSearchBinding

class LocationSearchAdapter(
    private val dataList: List<LocationItem>,
    private val onItemClick: (LocationItem) -> Unit
) : RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>() {

    private var filteredList: MutableList<LocationItem> = dataList.toMutableList()
    private var selectedItem: LocationItem? = null

    inner class ViewHolder(val binding: ItemLocationSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationItem) {
            binding.itemLocationConcreteTv.text = item.address
            binding.root.setOnClickListener {
                selectedItem = item
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
            val normalizedQueryWords = normalizeLocationName(query.lowercase()).split(" ")

            for (item in dataList) {
                val normalizedAddressWords = normalizeLocationName(item.address.lowercase()).split(" ")


                if (normalizedQueryWords.all { queryWord ->
                        normalizedAddressWords.any { addressWord -> addressWord.contains(queryWord) }
                    }) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }




    fun getSelectedItem(): LocationItem? {
        return selectedItem
    }

    fun normalizeLocationName(name: String): String {
        return name.replace("특별자치도", "도")
            .replace("특별자치시", "시")
            .replace("광역시", "시")
            .replace("자치시", "시")
            .replace("도", " 도")
            .replace("시", " 시")
            .replace("군", " 군")
            .replace("구", " 구")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

}
