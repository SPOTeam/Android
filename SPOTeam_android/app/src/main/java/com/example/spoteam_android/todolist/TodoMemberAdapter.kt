package com.example.spoteam_android.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.Member
import com.example.spoteam_android.R

class TodoMemberAdapter(
    private val members: List<Member>, // 멤버 리스트
    private val onMemberClick: (Int) -> Unit // 클릭 이벤트 콜백
) : RecyclerView.Adapter<TodoMemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.fragment_detail_study_home_hostuser_iv)
        private val nickname: TextView = itemView.findViewById(R.id.profile_nickname)

        fun bind(member: Member) {
            nickname.text = member.nickname
            Glide.with(itemView.context)
                .load(member.profileImage) // 프로필 이미지 로드
                .placeholder(R.drawable.fragment_calendar_spot_logo) // 기본 이미지
                .into(profileImage)

            itemView.setOnClickListener {
                onMemberClick(member.memberId) // 클릭 시 memberId 전달
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_study_home_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size
}
