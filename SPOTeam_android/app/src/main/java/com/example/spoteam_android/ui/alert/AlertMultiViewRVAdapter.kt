package com.example.spoteam_android.ui.alert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_LIVE = 1
        private const val VIEW_TYPE_UPDATE = 2
    }

    var headerClickListener: (() -> Unit)? = null

    var studyAlertEnabled: Boolean = false

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.alert_item_header, parent, false)
                HeaderViewHolder(view)
            }
            VIEW_TYPE_LIVE -> {
                val binding = ItemAlertLiveBinding.inflate(inflater, parent, false)
                LiveViewHolder(binding)
            }
            VIEW_TYPE_UPDATE -> {
                val binding = ItemAlertUpdateBinding.inflate(inflater, parent, false)
                UpdatedViewHolder(binding)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입")
        }
    }


    override fun getItemCount(): Int = dataList.size+1

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> VIEW_TYPE_HEADER
            dataList[position - 1].type == "POPULAR_POST" -> VIEW_TYPE_LIVE
            else -> VIEW_TYPE_UPDATE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind()
            return
        }

        val actualPosition = position - 1
        val item = dataList[actualPosition]

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

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val container = view

        init {
            container.setOnClickListener {
                if (studyAlertEnabled) {
                    headerClickListener?.invoke()
                }
            }
        }

        fun bind() {
            // 클릭 가능 여부에 따라 뷰 상태 변경
            container.isEnabled = studyAlertEnabled
            container.alpha = if (studyAlertEnabled) 1.0f else 0.5f // 비활성화 시 투명도 적용 등
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
