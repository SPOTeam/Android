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

class CommunityCategoryContentRVAdapter(private var dataList: List<CategoryPagesDetail>) : RecyclerView.Adapter<CommunityCategoryContentRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: CategoryPagesDetail)
        fun onLikeClick(data: CategoryPagesDetail)
        fun onUnLikeClick(data: CategoryPagesDetail)
        fun onBookMarkClick(data: CategoryPagesDetail)
        fun onUnBookMarkClick(data: CategoryPagesDetail)
    }

    private lateinit var itemClickListener: OnItemClickListener

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

        holder.binding.contentLikeNumCheckedIv.setOnClickListener {
            itemClickListener.onUnLikeClick(dataList[position])
        }

        holder.binding.contentLikeNumUncheckedIv.setOnClickListener {
            itemClickListener.onLikeClick(dataList[position])
        }

        holder.binding.contentBookmarkCheckedIv.setOnClickListener {
            itemClickListener.onBookMarkClick(dataList[position])
        }
        holder.binding.contentBookmarkUncheckedIv.setOnClickListener {
            itemClickListener.onUnBookMarkClick(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding: ItemCommunityContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryPagesDetail) {
            binding.contentTitleTv.text = data.title

            if (data.scrapCount > 999) {
                val formatted = String.format("%.1fK", data.scrapCount / 1000.0)
                binding.contentSaveNumTv.text = formatted
            } else {
                binding.contentSaveNumTv.text = data.scrapCount.toString()
            }

            binding.contentSummaryTv.text = data.content

            if (data.likeCount > 999) {
                val formatted = String.format("%.1fK", data.likeCount / 1000.0)
                binding.contentLikeNumTv.text = formatted
            } else {
                binding.contentLikeNumTv.text = data.likeCount.toString()
            }

            if (data.commentCount > 999) {
                val formatted = String.format("%.1fK", data.commentCount / 1000.0)
                binding.contentCommentNumTv.text = formatted
            } else {
                binding.contentCommentNumTv.text = data.commentCount.toString()
            }

            if (data.viewCount > 999) {
                val formatted = String.format("%.1fK", data.viewCount / 1000.0)
                binding.contentViewNumTv.text = formatted
            } else {
                binding.contentViewNumTv.text = data.viewCount.toString()
            }

            binding.contentWriterTv.text = data.writer
            binding.contentDateTv.text = formatWrittenTime(data.writtenTime)

            if (data.likedByCurrentUser) {
                binding.contentLikeNumCheckedIv.visibility = View.VISIBLE
                binding.contentLikeNumUncheckedIv.visibility = View.GONE
            } else {
                binding.contentLikeNumCheckedIv.visibility = View.GONE
                binding.contentLikeNumUncheckedIv.visibility = View.VISIBLE
            }

            if(data.scrapedByCurrentUser) {
                binding.contentBookmarkCheckedIv.visibility = View.VISIBLE
                binding.contentBookmarkUncheckedIv.visibility = View.GONE
            } else {
                binding.contentBookmarkCheckedIv.visibility = View.GONE
                binding.contentBookmarkUncheckedIv.visibility = View.VISIBLE
            }
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
