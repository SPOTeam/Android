package com.example.spoteam_android.ui.study

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemCommunityContentBinding
import com.example.spoteam_android.databinding.ItemMystudyCommunityContentBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import com.example.spoteam_android.ui.community.PostDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyStudyPostRVAdapter(private var dataList: List<PostDetail>) : RecyclerView.Adapter<MyStudyPostRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: PostDetail)
//        fun onLikeClick(data: CategoryPagesDetail)
//        fun onUnLikeClick(data: CategoryPagesDetail)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMystudyCommunityContentBinding = ItemMystudyCommunityContentBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dataList.size) {
            holder.bind(dataList[position])
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding: ItemMystudyCommunityContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PostDetail) {
            binding.contentTitleTv.text = data.title
            binding.contentSummaryTv.text = data.content
            binding.contentLikeNumTv.text = data.likeNum.toString()
            binding.contentCommentNumTv.text = data.commentNum.toString()
            binding.contentViewNumTv.text = data.hitNum.toString()
            binding.contentDateTv.text = formatWrittenTime(data.createdAt)
            binding.categoryTv.text = categoryFormat(data.theme)
            if (data.isLiked) {
                binding.contentLikeNumCheckedIv.visibility = View.VISIBLE
                binding.contentLikeNumUncheckedIv.visibility = View.GONE
            } else {
                binding.contentLikeNumCheckedIv.visibility = View.GONE
                binding.contentLikeNumUncheckedIv.visibility = View.VISIBLE
            }
        }

        private fun categoryFormat(theme: String): String {
            return when (theme) {
                "ANNOUNCEMENT" -> "공지"
                "WELCOME" -> "가입인사"
                "INFO_SHARING" -> "정보공유"
                "FREE_TALK" -> "자유"
                "STUDY_REVIEW" -> "스터디후기"
                "QNA" -> "Q&A"
                else -> ""
            }
        }

        private fun formatWrittenTime(writtenTime: String): String {
            val formats = arrayOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            )
            var date: Date? = null
            for (format in formats) {
                try {
                    date = format.parse(writtenTime)
                    break
                } catch (e: ParseException) {
                    Log.d("DateParseException", "${e.message}")
                }
            }
            return if (date != null) {
                SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
            } else {
                writtenTime // 원본 문자열 반환
            }
        }
    }
}
