
package com.spot.android.ui.study

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spot.android.ProfileItem
import com.spot.android.R
import com.spot.android.databinding.ItemDetailStudyHomeMemberBinding

class DetailStudyHomeProfileAdapter(
    private var profiles: MutableList<ProfileItem>,
    private val onItemClick: ((ProfileItem) -> Unit)? = null, // 다른 스터디원 투두리스트 조회할 때, 클릭 이벤트 전달용
    private val isTodo: Boolean = false // 투두 프래그먼트 여부 플래그
) :
    RecyclerView.Adapter<DetailStudyHomeProfileAdapter.ProfileViewHolder>() {

    private var selectedPosition: Int? = null // 다른 스터디원 투두리스트 조회할 때, 클릭 이벤트 전달용

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
        val isSelected = position == selectedPosition
        holder.bind(profile, position == 0, isSelected, isTodo)
    }

    override fun getItemCount(): Int = profiles.size

    inner class ProfileViewHolder(private val binding: ItemDetailStudyHomeMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: ProfileItem, isHost: Boolean, isSelected: Boolean, isTodo: Boolean) {
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
            // 투두리스트 FRAGMENT 일 때만, 프로필에 투명도, Clickable 적용
            if (isTodo) {
                //다른 스터디원 투두리스트 조회할 때, 클릭 이벤트 전달용
                if (isSelected) {
                    binding.fragmentDetailStudyHomeHostuserIv.strokeColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.b400
                            )
                        )
                    binding.fragmentDetailStudyHomeHostuserIv.strokeWidth = 6f
                    binding.fragmentDetailStudyHomeHostuserIv.alpha = 1f
                    binding.profileNickname.alpha = 1f
                    if (isHost) {
                        binding.fragmentConsiderAttendanceMemberHostIv.alpha = 1f
                    }
                } else {
                    binding.fragmentDetailStudyHomeHostuserIv.strokeColor =
                        ColorStateList.valueOf(
                                ContextCompat.getColor(
                                binding.root.context,
                                android.R.color.transparent
                            )
                        )
                    binding.fragmentDetailStudyHomeHostuserIv.strokeWidth = 0f
                    binding.fragmentDetailStudyHomeHostuserIv.alpha = 0.5f
                    binding.fragmentConsiderAttendanceMemberHostIv.alpha = 0.5f
                    binding.profileNickname.alpha = 0.5f
                }

                // 다른 스터디원 투두리스트 조회할 때, 클릭 이벤트 전달 후 테두리 표시
                binding.root.setOnClickListener {
                    // 기존 선택된 위치와 현재 선택된 위치가 다를 경우 업데이트
                    if (selectedPosition != bindingAdapterPosition) {
                        val previousPosition = selectedPosition
                        selectedPosition = bindingAdapterPosition

                        // 이전 선택된 항목과 현재 선택된 항목 UI 갱신
                        previousPosition?.let { notifyItemChanged(it) }
                        notifyItemChanged(bindingAdapterPosition)
                    }
                    // 콜백 실행
                    onItemClick?.invoke(profile)
                }
            }
        }
    }

    fun updateList(newProfiles: List<ProfileItem>) {
        profiles.clear()
        profiles.addAll(newProfiles)
        selectedPosition = null // 선택 상태 초기화
        notifyDataSetChanged()
    }

    fun resetBorder() {
        val previousPosition = selectedPosition
        selectedPosition = null // 선택 상태 초기화
        previousPosition?.let {
            notifyItemChanged(it)
        }
    }
}
