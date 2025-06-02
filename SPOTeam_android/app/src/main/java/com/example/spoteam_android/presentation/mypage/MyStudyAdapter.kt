package com.example.spoteam_android.presentation.mypage

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.FinishedStudyItem
import com.example.spoteam_android.databinding.ItemCardMyStudyBinding
import com.example.spoteam_android.databinding.ItemMyStudyBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyStudyAdapter(private val studyList: MutableList<FinishedStudyItem>) :
    RecyclerView.Adapter<MyStudyAdapter.StudyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val binding = ItemCardMyStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        val item = studyList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = studyList.size

    class StudyViewHolder(private val binding: ItemCardMyStudyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FinishedStudyItem) {
            binding.tvTitle.text = item.title
            binding.tvSubtitle.text = item.performance ?: "목표 없음"
            binding.tvStartTime.text = "${formatDate(item.createdAt)}"
            if (item.finishedAt.isNullOrEmpty()) {
                binding.tvEndTime.visibility = View.GONE
            } else {
                binding.tvEndTime.visibility = View.VISIBLE
                binding.tvEndTime.text = "- ${formatDate(item.finishedAt)}"
            }
        }

        private fun formatDate(isoString: String?): String {
            return if (isoString != null) {
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                    val parsed = LocalDateTime.parse(isoString, formatter)
                    parsed.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                } catch (e: Exception) {
                    "-"
                }
            } else {
                "-"
            }
        }
    }

    fun updateList(newList: List<FinishedStudyItem>) {
        if (studyList != newList) {
            studyList.clear()
            studyList.addAll(newList)
            notifyDataSetChanged()
        }
    }
}
