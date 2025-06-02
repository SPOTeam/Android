package com.example.spoteam_android.presentation.community.contentComment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemContentCommentBinding
import com.example.spoteam_android.databinding.ItemContentCommentReplyBinding
import com.example.spoteam_android.presentation.community.CommentsInfo

class ContentCommentMultiViewRVAdapter(private val dataList: List<CommentsInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClick {
        fun onItemClick(view: View, position: Int, parentCommentId : Int)
        fun onLikeClick(view: View, position: Int, commentId : Int)
        fun onUnLikeClick(view: View, position: Int, commentId : Int)
        fun onDisLikeClick(view: View, position: Int, commentId : Int)
        fun onUnDisLikeClick(view: View, position: Int, commentId : Int)
    }

    var itemClick: ItemClick? = null
    private val clickedState = MutableList(dataList.size) { false } // 각 아이템의 클릭 상태를 저장

    fun resetClickedState() {
        clickedState.indices.forEach { clickedState[it] = false }
        notifyDataSetChanged() // 리스트 갱신
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            0 -> {
                val binding = ItemContentCommentBinding.inflate(inflater, parent, false)
                CommentViewHolder(binding)
            }
            else -> {
                val binding = ItemContentCommentReplyBinding.inflate(inflater, parent, false)
                ReplyCommentViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return dataList[position].parentCommentId
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        when (holder) {
            is CommentViewHolder -> {
                holder.bind(item)
            }
            is ReplyCommentViewHolder -> {
                holder.bind(item)
            }
        }
    }

    inner class CommentViewHolder(private val binding : ItemContentCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentsInfo) {
            binding.communityContentCommentStrTv.text = item.commentContent
            binding.communityContentCommentGoodNumTv.text = item.likeCount.toString()

            if(item.anonymous) {
                binding.communityContentCommentWriterTv.text = "익명"
                Glide.with(binding.root.context)
                    .load(R.drawable.fragment_calendar_spot_logo)
                    .into(binding.communityContentCommentProfileIv)
            } else {
                binding.communityContentCommentWriterTv.text = item.writer
                Glide.with(binding.root.context)
                    .load(item.profileImage)
                    .into(binding.communityContentCommentProfileIv)
            }

            if (item.likedByCurrentUser) {
                binding.communityContentCommentGoodCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentGoodUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentGoodCheckedIv.visibility = View.GONE
                binding.communityContentCommentGoodUncheckedIv.visibility = View.VISIBLE
            }

            if (item.dislikedByCurrentUser) {
                binding.communityContentCommentBadCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentBadUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentBadCheckedIv.visibility = View.GONE
                binding.communityContentCommentBadUncheckedIv.visibility = View.VISIBLE
            }

            // 클릭 상태에 따른 communityWriteReplyTv 텍스트 색상 변경
            binding.communityWriteReplyTv.setTextColor(
                if (clickedState[bindingAdapterPosition]) binding.root.context.getColor(R.color.active_blue) // 파란색으로 변경
                else binding.root.context.getColor(R.color.gray_03) // 기본 색상으로 변경
            )
            binding.communityWriteReplyTv.setOnClickListener {
                updateClickedState(bindingAdapterPosition) // 클릭 상태 갱신
                itemClick?.onItemClick(it, bindingAdapterPosition, item.commentId)
            }

            binding.communityContentCommentGoodUncheckedIv.setOnClickListener {
                itemClick?.onUnLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            binding.communityContentCommentGoodCheckedIv.setOnClickListener {
                itemClick?.onLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            binding.communityContentCommentBadUncheckedIv.setOnClickListener {
                itemClick?.onUnDisLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            binding.communityContentCommentBadCheckedIv.setOnClickListener {
                itemClick?.onDisLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            val isReplyExists = hasReplyComments(bindingAdapterPosition, item.commentId)
            binding.divider.visibility = if (!isReplyExists) View.VISIBLE else View.GONE
        }
    }

    private fun hasReplyComments(position: Int, commentId: Int): Boolean {
        // 현재 댓글 이후의 항목들 중에서 parentCommentId가 이 댓글의 commentId인 것이 있으면 대댓글이 있는 것
        for (i in position + 1 until dataList.size) {
            if (dataList[i].parentCommentId == commentId) {
                return true
            }
            // 다른 댓글이 나오면 탐색 종료
            if (dataList[i].parentCommentId == 0) break
        }
        return false
    }

    private fun isLastReplyOfGroup(position: Int, parentId: Int): Boolean {
        // 다음 댓글이 없거나, 다음 댓글의 부모 ID가 현재와 다르면 마지막 대댓글이다.
        if (position + 1 >= dataList.size) return true
        return dataList[position + 1].parentCommentId != parentId
    }

    inner class ReplyCommentViewHolder(private val binding : ItemContentCommentReplyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentsInfo) {
            binding.communityContentCommentReplyStrTv.text = item.commentContent
            binding.communityContentCommentReplyGoodNumTv.text = item.likeCount.toString()

            if(item.anonymous) {
                binding.communityContentCommentReplyWriterTv.text = "익명"
                Glide.with(binding.root.context)
                    .load(R.drawable.fragment_calendar_spot_logo)
                    .into(binding.communityContentCommentReplyProfileIv)
            } else {
                binding.communityContentCommentReplyWriterTv.text = item.writer
                Glide.with(binding.root.context)
                    .load(item.profileImage)
                    .into(binding.communityContentCommentReplyProfileIv)
            }

            if(item.likedByCurrentUser) {
                binding.communityContentCommentReplyGoodCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentReplyGoodUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentReplyGoodCheckedIv.visibility = View.GONE
                binding.communityContentCommentReplyGoodUncheckedIv.visibility = View.VISIBLE
            }

            if(item.dislikedByCurrentUser) {
                binding.communityContentCommentReplyBadCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentReplyBadUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentReplyBadCheckedIv.visibility = View.GONE
                binding.communityContentCommentReplyBadUncheckedIv.visibility = View.VISIBLE
            }

            // 클릭 상태에 따른 communityReplyWriteReplyTv 텍스트 색상 변경
            binding.communityReplyWriteReplyTv.setTextColor(
                if (clickedState[bindingAdapterPosition]) binding.root.context.getColor(R.color.blue) // 파란색으로 변경
                else binding.root.context.getColor(R.color.gray_03) // 기본 색상으로 변경
            )

            binding.communityReplyWriteReplyTv.setOnClickListener {
                updateClickedState(bindingAdapterPosition) // 클릭 상태 갱신
                itemClick?.onItemClick(it, bindingAdapterPosition, item.parentCommentId)
            }

            binding.communityContentCommentReplyGoodUncheckedIv.setOnClickListener {
                itemClick?.onUnLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            binding.communityContentCommentReplyGoodCheckedIv.setOnClickListener {
                itemClick?.onLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            binding.communityContentCommentReplyBadUncheckedIv.setOnClickListener {
                itemClick?.onUnDisLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            binding.communityContentCommentReplyBadCheckedIv.setOnClickListener {
                itemClick?.onDisLikeClick(it, bindingAdapterPosition, item.commentId)
            }

            // 마지막 대댓글 아래에만 divider 보이도록
            val isLastReply = isLastReplyOfGroup(bindingAdapterPosition, item.parentCommentId)
            binding.divider.visibility = if (isLastReply) View.VISIBLE else View.GONE
        }
    }
    // 클릭 상태를 업데이트하는 함수
    private fun updateClickedState(position: Int) {
        // 모든 상태를 false로 초기화하고, 현재 클릭된 아이템만 true로 설정
        clickedState.indices.forEach { clickedState[it] = false }
        clickedState[position] = true
        notifyDataSetChanged() // 데이터 변경을 반영하여 전체 리스트 갱신
    }
}
