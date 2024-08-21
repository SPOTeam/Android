package com.example.spoteam_android

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment
import com.bumptech.glide.Glide

class BoardAdapter(
    private val itemList: ArrayList<BoardItem>,
    private val onItemClick: (BoardItem) -> Unit,
    private val onLikeClick: (BoardItem, ImageView) -> Unit // onLikeClick 추가
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemRecyclerViewPlusToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }

        holder.likeButton.setOnClickListener {
            onLikeClick(currentItem, holder.likeButton) // onLikeClick 호출
        }
    }

    override fun getItemCount(): Int = itemList.size
    fun getItemList(): List<BoardItem> {
        return itemList
    }

    inner class BoardViewHolder(val binding: ItemRecyclerViewPlusToggleBinding) : RecyclerView.ViewHolder(binding.root) {
        val likeButton: ImageView = binding.heartCountIv

        fun bind(item: BoardItem) {
            // 데이터 바인딩
            binding.tvTime.text = item.title
            binding.tvTitle.text = item.goal
            binding.tvName.text = item.maxPeople.toString()
            binding.tvName2.text = item.memberCount.toString()
            binding.tvName3.text = item.heartCount.toString()
            binding.tvName4.text = item.hitNum.toString()

            // 이미지 로드
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.ImageView4)

            // 하트 아이콘 상태 설정
            val heartIcon = if (item.liked) R.drawable.ic_heart_filled else R.drawable.study_like
            binding.heartCountIv.setImageResource(heartIcon)

            binding.toggle.setOnClickListener {
                showPopupMenu(it)
            }
        }

        private fun showPopupMenu(view: View) {
            val popupMenu = PopupMenu(view.context, view)
            val inflater: MenuInflater = popupMenu.menuInflater
            val exit = ExitStudyPopupFragment(view.context, this@BoardAdapter, adapterPosition)
            val report = ReportStudymemberFragment(view.context)
            inflater.inflate(R.menu.menu_item_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.exit_study -> {
                        exit.start()
                        true
                    }
                    R.id.report_study -> {
                        report.start()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    fun updateList(newList: List<BoardItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < itemList.size) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemList.size)
        }
    }
}
