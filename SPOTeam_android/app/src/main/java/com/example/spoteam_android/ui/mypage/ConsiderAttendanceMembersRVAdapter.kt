package com.example.spoteam_android.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemConsiderAttendanceMemberBinding
import com.example.spoteam_android.ui.community.AttendanceMemberInfo
import com.example.spoteam_android.ui.community.MyRecruitingStudyDetail

class ConsiderAttendanceMembersRVAdapter(private var dataList: List<AttendanceMemberInfo>) : RecyclerView.Adapter<ConsiderAttendanceMembersRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: AttendanceMemberInfo)
//        fun onLikeClick(data: CategoryPagesDetail)
//        fun onUnLikeClick(data: CategoryPagesDetail)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemConsiderAttendanceMemberBinding = ItemConsiderAttendanceMemberBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dataList.size) {
            holder.bind(dataList[position])
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding: ItemConsiderAttendanceMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AttendanceMemberInfo) {
//            binding.fragmentConsiderAttendanceMemberProfileIv.setImageResource(data.profileImage.)
            binding.fragmentCosiderAttendanceMemberProfileTv.text = data.nickname
            Glide.with(binding.root.context)
                .load(data.profileImage)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.fragmentConsiderAttendanceMemberProfileIv)


        }
    }
}
