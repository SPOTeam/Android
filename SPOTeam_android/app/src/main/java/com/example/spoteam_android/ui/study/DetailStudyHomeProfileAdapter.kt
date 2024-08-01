package com.example.spoteam_android.ui.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.databinding.ItemDetailStudyHomeMemberBinding

class DetailStudyHomeProfileAdapter(private val profiles: List<ProfileItem>) :
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
        holder.bind(profile)
    }

    override fun getItemCount(): Int = profiles.size

    inner class ProfileViewHolder(private val binding: ItemDetailStudyHomeMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: ProfileItem) {
            binding.fragmentDetailStudyHomeHostuserIv.setImageResource(profile.profileImage)
            binding.profileNickname.text = profile.nickname
        }
    }
}
