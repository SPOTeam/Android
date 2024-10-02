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
            binding.contentCategoryTv.text = checkType(data.postType)
            binding.contentTitleTv.text = data.postTitle
            binding.contentCommentNumTv.text = data.commentCount.toString()
        }

        private fun checkType(postType: String): String {
            when(postType){
                "COUNSELING" -> {
                    return "고민상담"
                }
                "FREE_TALK" -> {
                    return "자유토크"
                }
                "INFORMATION_SHARING" -> {
                    return "정보공유"
                }
                "JOB_TALK" -> {
                    return "취준토크"
                }
                "PASS_EXPERIENCE" -> {
                    return "합격후기"
                }
                else -> return ""
            }
        }
    }
}