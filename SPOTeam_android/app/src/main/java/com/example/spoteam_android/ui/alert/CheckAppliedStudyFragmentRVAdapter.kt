package com.example.spoteam_android.ui.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.StudyInfo
import com.example.spoteam_android.databinding.ItemAppliedStudyBinding
import com.example.spoteam_android.ui.community.AlertStudyDetail

class CheckAppliedStudyFragmentRVAdapter(private val dataList: List<AlertStudyDetail>) : RecyclerView.Adapter<CheckAppliedStudyFragmentRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onOKClick(data : AlertStudyDetail)
        fun onRefuseClick(data : AlertStudyDetail)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAppliedStudyBinding = ItemAppliedStudyBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])

        holder.binding.studyOkIv.setOnClickListener{
            itemClickListener.onOKClick(dataList[position])
        }

        holder.binding.studyRefuseIv.setOnClickListener{
            itemClickListener.onRefuseClick(dataList[position])
        }
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding : ItemAppliedStudyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AlertStudyDetail){
            binding.studyTitleTv.text = data.studyTitle

            Glide.with(binding.root.context)
                .load(data.studyProfileImage)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지
                .into(binding.icStudyIv)

        }
    }
}