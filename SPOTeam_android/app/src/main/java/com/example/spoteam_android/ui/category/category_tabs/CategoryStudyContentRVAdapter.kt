package com.example.spoteam_android.ui.category.category_tabs

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding
import com.example.spoteam_android.ui.community.CategoryStudyDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryStudyContentRVAdapter(private val dataList: List<CategoryStudyDetail>) : RecyclerView.Adapter<CategoryStudyContentRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data : CategoryStudyDetail)
    }

    private lateinit var itemClickListener : OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRecyclerViewBinding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dataList.size) {
            holder.bind(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding : ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CategoryStudyDetail){
            binding.tvTime.text = data.title
            binding.tvTitle.text = data.introduction
            binding.tvName.text = data.maxPeople.toString()
            binding.tvName2.text = data.memberCount.toString()
            binding.tvName3.text = data.heartCount.toString()
            binding.tvName4.text = data.hitNum.toString()
            Glide.with(binding.root.context)
                .load(data.imageUrl)
                .into(binding.ImageView4)
        }
    }
}