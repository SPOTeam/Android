package com.umcspot.android.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umcspot.android.ProfileTemperatureItem
import com.umcspot.android.databinding.ItemConsiderAttendanceMemberBinding

class ConsiderAttendanceMemberVPAdapter(
    private val profileList: List<ProfileTemperatureItem>
) : RecyclerView.Adapter<ConsiderAttendanceMemberVPAdapter.ProfileViewHolder>() {

    inner class ProfileViewHolder(val binding: ItemConsiderAttendanceMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemConsiderAttendanceMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = profileList[position]
        holder.binding.fragmentConsiderAttendanceMemberProfileIv.setImageResource(item.profileImage)
        holder.binding.fragmentCosiderAttendanceMemberProfileTv.text = item.nickname
//        holder.binding.fragmentCosiderAttendanceMemberTemperatureTv.text = item.temperature
    }

    override fun getItemCount(): Int = profileList.size
}
