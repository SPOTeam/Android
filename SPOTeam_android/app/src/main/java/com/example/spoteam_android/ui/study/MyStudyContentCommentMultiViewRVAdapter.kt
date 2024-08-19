package com.example.spoteam_android.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemContentCommentBinding
import com.example.spoteam_android.databinding.ItemContentCommentReplyBinding
import com.example.spoteam_android.ui.community.StudyPostContentCommentDetail

data class CommentItem(
    val commentDetail: StudyPostContentCommentDetail,
    val isReply: Boolean
)


class MyStudyContentCommentMultiViewRVAdapter(private val dataList: MutableList<CommentItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    interface ItemClick {
////        fun onItemClick(view: View, position: Int, parentCommentId : Int)
////        fun onLikeClick(view: View, position: Int, commentId : Int)
////        fun onUnLikeClick(view: View, position: Int, commentId : Int)
////        fun onDisLikeClick(view: View, position: Int, commentId : Int)
////        fun onUnDisLikeClick(view: View, position: Int, commentId : Int)
//    }
//
//    var itemClick: ItemClick? = null
//    private val clickedState = MutableList(dataList.size) { false } // 각 아이템의 클릭 상태를 저장
//
//    fun resetClickedState() {
//        clickedState.indices.forEach { clickedState[it] = false }
//        notifyDataSetChanged() // 리스트 갱신
//    }

    // 데이터 설정 함수
    fun setData(comments: List<StudyPostContentCommentDetail>) {
        dataList.clear()
        comments.forEach { comment ->
            dataList.add(CommentItem(comment, false)) // 댓글
            comment.applies.forEach { reply ->
                dataList.add(CommentItem(reply, true)) // 대댓글
            }
        }
        notifyDataSetChanged()
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
        return if (dataList[position].isReply) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position].commentDetail
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
        fun bind(item: StudyPostContentCommentDetail) {
            binding.communityContentCommentWriterTv.text = item.member.name
            binding.communityContentCommentStrTv.text = item.content
            binding.communityContentCommentGoodNumTv.text = item.likeCount.toString()

            if (item.isLiked == "true") {
                binding.communityContentCommentGoodCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentGoodUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentGoodCheckedIv.visibility = View.GONE
                binding.communityContentCommentGoodUncheckedIv.visibility = View.VISIBLE
            }

            if (item.isDeleted) {
                binding.communityContentCommentBadCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentBadUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentBadCheckedIv.visibility = View.GONE
                binding.communityContentCommentBadUncheckedIv.visibility = View.VISIBLE
            }
        }
    }

    inner class ReplyCommentViewHolder(private val binding : ItemContentCommentReplyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyPostContentCommentDetail) {
            binding.communityContentCommentReplyWriterTv.text = item.member.name
            binding.communityContentCommentReplyStrTv.text = item.content
            binding.communityContentCommentReplyGoodNumTv.text = item.likeCount.toString()

            if (item.isLiked == "true") {
                binding.communityContentCommentReplyGoodCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentReplyGoodUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentReplyGoodCheckedIv.visibility = View.GONE
                binding.communityContentCommentReplyGoodUncheckedIv.visibility = View.VISIBLE
            }

            if (item.isDeleted) {
                binding.communityContentCommentReplyBadCheckedIv.visibility = View.VISIBLE
                binding.communityContentCommentReplyBadUncheckedIv.visibility = View.GONE
            } else {
                binding.communityContentCommentReplyBadCheckedIv.visibility = View.GONE
                binding.communityContentCommentReplyBadUncheckedIv.visibility = View.VISIBLE
            }
        }
    }
}
