package com.example.spoteam_android.presentation.bookMark

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding
import com.example.spoteam_android.domain.study.entity.StudyDataResponse

class BookMarkRVAdapter(
    private var bookmarkitemList: ArrayList<StudyDataResponse.StudyContent>,
    private val onItemClick: (StudyDataResponse.StudyContent) -> Unit,
    private val onLikeClick: (StudyDataResponse.StudyContent, ImageView) -> Unit
) : RecyclerView.Adapter<BookMarkRVAdapter.BookmarkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val currentItem = bookmarkitemList[position]
        holder.bind(currentItem)

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }

        holder.likeButton.setOnClickListener {
            onLikeClick(currentItem, holder.likeButton)
        }
    }

    override fun getItemCount(): Int {
        return bookmarkitemList.size
    }


    inner class BookmarkViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val likeButton: ImageView = binding.heartCountIv

        fun bind(item: StudyDataResponse.StudyContent) {
            binding.tvTime.text = item.title
            binding.tvTitle.text = item.goal
            binding.tvName.text = item.maxPeople.toString()
            binding.tvName2.text = item.memberCount.toString()
            binding.tvName3.text = item.heartCount.toString()
            binding.tvName4.text = item.hitNum.toString()

            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.ImageView4)

            val heartIcon = if (item.liked) R.drawable.ic_heart_filled else R.drawable.study_like
            binding.heartCountIv.setImageResource(heartIcon)
        }
    }

    fun updateList(newList: List<StudyDataResponse.StudyContent>) {

        bookmarkitemList = ArrayList()
        bookmarkitemList.addAll(newList)

        notifyDataSetChanged()
    }




}
