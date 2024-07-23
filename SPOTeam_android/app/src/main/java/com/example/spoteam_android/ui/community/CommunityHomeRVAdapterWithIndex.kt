package com.example.spoteam_android.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.indexData
import com.example.spoteam_android.databinding.ItemCommunityhomeContentWithIndexBinding

class CommunityHomeRVAdapterWithIndex(private val dataList: ArrayList<indexData>) : RecyclerView.Adapter<CommunityHomeRVAdapterWithIndex.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCommunityhomeContentWithIndexBinding = ItemCommunityhomeContentWithIndexBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(val binding : ItemCommunityhomeContentWithIndexBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: indexData){
            binding.contentIndexTv.text = data.index
            binding.contentTitleTv.text = data.content
            binding.contentCommentNumTv.text = data.commentNum
        }
    }
}