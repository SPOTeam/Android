package com.umcspot.android.ui.mypage

import StudyFormMode
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umcspot.android.BoardItem
import com.umcspot.android.HostApiResponse
import com.umcspot.android.HostResult
import com.umcspot.android.R
import com.umcspot.android.ReportStudyMemberFragment
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.databinding.ItemRecyclerViewPlusToggleBinding
import com.umcspot.android.ui.interestarea.GetHostInterface
import com.umcspot.android.ui.study.RegisterStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardAdapter(
    private val itemList: ArrayList<BoardItem>,
    private val onItemClick: (BoardItem) -> Unit,
    private val onLikeClick: (BoardItem, ImageView) -> Unit, // onLikeClick 추가
    private val listener: fetchProgressStudy? = null, // 🔹추가

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
            getHost(studyId) { hostResult ->
                if (hostResult != null) {
                    val isHost = hostResult.isOwned  // ✅ 호스트 여부 확인

//                    Log.d("BoardAdapter", "StudyId : ${studyId}")

                    val popupView = LayoutInflater.from(view.context)
                        .inflate(R.layout.modify_study_popup_menu, null)

                    // PopupWindow 생성
                    val popupWindow = PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                    )

                    val editInfo = popupView.findViewById<TextView>(R.id.edit_info)
                    val view1 = popupView.findViewById<View>(R.id.view_1)
                    val endStudy = popupView.findViewById<TextView>(R.id.end_study)
                    val view2 = popupView.findViewById<View>(R.id.view_2)
                    val reportMember = popupView.findViewById<TextView>(R.id.report_member)
                    val view3 = popupView.findViewById<View>(R.id.view_3)
                    val leaveStudy = popupView.findViewById<TextView>(R.id.leave_study)

                    if (isHost) {
                        editInfo.visibility = View.VISIBLE
                        view1.visibility = View.VISIBLE
                        endStudy.visibility = View.VISIBLE
                        view2.visibility = View.VISIBLE
                        reportMember.visibility = View.VISIBLE
                        view3.visibility = View.VISIBLE
                        leaveStudy.visibility = View.VISIBLE
                    } else {
                        editInfo.visibility = View.GONE
                        view1.visibility = View.GONE
                        endStudy.visibility = View.GONE
                        view2.visibility = View.GONE
                        reportMember.visibility = View.VISIBLE
                        view3.visibility = View.VISIBLE
                        leaveStudy.visibility = View.VISIBLE
                    }

                   editInfo.setOnClickListener {
                popupWindow.dismiss()

                val fragment = RegisterStudyFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("mode", StudyFormMode.EDIT)
                        putInt("studyId", studyId)
                    }
                }

                // Fragment 이동
                (view.context as? AppCompatActivity)?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.main_frm, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }

                    endStudy.setOnClickListener {
                        // 스터디 끝내기 다이얼로그 띄우기
                        val endStudyDialog = EndStudyDialog(view.context, studyId, onComplete = {
                            listener?.fetchProgress() // ✅ 여기서 호출
                        })
                        endStudyDialog.start()
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
                        if (isHost) { // 호스트 탈퇴
                            val hostLeaveDialog = HostLeaveStudyDialog(view.context, studyId, onComplete = {
                                listener?.fetchProgress() // ✅ 여기서 호출
                            })
                            hostLeaveDialog.start()
                            popupWindow.dismiss()
                        } else { //
                            // 스터디 탈퇴 다이얼로그 띄우기
                            val hostLeaveDialog = MemberLeaveStudyDialog(
                                context = view.context,
                                studyID = studyId
                                , onComplete = {
                                    listener?.fetchProgress() // ✅ 여기서 호출
                                }
                            )

                            hostLeaveDialog.start()
                            popupWindow.dismiss()
                        }
                    }

                    // 외부 클릭 시 닫힘 설정
                    popupWindow.isOutsideTouchable = true
                    popupWindow.isFocusable = true
                    popupWindow.setBackgroundDrawable(view.context.getDrawable(R.drawable.custom_popup_background))

                    val location = IntArray(2)
                    view.getLocationOnScreen(location)
                    val x = location[0]
                    val y = location[1]

                    val offsetX = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        75f,
                        view.resources.displayMetrics
                    ).toInt()

                    popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x - offsetX, y + 55)
                }
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

        private fun getHost(studyId: Int, callback: (HostResult?) -> Unit) {
            val apiService = RetrofitInstance.retrofit.create(GetHostInterface::class.java)

            apiService.getHost(studyId).enqueue(object : Callback<HostApiResponse> {
                override fun onResponse(
                    call: Call<HostApiResponse>,
                    response: Response<HostApiResponse>
                ) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d("HostRepository", "호스트 정보 성공적으로 가져옴: ${response.body()}")
                        callback(response.body()?.result)
                    } else {
                        Log.e("HostRepository", "호스트 정보 가져오기 실패: ${response.errorBody()?.string()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<HostApiResponse>, t: Throwable) {
                    Log.e("HostRepository", "네트워크 오류: ${t.message}")
                    callback(null)
                }
            })


        }

    }
}
