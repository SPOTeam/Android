package com.example.spoteam_android.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemDetailStudyHomeMemberBinding
import com.example.spoteam_android.ui.community.MembersDetail

class ReportStudyCrewMemberRVAdapter(
    private var dataList: List<MembersDetail>,
    private val listener: OnMemberClickListener
) : RecyclerView.Adapter<ReportStudyCrewMemberRVAdapter.ViewHolder>() {

    interface OnMemberClickListener {
        fun onProfileClick(member: MembersDetail)
    }

    private var selectedPosition = RecyclerView.NO_POSITION // ✅ 선택된 프로필 위치 저장

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

            // ✅ 선택된 프로필이면 테두리 추가, 그렇지 않으면 초기화
            if (position == selectedPosition) {
                binding.fragmentDetailStudyHomeHostuserIv.setBackgroundResource(R.drawable.selected_border)
            } else {
                binding.fragmentDetailStudyHomeHostuserIv.setBackgroundResource(0) // 기본 배경
            }

            // ✅ 클릭 이벤트 처리
            binding.fragmentDetailStudyHomeHostuserIv.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position

                // ✅ 이전 선택된 항목 갱신 (테두리 제거)
                notifyItemChanged(previousPosition)
                // ✅ 새로 선택된 항목 갱신 (테두리 추가)
                notifyItemChanged(selectedPosition)

                // ✅ 클릭 리스너 호출
                listener.onProfileClick(data)
            }
        }
    }
}
