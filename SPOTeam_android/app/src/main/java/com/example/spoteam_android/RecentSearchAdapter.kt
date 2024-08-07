package com.example.spoteam_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.RecentSearchItemBinding

class RecentSearchAdapter(
    private var recentSearchList: MutableList<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    class RecentSearchViewHolder(val binding: RecentSearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = RecentSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        val search = recentSearchList[position]
        holder.binding.searchText.text = search
        holder.itemView.setOnClickListener {
            onClick(search)
        }
    }

    override fun getItemCount() = recentSearchList.size
}
