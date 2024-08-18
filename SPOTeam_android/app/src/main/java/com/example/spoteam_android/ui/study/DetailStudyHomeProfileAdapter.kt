package com.example.spoteam_android.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemDetailStudyHomeMemberBinding

class DetailStudyHomeProfileAdapter(private var profiles: MutableList<ProfileItem>) :
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
                .load(profile.profileImage)  // URL 문자열
                .into(binding.fragmentDetailStudyHomeHostuserIv)

            binding.profileNickname.text = profile.nickname

            if (isHost) {
                binding.fragmentConsiderAttendanceMemberHostIv.visibility = View.VISIBLE
            } else {
                binding.fragmentConsiderAttendanceMemberHostIv.visibility = View.GONE
            }
        }
    }

    fun updateList(newProfiles: List<ProfileItem>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        notifyDataSetChanged()
    }
}
