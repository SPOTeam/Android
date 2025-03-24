package com.example.spoteam_android.ui.alert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemAlertEnrollStudyBinding
import com.example.spoteam_android.databinding.ItemAlertLiveBinding
import com.example.spoteam_android.databinding.ItemAlertUpdateBinding
import com.example.spoteam_android.ui.community.AlertDetail

class AlertMultiViewRVAdapter(private val dataList: List<AlertDetail>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClick {
        fun onStateUpdateClick(data : AlertDetail)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            1 -> {
                val binding = ItemAlertLiveBinding.inflate(inflater, parent, false)
                LiveViewHolder(binding)
            }
            2 -> {
                val binding = ItemAlertUpdateBinding.inflate(inflater, parent, false)
                UpdatedViewHolder(binding)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입")
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position].type) {
            "POPULAR_POST" -> 1
            "ANNOUNCEMENT" -> 2
            "SCHEDULE_UPDATE" -> 2
            "TO_DO_UPDATE" -> 2
            else -> -1  // 예외 처리, 정의되지 않은 타입에 대해 기본 값을 반환
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        when (holder) {
            is LiveViewHolder -> {
                holder.bind(item)
                holder.itemView.setOnClickListener {
                    itemClick?.onStateUpdateClick(item)
                }
            }
            is UpdatedViewHolder -> {
                holder.bind(item)
                holder.itemView.setOnClickListener {
                    itemClick?.onStateUpdateClick(item)
                }
            }
        }


    }


    inner class LiveViewHolder(private val binding : ItemAlertLiveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertDetail) {
            if(item.isChecked) {
                binding.root.isEnabled = false
                binding.icNewAlertLive.visibility = View.GONE
            } else {
                binding.root.isEnabled = true
                binding.icNewAlertLive.visibility = View.VISIBLE
            }
            binding.alertLiveContentTv.text = item.studyTitle
        }
    }

    inner class UpdatedViewHolder(private val binding : ItemAlertUpdateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertDetail) {

            Glide.with(binding.root.context)
                .load(item.studyProfileImage)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지
                .into(binding.alertBellIv)


            if(item.isChecked) {
                binding.root.isEnabled = false
                binding.icNewAlertUpdate.visibility = View.GONE
            } else {
                binding.root.isEnabled = true
                binding.icNewAlertUpdate.visibility = View.VISIBLE
            }
            when (item.type) {
                "ANNOUNCEMENT" -> {
//                    binding.studyName.text = item.studyTitle
//                    binding.categoryType.text = "'공지'"
//                    binding.alertType.text = "업데이트"

                    binding.summaryCombinedTv.text = "내 스터디 '공지' 업데이트"
                    binding.alertCombinedContentTv.text = "${item.studyTitle}의 새로운 공지"
                    binding.alertSoundIc.visibility = View.VISIBLE
                    binding.alertCheckIc.visibility = View.GONE
                }
                "SCHEDULE_UPDATE" -> {
//                    binding.studyName.text = item.studyTitle
//                    binding.categoryType.text = "새 '일정'"
//                    binding.alertType.text = "등록"
                    
                    binding.summaryCombinedTv.text = "내 스터디 '새 일정' 등록"
                    binding.alertCombinedContentTv.text = "${item.studyTitle}의 새로운 일정"
                    binding.alertSoundIc.visibility = View.VISIBLE
                    binding.alertCheckIc.visibility = View.GONE
                }
                "TO_DO_UPDATE" -> {
//                    binding.studyName.text = "'${item.notifierName}' 님의"
//                    binding.categoryType.text = "'${item.studyTitle}'"
//                    binding.alertType.text = "완료!"

                    binding.summaryCombinedTv.text = "'${item.notifierName}'님의 '${item.studyTitle}' 할일 완료!"
                    binding.alertCombinedContentTv.text = "${item.studyTitle}의  ${item.notifierName}님"
                    binding.alertSoundIc.visibility = View.GONE
                    binding.alertCheckIc.visibility = View.VISIBLE
                }
            }

        }
    }
}
