package com.spot.android.ui.community.communityContent

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.spot.android.R
import com.spot.android.databinding.ItemCommunityContentBinding
import com.spot.android.ui.community.CategoryPagesDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommunityCategoryContentRVAdapter(
    private var dataList: List<CategoryPagesDetail>,
    private val onItemClick: (CategoryPagesDetail) -> Unit,
    private val onLikeClick: (CategoryPagesDetail) -> Unit,
    private val onUnLikeClick: (CategoryPagesDetail) -> Unit,
    private val onBookmarkClick: (CategoryPagesDetail) -> Unit,
    private val onUnBookmarkClick: (CategoryPagesDetail) -> Unit,
    private val onPageSelected: (Int) -> Unit,
    private val onNextPrevClicked: (Boolean) -> Unit,
    private val getCurrentPage: () -> Int,
    private val getTotalPages: () -> Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < dataList.size) TYPE_ITEM else TYPE_FOOTER
    }

    override fun getItemCount(): Int {
        return if (dataList.isEmpty()) 0 else dataList.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = ItemCommunityContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_footer_paging, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = dataList[position]
            holder.bind(item)
            holder.itemView.setOnClickListener { onItemClick(item) }
            holder.binding.contentLikeNumCheckedIv.setOnClickListener { onUnLikeClick(item) }
            holder.binding.contentLikeNumUncheckedIv.setOnClickListener { onLikeClick(item) }
            holder.binding.contentBookmarkCheckedIv.setOnClickListener { onBookmarkClick(item) }
            holder.binding.contentBookmarkUncheckedIv.setOnClickListener { onUnBookmarkClick(item) }
        } else if (holder is FooterViewHolder) {
            holder.bind()

            val dp80 = (60 * holder.itemView.context.resources.displayMetrics.density).toInt()
            holder.itemView.setPadding(
                holder.itemView.paddingLeft,
                holder.itemView.paddingTop,
                holder.itemView.paddingRight,
                dp80
            )
        }
    }

    inner class ItemViewHolder(val binding: ItemCommunityContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryPagesDetail) {
            binding.contentTitleTv.text = data.title
            binding.contentSummaryTv.text = data.content
            binding.contentSaveNumTv.text = formatNumber(data.scrapCount)
            binding.contentLikeNumTv.text = formatNumber(data.likeCount)
            binding.contentCommentNumTv.text = formatNumber(data.commentCount)
            binding.contentViewNumTv.text = formatNumber(data.viewCount)
            binding.contentWriterTv.text = data.writer
            binding.contentDateTv.text = formatWrittenTime(data.writtenTime)

            binding.contentLikeNumCheckedIv.visibility = if (data.likedByCurrentUser) View.VISIBLE else View.GONE
            binding.contentLikeNumUncheckedIv.visibility = if (data.likedByCurrentUser) View.GONE else View.VISIBLE

            binding.contentBookmarkCheckedIv.visibility = if (data.scrapedByCurrentUser) View.VISIBLE else View.GONE
            binding.contentBookmarkUncheckedIv.visibility = if (data.scrapedByCurrentUser) View.GONE else View.VISIBLE
        }

        private fun formatNumber(value: Int): String {
            return if (value > 999) String.format("%.1fK", value / 1000.0) else value.toString()
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
                writtenTime
            }
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val context = itemView.context
            val pageButtons = listOf(
                itemView.findViewById<TextView>(R.id.page_1),
                itemView.findViewById<TextView>(R.id.page_2),
                itemView.findViewById<TextView>(R.id.page_3),
                itemView.findViewById<TextView>(R.id.page_4),
                itemView.findViewById<TextView>(R.id.page_5)
            )
            val prev = itemView.findViewById<ImageView>(R.id.previous_page)
            val next = itemView.findViewById<ImageView>(R.id.next_page)

            val currentPage = getCurrentPage()
            val totalPages = getTotalPages()

            val startPage = when {
                totalPages <= 5 -> 0
                currentPage >= totalPages - 3 -> totalPages - 5
                currentPage >= 2 -> currentPage - 2
                else -> 0
            }

            pageButtons.forEachIndexed { index, button ->
                val pageNum = startPage + index
                if (pageNum < totalPages) {
                    button.visibility = View.VISIBLE
                    button.text = (pageNum + 1).toString()
                    button.setTextColor(
                        if (pageNum == currentPage)
                            context.getColor(R.color.b500)
                        else
                            context.getColor(R.color.g400)
                    )
                    button.setOnClickListener {
                        if (pageNum != currentPage) onPageSelected(pageNum)
                    }
                } else {
                    button.visibility = View.GONE
                }
            }

            val gray = ContextCompat.getColor(context, R.color.g300)
            val blue = ContextCompat.getColor(context, R.color.b500)

            if (totalPages <= 1) {
                prev.isEnabled = false
                next.isEnabled = false
                prev.setColorFilter(gray, android.graphics.PorterDuff.Mode.SRC_IN)
                next.setColorFilter(gray, android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                prev.isEnabled = true
                next.isEnabled = true
                prev.setColorFilter(blue, android.graphics.PorterDuff.Mode.SRC_IN)
                next.setColorFilter(blue, android.graphics.PorterDuff.Mode.SRC_IN)

                prev.setOnClickListener {
                    if (currentPage == 0) {
                        onPageSelected(totalPages - 1)
                    } else {
                        onPageSelected(currentPage - 1)
                    }
                }

                next.setOnClickListener {
                    if (currentPage == totalPages - 1) {
                        onPageSelected(0)
                    } else {
                        onPageSelected(currentPage + 1)
                    }
                }
            }
        }
    }

    fun updateList(newList: List<CategoryPagesDetail>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
