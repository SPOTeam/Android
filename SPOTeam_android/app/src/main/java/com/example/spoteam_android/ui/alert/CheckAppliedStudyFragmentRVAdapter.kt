package com.example.spoteam_android.ui.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.StudyInfo
import com.example.spoteam_android.databinding.ItemAppliedStudyBinding
import com.example.spoteam_android.ui.community.AlertStudyDetail

class CheckAppliedStudyFragmentRVAdapter(private val dataList: List<AlertStudyDetail>, ) : RecyclerView.Adapter<CheckAppliedStudyFragmentRVAdapter.ViewHolder>() {

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
        }
    }
}