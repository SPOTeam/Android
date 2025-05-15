package com.example.spoteam_android.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding

class StudyAdapter(
    initialItems: List<StudyItem>,
    private val onItemClick: (StudyItem) -> Unit,
    private val onLikeClick: (StudyItem, ImageView) -> Unit,
    private val onPageSelected: (Int) -> Unit,
    private val onNextPrevClicked: (Boolean) -> Unit,
    private val getCurrentPage: () -> Int,
    private val getTotalPages: () -> Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }

    // 내부 리스트
    private var itemList = ArrayList<StudyItem>().apply {
        addAll(initialItems)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemList.size) TYPE_ITEM else TYPE_FOOTER
    }

    override fun getItemCount(): Int = if (itemList.isEmpty()) 0 else itemList.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            StudyViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_footer_paging, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StudyViewHolder) {
            val item = itemList[position]
            holder.bind(item)
            holder.itemView.setOnClickListener { onItemClick(item) }
            holder.likeButton.setOnClickListener { onLikeClick(item, holder.likeButton) }
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    inner class StudyViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val likeButton: ImageView = binding.heartCountIv

        fun bind(item: StudyItem) {
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

    fun updateList(newList: List<StudyItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}
