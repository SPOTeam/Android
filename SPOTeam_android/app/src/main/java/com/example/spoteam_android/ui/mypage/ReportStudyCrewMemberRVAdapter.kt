package com.example.spoteam_android.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemDetailStudyHomeMemberBinding
import com.example.spoteam_android.ui.community.MembersDetail

class ReportStudyCrewMemberRVAdapter(
    private var dataList: List<MembersDetail>,
    private val listener: Any // ✅ 클릭 이벤트 리스너 추가
) : RecyclerView.Adapter<ReportStudyCrewMemberRVAdapter.ViewHolder>() {
    interface OnMemberClickListener {
        fun onProfileClick(member: MembersDetail)
    }

    private lateinit var itemClickListener: OnMemberClickListener

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDetailStudyHomeMemberBinding = ItemDetailStudyHomeMemberBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dataList.size) {
            holder.bind(dataList[position], position == 0)
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding: ItemDetailStudyHomeMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MembersDetail, isOwned: Boolean) {
            Glide.with(binding.root.context)
                .load(data.profileImage)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.fragmentDetailStudyHomeHostuserIv)

            binding.profileNickname.text = data.nickname

            if(isOwned) {
                binding.fragmentConsiderAttendanceMemberHostIv.visibility = View.VISIBLE
            } else {
                binding.fragmentConsiderAttendanceMemberHostIv.visibility = View.GONE
            }


            binding.fragmentDetailStudyHomeHostuserIv.setOnClickListener{
                binding.fragmentDetailStudyHomeHostuserIv.setBackgroundResource(R.drawable.selected_border)
            }

        }
    }
}
