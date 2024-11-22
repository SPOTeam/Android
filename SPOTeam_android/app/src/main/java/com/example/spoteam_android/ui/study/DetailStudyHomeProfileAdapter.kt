package com.example.spoteam_android.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemDetailStudyHomeMemberBinding

class DetailStudyHomeProfileAdapter(
    private var profiles: MutableList<ProfileItem>,
    private val onItemClick: ((ProfileItem) -> Unit)? = null // 기본값 null
) :
    RecyclerView.Adapter<DetailStudyHomeProfileAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemDetailStudyHomeMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.bind(profile, position == 0)
    }

    override fun getItemCount(): Int = profiles.size

    inner class ProfileViewHolder(private val binding: ItemDetailStudyHomeMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: ProfileItem, isHost: Boolean) {
            // Glide를 사용하여 프로필 이미지 로드
            Glide.with(binding.root.context)
                .load(profile.profileImage)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.fragmentDetailStudyHomeHostuserIv)

            val nickname = if (profile.nickname.length == 2) {
                " ${profile.nickname}"
            } else {
                profile.nickname
            }


            binding.profileNickname.text = nickname

            if (isHost) {
                binding.fragmentConsiderAttendanceMemberHostIv.visibility = View.VISIBLE
            } else {
                binding.fragmentConsiderAttendanceMemberHostIv.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick?.invoke(profile) // onItemClick이 null이 아닌 경우에만 호출
            }

        }
    }

    fun updateList(newProfiles: List<ProfileItem>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        notifyDataSetChanged()
    }
}