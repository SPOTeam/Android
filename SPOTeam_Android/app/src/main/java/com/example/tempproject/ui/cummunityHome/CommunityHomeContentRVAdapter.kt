package com.example.tempproject.ui.cummunityHome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tempproject.Data
import com.example.tempproject.databinding.ItemCommunityhomeContentwithindexBinding

class CommunityHomeContentRVAdapter(private val dataList: ArrayList<Data>) : RecyclerView.Adapter<CommunityHomeContentRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommunityHomeContentRVAdapter.ViewHolder {
        val binding: ItemCommunityhomeContentwithindexBinding = ItemCommunityhomeContentwithindexBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityHomeContentRVAdapter.ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(val binding : ItemCommunityhomeContentwithindexBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data){
            binding.contentIndexTv.text = data.index
            binding.contentTitleTv.text = data.content
            binding.contentCommentNumTv.text = data.commentNum
        }
    }
}