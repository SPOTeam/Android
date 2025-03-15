package com.example.spoteam_android.ui.community.communityContent

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.BookmarkItem
import com.example.spoteam_android.databinding.ItemCommunityContentBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScrapContentRVAdapter(private var dataList: ArrayList<CategoryPagesDetail>) : RecyclerView.Adapter<ScrapContentRVAdapter.ViewHolder>() {

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
            binding.contentSaveNumTv.text = data.scrapCount.toString()
            binding.contentSummaryTv.text = data.content
            binding.contentLikeNumTv.text = data.likeCount.toString()
            binding.contentCommentNumTv.text = data.commentCount.toString()
            binding.contentViewNumTv.text = data.viewCount.toString()
            binding.contentWriterTv.text = data.writer
            binding.contentDateTv.text = formatWrittenTime(data.writtenTime)
            binding.contentSaveNumTv.text = data.scrapCount.toString()

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

    fun updateList(newList: List<CategoryPagesDetail>) {
        //Log.d("BookMarkRVAdapter", "updateList called with ${newList.size} items")

        dataList = ArrayList() // 새 ArrayList로 초기화
        dataList.addAll(newList)
        //Log.d("BookMarkRVAdapter", "itemList size after addAll: ${bookmarkitemList.size}")

        notifyDataSetChanged()
    }
}
