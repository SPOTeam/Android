package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.ReportStudyMemberFragment
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.GetHostResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardAdapter(
    private val itemList: ArrayList<BoardItem>,
    private val onItemClick: (BoardItem) -> Unit,
    private val onLikeClick: (BoardItem, ImageView) -> Unit // onLikeClick 추가
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemRecyclerViewPlusToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
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
                showPopupMenu(it, item.studyId)
            }
        }

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
            val view1 = popupView.findViewById<View>(R.id.view_1)
            val endStudy = popupView.findViewById<TextView>(R.id.end_study)
            val view2 = popupView.findViewById<View>(R.id.view_2)
            val reportMember = popupView.findViewById<TextView>(R.id.report_member)
            val view3 = popupView.findViewById<View>(R.id.view_3)
            val leaveStudy = popupView.findViewById<TextView>(R.id.leave_study)

            getIsHost(studyId, endStudy, view1, reportMember, view2)


            // 클릭 리스너 설정
            editInfo.setOnClickListener {
                Toast.makeText(view.context, "정보 수정하기 클릭됨", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }

            endStudy.setOnClickListener {
                // 스터디 종료 다이얼로그 띄우기
                val exitDialog = EndStudyDialog(view.context, studyId)
                exitDialog.start()
                popupWindow.dismiss()
            }

            reportMember.setOnClickListener {
                // 스터디원 신고 다이얼로그 띄우기
                val reportDialog = ReportStudyMemberFragment(view.context, studyId)
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

    fun getIsHost(
        studyId: Int,
        endStudy: TextView,
        view1: View,
        reportMember: TextView,
        view2: View
    ) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyHost(studyId)
            .enqueue(object : Callback<GetHostResponse> {
                override fun onResponse(
                    call: Call<GetHostResponse>,
                    response: Response<GetHostResponse>
                ) {
                    if (response.isSuccessful) {
                        val hostResponse = response.body()
                        if (hostResponse?.isSuccess == true) {
                            Log.d("isOwned", hostResponse.code)
                            if(hostResponse.result.isOwned) {
                                endStudy.visibility = View.VISIBLE
                                view1.visibility = View.VISIBLE
                                reportMember.visibility = View.VISIBLE
                                view2.visibility = View.VISIBLE
                            } else {
                                endStudy.visibility = View.GONE
                                view1.visibility = View.GONE
                                reportMember.visibility = View.GONE
                                view2.visibility = View.GONE
                            }
                        } else {
                            showError(hostResponse?.message)
                            endStudy.visibility = View.GONE
                            view1.visibility = View.GONE
                            reportMember.visibility = View.GONE
                            view2.visibility = View.GONE
                        }
                    } else {
                        showError(response.code().toString())
                        endStudy.visibility = View.GONE
                        view1.visibility = View.GONE
                        reportMember.visibility = View.GONE
                        view2.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<GetHostResponse>, t: Throwable) {
                    Log.e("MyStudyAttendance", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
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
