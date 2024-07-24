//package com.example.spoteam_android
//
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//
//class RecentSearchAdapter(private val searchList: List<String>, private val clickListener: (String) -> Unit) :
//    RecyclerView.Adapter<RecentSearchAdapter.SearchViewHolder>() {
//
//    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val searchText: TextView = itemView.findViewById(R.id.searchText)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.recent_search_item, parent, false)
//        return SearchViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
//        val currentSearch = searchList[position]
//        holder.searchText.text = currentSearch
//        holder.itemView.setOnClickListener {
//            clickListener(currentSearch)
//        }
//    }
//
//    override fun getItemCount() = searchList.size
//}


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
