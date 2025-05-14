package com.example.spoteam_android.ui.category

import StudyViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding

class CategoryVPAdapter(
    var dataList: ArrayList<BoardItem>,
    private val onLikeClick: (BoardItem, ImageView) -> Unit,
    private val studyViewModel: StudyViewModel,
    private val onPageSelected: (Int) -> Unit,  // 페이지 번호 직접 선택
    private val onNextPrevClicked: (Boolean) -> Unit, // true = 다음, false = 이전
    private val getCurrentPage: () -> Int,      // 현재 페이지 외부에서 조회
    private val getTotalPages: () -> Int        // 총 페이지 외부에서 조회
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListeners {
        fun onItemClick(data: BoardItem)
    }

    private lateinit var itemClickListener: OnItemClickListeners

    fun setItemClickListener(onItemClickListener: OnItemClickListeners) {
        this.itemClickListener = onItemClickListener
    }

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < dataList.size) TYPE_ITEM else TYPE_FOOTER
    }

    override fun getItemCount() = dataList.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            BoardViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_footer_paging, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BoardViewHolder) {
            val item = dataList[position]
            holder.bind(item)

            holder.itemView.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    itemClickListener.onItemClick(item)
                }
                studyViewModel.setRecentStudyId(item.studyId)
            }

            holder.likeButton.setOnClickListener {
                onLikeClick(item, holder.likeButton)
            }

        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    inner class BoardViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val likeButton: ImageView = binding.heartCountIv

        fun bind(item: BoardItem) {
            binding.tvTime.text = item.title
            binding.tvTitle.text = item.goal
            binding.tvName.text = item.maxPeople.toString()
            binding.tvName2.text = item.memberCount.toString()
            binding.tvName3.text = item.heartCount.toString()
            binding.tvName4.text = item.hitNum.toString()

            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.ImageView4)

            val heartIcon = if (item.liked) R.drawable.ic_heart_filled else R.drawable.study_like
            binding.heartCountIv.setImageResource(heartIcon)
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val pageButtons = listOf(
                itemView.findViewById<TextView>(R.id.page_1),
                itemView.findViewById<TextView>(R.id.page_2),
                itemView.findViewById<TextView>(R.id.page_3),
                itemView.findViewById<TextView>(R.id.page_4),
                itemView.findViewById<TextView>(R.id.page_5)
            )
            val context = itemView.context

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
                            itemView.context.getColor(R.color.b500)
                        else
                            itemView.context.getColor(R.color.g400)
                    )
                    button.setOnClickListener {
                        if (pageNum != currentPage) onPageSelected(pageNum)
                    }
                } else {
                    button.visibility = View.GONE
                }
            }

            val grayColor = ContextCompat.getColor(context, R.color.g300)
            val blueColor = ContextCompat.getColor(context, R.color.b500)

            // 좌우 버튼 처리
            if (totalPages <= 1) {
                prev.isEnabled = false
                next.isEnabled = false
                prev.setColorFilter(grayColor, android.graphics.PorterDuff.Mode.SRC_IN)
                next.setColorFilter(grayColor, android.graphics.PorterDuff.Mode.SRC_IN)
            } else {
                prev.isEnabled = true
                next.isEnabled = true
                prev.setColorFilter(blueColor, android.graphics.PorterDuff.Mode.SRC_IN)
                next.setColorFilter(blueColor, android.graphics.PorterDuff.Mode.SRC_IN)

                prev.setOnClickListener {
                    if (currentPage == 0) {
                        onPageSelected(totalPages - 1)  // 첫 페이지에서 ← 클릭 → 마지막 페이지로
                    } else {
                        onPageSelected(currentPage - 1)
                    }
                }

                next.setOnClickListener {
                    if (currentPage == totalPages - 1) {
                        onPageSelected(0)  // 마지막 페이지에서 → 클릭 → 첫 페이지로
                    } else {
                        onPageSelected(currentPage + 1)
                    }
                }
            }
        }
    }

    fun updateList(newList: List<BoardItem>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }
}
