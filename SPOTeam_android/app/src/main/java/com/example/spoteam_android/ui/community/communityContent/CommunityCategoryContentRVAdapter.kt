package com.example.spoteam_android.ui.community.communityContent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.CommunityData
import com.example.spoteam_android.databinding.ItemCommunityContentBinding

class CommunityCategoryContentRVAdapter(private val dataList: ArrayList<CommunityData>) : RecyclerView.Adapter<CommunityCategoryContentRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data : CommunityData)
    }

    private lateinit var itemClickListener : OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCommunityContentBinding = ItemCommunityContentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(dataList[position])
        }

        holder.binding.contentLikeNumCheckedIv.setOnClickListener{
            holder.binding.contentLikeNumCheckedIv.visibility = View.GONE
            holder.binding.contentLikeNumUncheckedIv.visibility = View.VISIBLE
        }
        holder.binding.contentLikeNumUncheckedIv.setOnClickListener{
            holder.binding.contentLikeNumCheckedIv.visibility = View.VISIBLE
            holder.binding.contentLikeNumUncheckedIv.visibility = View.GONE
        }

        holder.binding.contentBookmarkCheckedIv.setOnClickListener{
            holder.binding.contentBookmarkCheckedIv.visibility = View.GONE
            holder.binding.contentBookmarkUncheckedIv.visibility = View.VISIBLE
        }
        holder.binding.contentBookmarkUncheckedIv.setOnClickListener{
            holder.binding.contentBookmarkCheckedIv.visibility = View.VISIBLE
            holder.binding.contentBookmarkUncheckedIv.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding : ItemCommunityContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CommunityData){
            binding.contentTitleTv.text = data.title
            binding.contentSaveNumTv.text = data.bookmarkNum
            binding.contentSummaryTv.text = data.content
            binding.contentLikeNumTv.text = data.likeNum
            binding.contentCommentNumTv.text = data.commentNum
            binding.contentViewNumTv.text = data.viewNum
            binding.contentViewNumTv.text = data.viewNum
            binding.contentWriterTv.text = data.writer
            binding.contentDateTv.text = data.date
        }
    }
}