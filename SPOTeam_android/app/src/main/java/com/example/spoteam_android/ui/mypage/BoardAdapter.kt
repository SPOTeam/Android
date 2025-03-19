package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding

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
            Log.d("BoardAdapter",currentItem.toString())
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
//                showPopupMenu(it, item)
                showPopupMenu(it, item.studyId)
            }
        }

//        private fun showPopupMenu(view: View, item : BoardItem) {
//            val popupMenu = PopupMenu(view.context, view)
//            val inflater: MenuInflater = popupMenu.menuInflater
//            val exit = ExitStudyPopupFragment(view.context, this@BoardAdapter, adapterPosition)
//            val report = ReportStudyCrewDialog(view.context, item.studyId)
//            inflater.inflate(R.menu.menu_item_options, popupMenu.menu)
//
//            val endStudyItem = popupMenu.menu.findItem(R.id.end_study)
//            endStudyItem.isVisible = true //호스트인지 아닌지에 따라 변경 필요
//
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
//                    R.id.end_study -> {
//                        val endStudyDialog = EndStudyDialog(view.context, item.studyId)
//                        endStudyDialog.start()
//                        true
//                    }
//                    else -> false
//                }
//            }
//            popupMenu.show()
//        }
//    }

        private fun showPopupMenu(view: View, studyId: Int) {
            // 팝업 레이아웃 가져오기
            val popupView = LayoutInflater.from(view.context).inflate(R.layout.modify_study_popup_menu, null)

            // PopupWindow 생성
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            // 팝업 내부 요소 가져오기
            val editInfo = popupView.findViewById<TextView>(R.id.edit_info)
            val endStudy = popupView.findViewById<TextView>(R.id.end_study)
            val reportMember = popupView.findViewById<TextView>(R.id.report_member)
            val leaveStudy = popupView.findViewById<TextView>(R.id.leave_study)

            // 클릭 리스너 설정
            editInfo.setOnClickListener {
                Toast.makeText(view.context, "정보 수정하기 클릭됨", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            endStudy.setOnClickListener {
                // 스터디 종료 다이얼로그 띄우기
                val exitDialog = ExitStudyPopupFragment(view.context, this@BoardAdapter, adapterPosition)
                exitDialog.start()
                popupWindow.dismiss()
            }

            reportMember.setOnClickListener {
                // 스터디원 신고 다이얼로그 띄우기
                val reportDialog = ReportStudyCrewDialog(view.context, studyId)
                reportDialog.start()
                popupWindow.dismiss()
            }

            leaveStudy.setOnClickListener {
                // 스터디 탈퇴 다이얼로그 띄우기
                val reportDialog = HostLeaveStudyDialog(view.context, studyId)
                reportDialog.start()
                popupWindow.dismiss()
            }

            // 외부 클릭 시 닫힘 설정
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(view.context.getDrawable(R.drawable.custom_popup_background))

            val location = IntArray(2)
            view.getLocationOnScreen(location) // 화면 전체 기준 좌표 가져오기
            val x = location[0]
            val y = location[1]
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y + 100)
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
