package com.example.spoteam_android.ui.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.CategoryData
import com.example.spoteam_android.databinding.ItemCommunityhomeContentWithCategoryBinding

class CommunityHomeRVAdapterWithCategory(private val dataList: ArrayList<CategoryData>) : RecyclerView.Adapter<CommunityHomeRVAdapterWithCategory.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCommunityhomeContentWithCategoryBinding = ItemCommunityhomeContentWithCategoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(val binding : ItemCommunityhomeContentWithCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryData){
            binding.contentCategoryTv.text = data.category
            binding.contentTitleTv.text = data.content
            binding.contentCommentNumTv.text = data.commentNum
        }
    }
}