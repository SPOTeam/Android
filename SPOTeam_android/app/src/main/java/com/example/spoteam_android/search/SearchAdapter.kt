package com.example.spoteam_android.search

import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.ReportStudymemberFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment
import com.bumptech.glide.Glide
import com.example.spoteam_android.SearchItem

class SearchAdapter(
    private val itemList: ArrayList<SearchItem>,
    private val onItemClick: (SearchItem) -> Unit
) : RecyclerView.Adapter<SearchAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemRecyclerViewPlusToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            Log.d("SearchAdapter","{$currentItem}이 선택되었습니다")
            onItemClick(currentItem)
            Log.d("SearchAdapter", "onItemClick 호출됨")
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class BoardViewHolder(val binding: ItemRecyclerViewPlusToggleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchItem) {
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
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.ImageView4)

            binding.toggle.setOnClickListener {
                showPopupMenu(it)
            }
        }

        private fun showPopupMenu(view: View) {}
//            val popupMenu = PopupMenu(view.context, view)
//            val inflater: MenuInflater = popupMenu.menuInflater
//            val exit = ExitStudyPopupFragment(view.context, this@SearchAdapter, adapterPosition)
//            val report = ReportStudymemberFragment(view.context)
//            inflater.inflate(R.menu.menu_item_options, popupMenu.menu)
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.exit_study -> {
//                        exit.start()
//                        true
//                    }
//                    R.id.report_study -> {
//                        report.start()
//                        true
//                    }
//                    else -> false
//                }
//            }
//            popupMenu.show()
//        }
    }


    fun removeItem(position: Int) {
        if (position >= 0 && position < itemList.size) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    fun updateList(newList: List<SearchItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}