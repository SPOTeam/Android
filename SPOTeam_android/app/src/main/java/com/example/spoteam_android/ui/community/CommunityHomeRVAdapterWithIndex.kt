package com.example.spoteam_android.ui.community

import android.content.Intent
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
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(val binding : ItemCommunityhomeContentWithIndexBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ContentDetailInfo){
            binding.contentIndexTv.text = "0${data.rank}"
            binding.contentTitleTv.text = data.postTitle
            if (data.commentCount > 999) {
                binding.contentCommentNumTv.text = "${data.commentCount}+"
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