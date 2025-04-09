package com.example.spoteam_android.ui.community

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemCommunityhomeContentWithIndexBinding
import com.kakao.sdk.template.model.Content

class CommunityHomeRVAdapterAnnouncement(private val dataList: List<AnnouncementDetailInfo>) : RecyclerView.Adapter<CommunityHomeRVAdapterAnnouncement.ViewHolder>() {

    interface ItemClick {
        fun onItemClick(view: View, position: Int, parentCommentId : Int)
    }

    var itemClick: ItemClick? = null

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
        fun bind(data: AnnouncementDetailInfo){
            binding.contentIndexTv.text = data.rank.toString()
            binding.contentTitleTv.text = data.postTitle

            if (data.commentCount > 999) {
                val formatted = String.format("%.1fK", data.commentCount / 1000.0)
                binding.contentCommentNumTv.text = formatted
            } else {
                binding.contentCommentNumTv.text = data.commentCount.toString()
            }

            binding.root.setOnClickListener{
                val context = binding.root.context
                val intent = Intent(context, CommunityContentActivity::class.java)
                intent.putExtra("postInfo", data.postId) // postId 등 필요한 데이터 전달
                context.startActivity(intent)
            }
        }
    }
}