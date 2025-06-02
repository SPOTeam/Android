package com.example.spoteam_android.presentation.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding
import com.example.spoteam_android.domain.study.entity.BoardItem

class SearchAdapter(
    private val itemList: ArrayList<BoardItem>,
    private val onItemClick: (BoardItem) -> Unit
) : RecyclerView.Adapter<SearchAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemRecyclerViewPlusToggleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem) // ViewHolder의 bind 메서드로 데이터 전달

        holder.itemView.setOnClickListener {
            Log.d("SearchAdapter", "{$currentItem}이 선택되었습니다")
            onItemClick(currentItem)
            Log.d("SearchAdapter", "onItemClick 호출됨")
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class BoardViewHolder(val binding: ItemRecyclerViewPlusToggleBinding) : RecyclerView.ViewHolder(binding.root) {

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



    fun updateList(newList: List<BoardItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}