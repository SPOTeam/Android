package com.example.spoteam_android.ui.mypage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemCommunityContentBinding
import com.example.spoteam_android.databinding.ItemConsiderAttendanceBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import com.example.spoteam_android.ui.community.MyRecruitingStudyDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConsiderAttendanceContentRVAdapter(private var dataList: List<MyRecruitingStudyDetail>) : RecyclerView.Adapter<ConsiderAttendanceContentRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: MyRecruitingStudyDetail)
//        fun onLikeClick(data: CategoryPagesDetail)
//        fun onUnLikeClick(data: CategoryPagesDetail)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemConsiderAttendanceBinding = ItemConsiderAttendanceBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dataList.size) {
            holder.bind(dataList[position])
        }

        holder.binding.considerAttendanceBt.setOnClickListener {
            itemClickListener.onItemClick(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding: ItemConsiderAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MyRecruitingStudyDetail) {
            binding.tvTime.text = data.title
            binding.tvTitle.text = data.goal
            binding.tvName.text = data.maxPeople.toString()
            binding.tvName2.text = data.memberCount.toString()
            binding.tvName3.text = data.heartCount.toString()
            binding.tvName4.text = data.hitNum.toString()
            if (data.liked) {
                binding.icHeartRed.visibility = View.VISIBLE
                binding.ImageView6.visibility = View.GONE
            } else {
                binding.icHeartRed.visibility = View.GONE
                binding.ImageView6.visibility = View.VISIBLE
            }
        }
    }
}
