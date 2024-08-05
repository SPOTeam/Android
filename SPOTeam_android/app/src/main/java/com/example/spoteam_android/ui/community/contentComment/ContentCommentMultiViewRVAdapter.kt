package com.example.spoteam_android.ui.community.contentComment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.CommentInfo
import com.example.spoteam_android.databinding.ItemAlertEnrollStudyBinding
import com.example.spoteam_android.databinding.ItemAlertLiveBinding
import com.example.spoteam_android.databinding.ItemAlertUpdateBinding
import com.example.spoteam_android.databinding.ItemContentCommentBinding
import com.example.spoteam_android.databinding.ItemContentCommentReplyBinding

class ContentCommentMultiViewRVAdapter(private val dataList: ArrayList<CommentInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val COMMENT = 0
        const val COMMENTREPLY = 1
    }

    interface ItemClick {
        fun onItemClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            COMMENT -> {
                val binding = ItemContentCommentBinding.inflate(inflater, parent, false)
                CommentViewHolder(binding)
            }
            COMMENTREPLY -> {
                val binding = ItemContentCommentReplyBinding.inflate(inflater, parent, false)
                ReplyCommentViewHolder(binding)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입")
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return dataList[position].commentType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
    }

    inner class CommentViewHolder(private val binding : ItemContentCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentInfo) {
            binding.communityContentCommentWriterTv.text = item.commentWriter
            binding.communityContentCommentStrTv.text = item.Comment
        }
    }

    inner class ReplyCommentViewHolder(private val binding : ItemContentCommentReplyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentInfo) {
            binding.communityContentCommentReplyWriterTv.text = item.commentWriter
            binding.communityContentCommentReplyStrTv.text = item.Comment
        }
    }
}
