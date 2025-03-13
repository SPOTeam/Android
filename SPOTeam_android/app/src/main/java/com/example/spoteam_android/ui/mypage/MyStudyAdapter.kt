package com.example.spoteam_android.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.databinding.ItemMyStudyBinding

class MyStudyAdapter(private val studyList: MutableList<StudyItem>) :
    RecyclerView.Adapter<MyStudyAdapter.StudyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val binding = ItemMyStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        val item = studyList[position]
        holder.bind(item)
    }



    override fun getItemCount(): Int = studyList.size

    class StudyViewHolder(private val binding: ItemMyStudyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyItem) {
            binding.tvTitle.text = item.title
            binding.tvSubtitle.text = item.goal
        }


    }

    fun updateList(newList: List<StudyItem>) {
        if (studyList != newList) { // 데이터가 변경된 경우에만 업데이트
            studyList.clear()
            studyList.addAll(newList)
            notifyDataSetChanged() // RecyclerView UI 갱신
        }
    }

    /** 🔹 UI 업데이트 기능 추가 */

}
