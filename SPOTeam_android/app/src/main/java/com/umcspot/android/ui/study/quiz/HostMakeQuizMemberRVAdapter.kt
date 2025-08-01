package com.umcspot.android.ui.study.quiz

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umcspot.android.R
import com.umcspot.android.databinding.ItemDetailStudyHomeMemberBinding
import com.umcspot.android.ui.community.MembersDetail

class HostMakeQuizMemberRVAdapter(
    private var dataList: List<MembersDetail>,
    private val myId : Int
) : RecyclerView.Adapter<HostMakeQuizMemberRVAdapter.ViewHolder>() {

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

            if (data.memberId == myId) {
                binding.fragmentDetailStudyHomeHostuserIv.strokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.b500))
                binding.fragmentDetailStudyHomeHostuserIv.strokeWidth = 4f
                binding.fragmentDetailStudyHomeHostuserIv.alpha = 1.0f


                binding.profileNickname.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))

            } else {
                binding.fragmentDetailStudyHomeHostuserIv.strokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, android.R.color.transparent))
                binding.fragmentDetailStudyHomeHostuserIv.strokeWidth = 0f
                binding.fragmentDetailStudyHomeHostuserIv.alpha = 0.4f

                // ✅ 텍스트 블러 적용
                binding.profileNickname.setTextColor(ContextCompat.getColor(binding.root.context, R.color.semi_black_40))
            }

        }
    }
}
