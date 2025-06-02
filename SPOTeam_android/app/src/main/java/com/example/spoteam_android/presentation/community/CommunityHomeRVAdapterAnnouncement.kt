package com.example.spoteam_android.presentation.community

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemCommunityhomeContentWithIndexBinding
import com.kakao.sdk.template.model.Content

class CommunityHomeRVAdapterAnnouncement(private val dataList: List<com.example.spoteam_android.presentation.community.AnnouncementDetailInfo>) : RecyclerView.Adapter<com.example.spoteam_android.presentation.community.CommunityHomeRVAdapterAnnouncement.ViewHolder>() {

    interface ItemClick {
        fun onItemClick(view: View, position: Int, parentCommentId : Int)
    }

    var itemClick: _root_ide_package_.com.example.spoteam_android.presentation.community.CommunityHomeRVAdapterAnnouncement.ItemClick? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): _root_ide_package_.com.example.spoteam_android.presentation.community.CommunityHomeRVAdapterAnnouncement.ViewHolder {
        val binding: ItemCommunityhomeContentWithIndexBinding = ItemCommunityhomeContentWithIndexBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: _root_ide_package_.com.example.spoteam_android.presentation.community.CommunityHomeRVAdapterAnnouncement.ViewHolder, position: Int) {
        if (position < dataList.size) {
            holder.bind(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(val binding : ItemCommunityhomeContentWithIndexBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: _root_ide_package_.com.example.spoteam_android.presentation.community.AnnouncementDetailInfo){
            binding.contentIndexTv.text = data.rank.toString()
            binding.contentTitleTv.text = data.postTitle

            if (data.commentCount > 999) {
                binding.contentCommentNumTv.text = "${data.commentCount}+"
//                val formatted = String.format("%.1fK", data.commentCount / 1000.0)
//                binding.contentCommentNumTv.text = formatted
            } else {
                binding.contentCommentNumTv.text = data.commentCount.toString()
            }

            binding.root.setOnClickListener{
                val context = binding.root.context
                val intent = Intent(context, _root_ide_package_.com.example.spoteam_android.presentation.community.CommunityContentActivity::class.java)
                intent.putExtra("postInfo", data.postId) // postId 등 필요한 데이터 전달
                context.startActivity(intent)
            }
        }
    }
}