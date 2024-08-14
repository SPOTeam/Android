package com.example.spoteam_android.ui.community.communityContent

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemCommunityContentBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityCategoryContentRVAdapter(private val dataList: List<CategoryPagesDetail>) : RecyclerView.Adapter<CommunityCategoryContentRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: CategoryPagesDetail)
        fun onLikeClick(data: CategoryPagesDetail)
        fun onUnLikeClick(data: CategoryPagesDetail)
//        fun onSubscribeClick(data: CategoryPagesDetail)
//        fun onUnSubscribeClick(data: CategoryPagesDetail)
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
        if (position < dataList.size) {
            holder.bind(dataList[position])
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(dataList[position])
        }

        holder.binding.contentLikeNumCheckedIv.setOnClickListener{
            holder.binding.contentLikeNumCheckedIv.visibility = View.GONE
            holder.binding.contentLikeNumUncheckedIv.visibility = View.VISIBLE
            itemClickListener.onUnLikeClick(dataList[position])
        }

        holder.binding.contentLikeNumUncheckedIv.setOnClickListener{
            holder.binding.contentLikeNumCheckedIv.visibility = View.VISIBLE
            holder.binding.contentLikeNumUncheckedIv.visibility = View.GONE
            itemClickListener.onLikeClick(dataList[position])
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
        fun bind(data: CategoryPagesDetail){
            binding.contentTitleTv.text = data.title
            binding.contentSaveNumTv.text = data.scrapCount.toString()
            binding.contentSummaryTv.text = data.content
            binding.contentLikeNumTv.text = data.likeCount.toString()
            binding.contentCommentNumTv.text = data.commentCount.toString()
            binding.contentViewNumTv.text = data.viewCount.toString()
            binding.contentWriterTv.text = data.writer
            binding.contentDateTv.text = formatWrittenTime(data.writtenTime)
        }
        private fun formatWrittenTime(writtenTime: String): String {
            val formats = arrayOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            )
            var date: Date? = null
            for (format in formats) {
                try {
                    date = format.parse(writtenTime)
                    break
                } catch (e: ParseException) {
                    Log.d("DateParseException", "${e.message}")
                }
            }
            return if (date != null) {
                SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
            } else {
                writtenTime // 원본 문자열 반환
            }
        }
    }
}