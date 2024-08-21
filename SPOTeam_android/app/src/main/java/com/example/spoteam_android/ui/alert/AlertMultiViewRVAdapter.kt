package com.example.spoteam_android.ui.alert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.databinding.ItemAlertEnrollStudyBinding
import com.example.spoteam_android.databinding.ItemAlertLiveBinding
import com.example.spoteam_android.databinding.ItemAlertUpdateBinding
import com.example.spoteam_android.ui.community.AlertDetail

class AlertMultiViewRVAdapter(private val dataList: List<AlertDetail>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemClick {
//        fun onItemClick(data : AlertDetail)
        fun onStateUpdateClick(data : AlertDetail)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
//            1 -> {
//                val binding = ItemAlertEnrollStudyBinding.inflate(inflater, parent, false)
//                EnrolledViewHolder(binding)
//            }
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
//            is EnrolledViewHolder -> {
//                holder.bind(item)
//                holder.itemView.setOnClickListener {
//                    itemClick?.onItemClick(item)
//                }
//            }
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

//    inner class EnrolledViewHolder(private val binding : ItemAlertEnrollStudyBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: AlertDetail) {
//            // 필요시 데이터를 설정
//        }
//    }

    inner class LiveViewHolder(private val binding : ItemAlertLiveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertDetail) {
            binding.alertLiveContentTv.text = item.studyTitle
        }
    }

    inner class UpdatedViewHolder(private val binding : ItemAlertUpdateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertDetail) {
            when (item.type) {
                "ANNOUNCEMENT" -> {
                    binding.studyName.text = item.studyTitle
                    binding.alertType.text = "'공지'"
                }
                "SCHEDULE_UPDATE " -> {
                    binding.studyName.text = item.studyTitle
                    binding.alertType.text = "새'일정'"

                }
                "TO_DO_UPDATE" -> {
                    binding.studyName.text = "'${item.notifierName}'님의"
//                    binding.alertType.text = item.
                }
            }
        }
    }
}
