package com.example.spoteam_android.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemCommunityhomeContentWithIndexBinding

class CommunityHomeRVAdapterWithIndex(private val dataList: List<ContentDetailInfo>) : RecyclerView.Adapter<CommunityHomeRVAdapterWithIndex.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCommunityhomeContentWithIndexBinding = ItemCommunityhomeContentWithIndexBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dataList.size) {
            holder.bind(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(val binding : ItemCommunityhomeContentWithIndexBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ContentDetailInfo){
            binding.contentIndexTv.text = data.rank.toString()
            binding.contentTitleTv.text = data.postTitle
            binding.contentCommentNumTv.text = data.commentCount.toString()
        }
    }
}