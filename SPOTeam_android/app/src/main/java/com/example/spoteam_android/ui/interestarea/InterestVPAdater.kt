package com.example.spoteam_android.ui.interestarea

import StudyViewModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding

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

    override fun getItemCount() = dataList.size

    inner class BoardViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val likeButton: ImageView = binding.heartCountIv

        fun bind(item: BoardItem) {
            binding.tvTime.text = item.title
            binding.tvTitle.text = item.goal
            binding.tvName.text = item.maxPeople.toString()
            binding.tvName2.text = item.memberCount.toString()
            binding.tvName3.text = item.heartCount.toString()
            binding.tvName4.text = item.hitNum.toString()

            // 이미지 로드
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.ImageView4)

            // 하트 아이콘 상태 설정
            val heartIcon = if (item.liked) R.drawable.ic_heart_filled else R.drawable.study_like
            binding.heartCountIv.setImageResource(heartIcon)
        }
    }

    fun updateList(newList: List<BoardItem>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }
}
