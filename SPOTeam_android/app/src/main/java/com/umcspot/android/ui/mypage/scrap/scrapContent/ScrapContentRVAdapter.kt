package com.umcspot.android.ui.community.communityContent

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.umcspot.android.R
import com.umcspot.android.databinding.ItemCommunityContentBinding
import com.umcspot.android.ui.community.CategoryPagesDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class ScrapContentRVAdapter(
    private var dataList: ArrayList<CategoryPagesDetail>,
    private val onPageClick: (Int) -> Unit,
    private val currentPageProvider: () -> Int,
    private val totalPagesProvider: () -> Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: CategoryPagesDetail)
        fun onLikeClick(data: CategoryPagesDetail)
        fun onUnLikeClick(data: CategoryPagesDetail)
        fun onBookMarkClick(data: CategoryPagesDetail)
        fun onUnBookMarkClick(data: CategoryPagesDetail)
    }

    private lateinit var itemClickListener: OnItemClickListener

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int = dataList.size + 1 // Footer 포함

    override fun getItemViewType(position: Int): Int {
        return if (position == dataList.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemCommunityContentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            ViewHolder(binding)
        } else {
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_footer_paging, viewGroup, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder && position < dataList.size) {
            val item = dataList[position]
            holder.bind(item)

            holder.itemView.setOnClickListener { itemClickListener.onItemClick(item) }
            holder.binding.contentLikeNumCheckedIv.setOnClickListener { itemClickListener.onUnLikeClick(item) }
            holder.binding.contentLikeNumUncheckedIv.setOnClickListener { itemClickListener.onLikeClick(item) }
            holder.binding.contentBookmarkCheckedIv.setOnClickListener { itemClickListener.onBookMarkClick(item) }
            holder.binding.contentBookmarkUncheckedIv.setOnClickListener { itemClickListener.onUnBookMarkClick(item) }

        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    inner class ViewHolder(val binding: ItemCommunityContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryPagesDetail) {
            binding.contentTitleTv.text = data.title
            binding.contentSummaryTv.text = data.content
            binding.contentLikeNumTv.text = data.likeCount.toString()
            binding.contentCommentNumTv.text = data.commentCount.toString()
            binding.contentViewNumTv.text = data.viewCount.toString()
            binding.contentWriterTv.text = data.writer
            binding.contentDateTv.text = formatWrittenTime(data.writtenTime)
            binding.contentSaveNumTv.text = data.scrapCount.toString()

            binding.contentLikeNumCheckedIv.visibility = if (data.likedByCurrentUser) View.VISIBLE else View.GONE
            binding.contentLikeNumUncheckedIv.visibility = if (!data.likedByCurrentUser) View.VISIBLE else View.GONE
            binding.contentBookmarkCheckedIv.visibility = if (data.scrapedByCurrentUser) View.VISIBLE else View.GONE
            binding.contentBookmarkUncheckedIv.visibility = if (!data.scrapedByCurrentUser) View.VISIBLE else View.GONE
        }

        private fun formatWrittenTime(writtenTime: String): String {
            val formats = arrayOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            )
            for (format in formats) {
                try {
                    val date = format.parse(writtenTime)
                    return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date!!)
                } catch (e: ParseException) {
                    Log.d("DateParseException", "${e.message}")
                }
            }
            return writtenTime
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val pageButtons = listOf<TextView>(
                itemView.findViewById(R.id.page_1),
                itemView.findViewById(R.id.page_2),
                itemView.findViewById(R.id.page_3),
                itemView.findViewById(R.id.page_4),
                itemView.findViewById(R.id.page_5),
            )

            val previousPage = itemView.findViewById<ImageView>(R.id.previous_page)
            val nextPage = itemView.findViewById<ImageView>(R.id.next_page)

            val currentPage = currentPageProvider()
            val totalPages = totalPagesProvider()
            val startPage = when {
                totalPages <= 5 -> 0
                currentPage >= totalPages - 3 -> totalPages - 5
                currentPage >= 2 -> currentPage - 2
                else -> 0
            }

            pageButtons.forEachIndexed { index, button ->
                val pageNum = startPage + index
                if (pageNum < totalPages) {
                    button.text = (pageNum + 1).toString()
                    button.visibility = View.VISIBLE
                    button.setTextColor(
                        if (pageNum == currentPage)
                            itemView.context.getColor(R.color.b500)
                        else
                            itemView.context.getColor(R.color.g400)
                    )
                    button.setOnClickListener { onPageClick(pageNum) }
                } else {
                    button.visibility = View.GONE
                }
            }

            if (totalPages <= 1) {
                previousPage.isEnabled = false
                nextPage.isEnabled = false
                previousPage.setColorFilter(itemView.context.getColor(R.color.g300))
                nextPage.setColorFilter(itemView.context.getColor(R.color.g300))
            } else {
                previousPage.isEnabled = true
                nextPage.isEnabled = true
                previousPage.setColorFilter(itemView.context.getColor(R.color.b500))
                nextPage.setColorFilter(itemView.context.getColor(R.color.b500))
            }

            previousPage.setOnClickListener {
                if (totalPages > 1) {
                    onPageClick(if (currentPage > 0) currentPage - 1 else totalPages - 1)
                }
            }

            nextPage.setOnClickListener {
                if (totalPages > 1) {
                    onPageClick(if (currentPage < totalPages - 1) currentPage + 1 else 0)
                }
            }
        }
    }

    fun updateList(newList: List<CategoryPagesDetail>) {
        dataList = ArrayList(newList)
        notifyDataSetChanged()
    }
}
