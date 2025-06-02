package com.example.spoteam_android.presentation.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemContentCommentBinding
import com.example.spoteam_android.databinding.ItemContentCommentReplyBinding
import com.example.spoteam_android.presentation.community.StudyPostContentCommentDetail

data class CommentItem(
    val commentDetail: StudyPostContentCommentDetail,
    val isReply: Boolean
)

class MyStudyContentCommentMultiViewRVAdapter(private val dataList: MutableList<CommentItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClick {
        fun onItemClick(parentId: Int?)
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

    fun setData(comments: List<StudyPostContentCommentDetail>) {
        dataList.clear()
        comments.forEach { comment ->
            dataList.add(CommentItem(comment, false)) // 댓글
            comment.applies.forEach { reply ->
                dataList.add(CommentItem(reply, true)) // 대댓글
            }
        }
        clickedState.clear() // 기존 상태 초기화
        clickedState.addAll(List(dataList.size) { false }) // 새로운 데이터 크기만큼 초기화
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val binding = ItemContentCommentBinding.inflate(inflater, parent, false)
                MyStudyCommentViewHolder(binding)
            }

            else -> {
                val binding = ItemContentCommentReplyBinding.inflate(inflater, parent, false)
                MyStudyReplyCommentViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].isReply) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position].commentDetail
        when (holder) {
            is MyStudyCommentViewHolder -> {
                holder.bind(item)
            }

            is MyStudyReplyCommentViewHolder -> {
                // 대댓글인 경우, 상위 댓글의 ID를 찾습니다.
                val parentCommentId = findParentCommentId(position)
                holder.bind(item, parentCommentId)
            }
        }
    }

    private fun findParentCommentId(position: Int): Int? {
        // position에서 위로 올라가면서 댓글을 찾음
        for (i in position downTo 0) {
            if (!dataList[i].isReply) {
                return dataList[i].commentDetail.commentId
            }
        }
        return null
    }

    inner class MyStudyCommentViewHolder(private val binding: ItemContentCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyPostContentCommentDetail) {
            binding.communityContentCommentWriterTv.text = item.member.name
            binding.communityContentCommentStrTv.text = item.content
            binding.communityContentCommentGoodNumTv.text = item.likeCount.toString()

            Glide.with(binding.root.context)
                .load(item.member.profileImage)
                .into(binding.communityContentCommentProfileIv)

            if (item.isLiked == "LIKED") {
                binding.communityContentCommentGoodCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentGoodUncheckedIv.visibility = View.GONE
            }
            if (item.isLiked == "NONE") {
                binding.communityContentCommentGoodCheckedIv.visibility = View.GONE
                binding.communityContentCommentGoodUncheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentBadCheckedIv.visibility = View.GONE
                binding.communityContentCommentBadUncheckedIv.visibility = View.VISIBLE
            }
            if (item.isLiked == "DISLIKED") {
                binding.communityContentCommentBadCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentBadUncheckedIv.visibility = View.GONE
            }

            binding.communityWriteReplyTv.setOnClickListener {
                updateClickedState(bindingAdapterPosition) // 클릭 상태 갱신
                itemClick?.onItemClick(item.commentId)  // 댓글의 ID를 전달, parentId는 null로 설정
            }

            // 클릭 상태에 따른 communityWriteReplyTv 텍스트 색상 변경
            binding.communityWriteReplyTv.setTextColor(
                if (clickedState[bindingAdapterPosition]) binding.root.context.getColor(R.color.b500) // 파란색으로 변경
                else binding.root.context.getColor(R.color.gray_03) // 기본 색상으로 변경
            )

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

        }
    }


    inner class MyStudyReplyCommentViewHolder(private val binding: ItemContentCommentReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyPostContentCommentDetail, parentId: Int?) {
            binding.communityContentCommentReplyWriterTv.text = item.member.name
            binding.communityContentCommentReplyStrTv.text = item.content
            binding.communityContentCommentReplyGoodNumTv.text = item.likeCount.toString()

            Glide.with(binding.root.context)
                .load(item.member.profileImage)
                .into(binding.communityContentCommentReplyProfileIv)

            if (item.isLiked == "LIKED") {
                binding.communityContentCommentReplyGoodCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentReplyGoodUncheckedIv.visibility = View.GONE
            }
            if (item.isLiked == "NONE"){
                binding.communityContentCommentReplyGoodCheckedIv.visibility = View.GONE
                binding.communityContentCommentReplyGoodUncheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentReplyBadCheckedIv.visibility = View.GONE
                binding.communityContentCommentReplyBadUncheckedIv.visibility = View.VISIBLE
            }
            if (item.isLiked == "DISLIKED") {
                binding.communityContentCommentReplyBadCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentReplyBadUncheckedIv.visibility = View.GONE
            }

            binding.communityReplyWriteReplyTv.setOnClickListener {
                updateClickedState(bindingAdapterPosition) // 클릭 상태 갱신
                itemClick?.onItemClick(parentId)  // 대댓글의 ID와 부모 댓글의 ID 반환
            }

            // 클릭 상태에 따른 communityReplyWriteReplyTv 텍스트 색상 변경
            binding.communityReplyWriteReplyTv.setTextColor(
                if (clickedState[bindingAdapterPosition]) binding.root.context.getColor(R.color.b500) // 파란색으로 변경
                else binding.root.context.getColor(R.color.gray_03) // 기본 색상으로 변경
            )

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
        }
    }

    private fun updateClickedState(position: Int) {
        if (position in clickedState.indices) {
            clickedState.indices.forEach { clickedState[it] = false }
            clickedState[position] = true
            notifyDataSetChanged()
        }
    }
}
