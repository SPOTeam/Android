package com.umcspot.android.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.umcspot.android.R
import com.umcspot.android.databinding.ItemMystudyCommunityContentBinding
import com.umcspot.android.ui.community.PostDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyStudyPostRVAdapter(
    private var dataList: List<PostDetail>,
    private val onItemClick: (PostDetail) -> Unit,
    private val onLikeClick: (PostDetail) -> Unit,
    private val onUnLikeClick: (PostDetail) -> Unit,
    private val onPageSelected: (Int) -> Unit,
    private val onNextPrevClicked: (Boolean) -> Unit,
    private val getCurrentPage: () -> Int,
    private val getTotalPages: () -> Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    override fun getItemCount(): Int {
        return if (dataList.isEmpty()) 0 else dataList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < dataList.size) TYPE_ITEM else TYPE_FOOTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = ItemMystudyCommunityContentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            PostViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_footer_paging, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            val item = dataList[position]
            holder.bind(item)
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    inner class PostViewHolder(val binding: ItemMystudyCommunityContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PostDetail) {
            binding.contentTitleTv.text = data.title
            binding.contentSummaryTv.text = data.content
            binding.contentLikeNumTv.text = data.likeNum.toString()
            binding.contentCommentNumTv.text = data.commentNum.toString()
            binding.contentViewNumTv.text = data.hitNum.toString()
            binding.contentDateTv.text = formatWrittenTime(data.createdAt)
            binding.categoryTv.text = if (data.isAnnouncement) "공지" else categoryFormat(data.theme)

            if (data.isLiked) {
                binding.contentLikeNumCheckedIv.visibility = View.VISIBLE
                binding.contentLikeNumUncheckedIv.visibility = View.GONE
            } else {
                binding.contentLikeNumCheckedIv.visibility = View.GONE
                binding.contentLikeNumUncheckedIv.visibility = View.VISIBLE
            }

            itemView.setOnClickListener { onItemClick(data) }
            binding.contentLikeNumCheckedIv.setOnClickListener { onLikeClick(data) }
            binding.contentLikeNumUncheckedIv.setOnClickListener { onUnLikeClick(data) }
        }

        private fun categoryFormat(theme: String): String = when (theme) {
            "WELCOME" -> "가입인사"
            "INFO_SHARING" -> "정보공유"
            "FREE_TALK" -> "자유"
            "STUDY_REVIEW" -> "스터디후기"
            "QNA" -> "Q&A"
            else -> ""
        }

        private fun formatWrittenTime(writtenTime: String): String {
            val formats = arrayOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            )
            for (format in formats) {
                try {
                    return format.parse(writtenTime)?.let {
                        SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(it)
                    } ?: writtenTime
                } catch (_: ParseException) {}
            }
            return writtenTime
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
                    val prevPage = if (currentPage == 0) totalPages - 1 else currentPage - 1
                    onPageSelected(prevPage)
                }

                next.setOnClickListener {
                    val nextPage = if (currentPage == totalPages - 1) 0 else currentPage + 1
                    onPageSelected(nextPage)
                }
            }
        }
    }

    fun updateList(newList: List<PostDetail>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
