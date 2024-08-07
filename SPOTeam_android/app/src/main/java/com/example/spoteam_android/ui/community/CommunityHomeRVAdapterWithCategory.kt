package com.example.spoteam_android.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemCommunityhomeContentWithCategoryBinding

class CommunityHomeRVAdapterWithCategory(private val dataList: List<RepresentativeDetailInfo>) : RecyclerView.Adapter<CommunityHomeRVAdapterWithCategory.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCommunityhomeContentWithCategoryBinding = ItemCommunityhomeContentWithCategoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

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

    inner class ViewHolder(val binding : ItemCommunityhomeContentWithCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RepresentativeDetailInfo){
            binding.contentCategoryTv.text = data.postType
            binding.contentTitleTv.text = data.postTitle
            binding.contentCommentNumTv.text = data.commentCount.toString()
        }
    }
}