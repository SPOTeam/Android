package com.example.spoteam_android.ui.category.category_tabs

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding
import com.example.spoteam_android.ui.community.CategoryStudyDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryStudyContentRVAdapter(
    var dataList: ArrayList<CategoryStudyDetail>, // ArrayList로 명시적으로 선언
    private val onLikeClick: (CategoryStudyDetail, ImageView) -> Unit
) : RecyclerView.Adapter<CategoryStudyContentRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: CategoryStudyDetail)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    fun addData(newData: List<CategoryStudyDetail>) {
        dataList.addAll(newData)
        notifyDataSetChanged() // 어댑터에 데이터가 변경되었음을 알림
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRecyclerViewBinding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dataList.size) {
            val currentItem = dataList[position]
            holder.bind(currentItem)

            // 항목 클릭 이벤트 설정
            holder.itemView.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    itemClickListener.onItemClick(currentItem)
                }
            }

            // 좋아요 클릭 이벤트 설정
            holder.likeButton.setOnClickListener {
                onLikeClick(currentItem, holder.likeButton)
            }
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val likeButton: ImageView = binding.heartCountIv // 찜 버튼

        fun bind(data: CategoryStudyDetail) {
            binding.tvTime.text = data.title
            binding.tvTitle.text = data.introduction
            binding.tvName.text = data.maxPeople.toString()
            binding.tvName2.text = data.memberCount.toString()
            binding.tvName3.text = data.heartCount.toString()
            binding.tvName4.text = data.hitNum.toString()

            // Glide를 사용하여 imageUrl을 ImageView에 로드
            Glide.with(binding.root.context)
                .load(data.imageUrl)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.ImageView4) // ImageView4에 이미지를 로드

            // 좋아요 상태에 따른 아이콘 설정
            val heartIcon = if (data.liked) R.drawable.ic_heart_filled else R.drawable.study_like
            binding.heartCountIv.setImageResource(heartIcon)
        }
    }

    fun updateList(newList: List<CategoryStudyDetail>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }
}