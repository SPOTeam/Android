package com.spot.android.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spot.android.R
import com.spot.android.databinding.ItemConsiderAttendanceBinding
import com.spot.android.ui.community.MyRecruitingStudyDetail

class ConsiderAttendanceContentRVAdapter(
    private var dataList: List<MyRecruitingStudyDetail>,
    private val onItemClick: (MyRecruitingStudyDetail) -> Unit,
    private val onPageClick: (Int) -> Unit,
    private val currentPageProvider: () -> Int,
    private val totalPagesProvider: () -> Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1
    }

    override fun getItemCount(): Int {
        return if (dataList.isEmpty()) 0 else dataList.size + 1  // 데이터 없으면 footer 제거
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == dataList.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemConsiderAttendanceBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            ItemViewHolder(binding)
        } else {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_footer_paging, viewGroup, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder && position < dataList.size) {
            val item = dataList[position]
            holder.bind(item)
        } else if (holder is FooterViewHolder) {
            holder.bind()
        }
    }

    inner class ItemViewHolder(val binding: ItemConsiderAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MyRecruitingStudyDetail) {
            binding.tvTime.text = data.title
            binding.tvTitle.text = data.goal
            binding.tvName.text = data.maxPeople.toString()
            binding.tvName2.text = data.memberCount.toString()
            binding.tvName3.text = data.heartCount.toString()
            binding.tvName4.text = data.hitNum.toString()

            if (data.liked) {
                binding.icHeartRed.visibility = View.VISIBLE
                binding.ImageView6.visibility = View.GONE
            } else {
                binding.icHeartRed.visibility = View.GONE
                binding.ImageView6.visibility = View.VISIBLE
            }

            Glide.with(binding.root.context)
                .load(data.imageUrl)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.ImageView4)

            binding.considerAttendanceBt.setOnClickListener {
                onItemClick(data)
            }
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
            val context = itemView.context


            val prev = itemView.findViewById<ImageView>(R.id.previous_page)
            val next = itemView.findViewById<ImageView>(R.id.next_page)

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
                        onPageClick(totalPages - 1)  // 첫 페이지에서 ← 클릭 → 마지막 페이지로
                    } else {
                        onPageClick(currentPage - 1)
                    }
                }

                next.setOnClickListener {
                    if (currentPage == totalPages - 1) {
                        onPageClick(0)  // 마지막 페이지에서 → 클릭 → 첫 페이지로
                    } else {
                        onPageClick(currentPage + 1)
                    }
                }
            }
        }
    }

    fun updateList(newList: List<MyRecruitingStudyDetail>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
