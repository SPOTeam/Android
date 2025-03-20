package com.example.spoteam_android.ui.mypage

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemDetailStudyHomeMemberBinding
import com.example.spoteam_android.ui.community.MembersDetail
import com.google.android.material.imageview.ShapeableImageView

class ReportStudyCrewMemberRVAdapter(
    private var dataList: List<MembersDetail>,
    private val listener: OnMemberClickListener
) : RecyclerView.Adapter<ReportStudyCrewMemberRVAdapter.ViewHolder>() {

    private var selectedPosition = -1

    interface OnMemberClickListener {
        fun onProfileClick(member: MembersDetail)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDetailStudyHomeMemberBinding =
            ItemDetailStudyHomeMemberBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], position)
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(private val binding: ItemDetailStudyHomeMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MembersDetail, position: Int) {
            Glide.with(binding.root.context)
                .load(data.profileImage)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.fragmentDetailStudyHomeHostuserIv)

            binding.profileNickname.text = data.nickname

            // 방장 아이콘 표시
            binding.fragmentConsiderAttendanceMemberHostIv.visibility = if (position == 0) View.VISIBLE else View.GONE

            val imageView = binding.fragmentDetailStudyHomeHostuserIv
            val textView = binding.profileNickname


            // ✅ 선택된 상태에서 테두리 적용
            if (selectedPosition == position) {
                imageView.strokeWidth = 6f  // 두께 설정
                imageView.strokeColor = binding.root.context.getColorStateList(R.color.red)  // 빨간색 테두리
                textView.setTextColor(Color.RED)
            } else {
                imageView.strokeWidth = 0f  // 테두리 제거
                textView.setTextColor(Color.BLACK)
            }
            // ✅ 클릭 이벤트 처리
            binding.fragmentDetailStudyHomeHostuserIv.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition) // 이전 선택 해제
                notifyItemChanged(selectedPosition) // 새로운 선택 반영
                listener.onProfileClick(data)
            }
        }
    }
}
