package com.example.spoteam_android.presentation.interestarea

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding
import com.example.spoteam_android.domain.study.entity.BoardItem
import com.example.spoteam_android.presentation.study.StudyViewModel

class InterestVPAdapter(
    var dataList: ArrayList<BoardItem>,
    private val onLikeClick: (BoardItem, ImageView) -> Unit,
    private val studyViewModel: StudyViewModel // ViewModel 추가
) : RecyclerView.Adapter<InterestVPAdapter.BoardViewHolder>() {

    interface OnItemClickListeners {
        fun onItemClick(data: BoardItem)
    }

    private lateinit var itemClickListener: OnItemClickListeners

    fun setItemClickListener(onItemClickListener: OnItemClickListeners) {
        this.itemClickListener = onItemClickListener
    }

    fun addData(newData: List<BoardItem>) {
        dataList.addAll(newData)
        notifyDataSetChanged()
    }
    fun getCurrentList(): List<BoardItem> = dataList


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BoardViewHolder {
        val binding: ItemRecyclerViewBinding = ItemRecyclerViewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)

        // 항목 클릭 이벤트 설정
        holder.itemView.setOnClickListener {
            if (::itemClickListener.isInitialized) {
                itemClickListener.onItemClick(currentItem)
            }
            studyViewModel.setRecentStudyId(currentItem.studyId)
        }

        // 좋아요 클릭 이벤트 설정
        holder.likeButton.setOnClickListener {
            onLikeClick(currentItem, holder.likeButton)
            Log.d("InterestVPAdapter", "Like button clicked for item: ${currentItem.studyId}")
        }
    }

    inner class BoardViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val likeButton: ImageView = binding.heartCountIv

        fun bind(item: BoardItem) {
            binding.tvTime.text = item.title
            binding.tvTitle.text = item.goal
            binding.tvName.text = item.maxPeople.toString()
            binding.tvName2.text = item.memberCount.toString()
            binding.tvName3.text = item.heartCount.toString()
            binding.tvName4.text = item.hitNum.toString()

            // Glide를 활용한 이미지 로드
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지
                .into(binding.ImageView4)

            // 하트 아이콘 상태 설정
            val heartIcon = if (item.liked) R.drawable.ic_heart_filled else R.drawable.study_like
            binding.heartCountIv.setImageResource(heartIcon)
        }
    }


    override fun getItemCount() = dataList.size


    fun updateList(newList: List<BoardItem>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }
}
