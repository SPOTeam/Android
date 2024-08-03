package com.example.spoteam_android.ui.alert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.databinding.ItemAlertEnrollStudyBinding
import com.example.spoteam_android.databinding.ItemAlertLiveBinding
import com.example.spoteam_android.databinding.ItemAlertUpdateBinding

class AlertMultiViewRVAdapter(private val dataList: ArrayList<AlertInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ENROLL = 0
        const val LIVE = 1
        const val UPDATE = 2
    }

    interface ItemClick {
        fun onItemClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ENROLL -> {
                val binding = ItemAlertEnrollStudyBinding.inflate(inflater, parent, false)
                EnrolledViewHolder(binding)
            }
            LIVE -> {
                val binding = ItemAlertLiveBinding.inflate(inflater, parent, false)
                LiveViewHolder(binding)
            }
            UPDATE -> {
                val binding = ItemAlertUpdateBinding.inflate(inflater, parent, false)
                UpdatedViewHolder(binding)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입")
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        when (holder) {
            is EnrolledViewHolder -> {
                holder.bind(item)
                holder.itemView.setOnClickListener {
                    itemClick?.onItemClick(it, position)
                }
            }
            is LiveViewHolder -> {
                holder.bind(item)
            }
            is UpdatedViewHolder -> {
                holder.bind(item)
            }
        }


    }

    inner class EnrolledViewHolder(private val binding : ItemAlertEnrollStudyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertInfo) {
            // 필요시 데이터를 설정
        }
    }

    inner class LiveViewHolder(private val binding : ItemAlertLiveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertInfo) {
            binding.alertLiveContentTv.text = item.contentText
        }
    }

    inner class UpdatedViewHolder(private val binding : ItemAlertUpdateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlertInfo) {
            binding.alertUpdateContentTv.text = item.contentText
        }
    }
}
