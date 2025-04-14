package com.example.spoteam_android.ui.alert

import StudyViewModel
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemAlertEnrollStudyBinding
import com.example.spoteam_android.databinding.ItemAlertLiveBinding
import com.example.spoteam_android.databinding.ItemAlertUpdateBinding
import com.example.spoteam_android.ui.community.AlertDetail
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.google.android.material.card.MaterialCardView

class AlertMultiViewRVAdapter(
    private val dataList: List<AlertDetail>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    interface ItemClick {
        fun onStateUpdateClick(data : AlertDetail)
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_LIVE = 1
        private const val VIEW_TYPE_UPDATE = 2
    }

    var isExistAlert : Boolean = false

    var headerClickListener: (() -> Unit)? = null
    var onNavigateToDetail: (() -> Unit)? = null

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


    override fun getItemCount(): Int {
        return dataList.size + 1 // 항상 헤더 포함
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER
        else {
            val item = dataList[position - 1]
            if (item.type == "POPULAR_POST") VIEW_TYPE_LIVE
            else VIEW_TYPE_UPDATE
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as HeaderViewHolder).bind()
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
        private val cardView: MaterialCardView = view.findViewById(R.id.alert_header_card)

        init {
            cardView.setOnClickListener {
                headerClickListener?.invoke()
            }
        }

        fun bind() {
            cardView.isVisible = true
            cardView.isEnabled = true
            cardView.alpha = 1.0f

            val color = if (isExistAlert) {
                ContextCompat.getColor(cardView.context, R.color.transparent_blue) // 파란색
            } else {
                ContextCompat.getColor(cardView.context, R.color.white)   // 흰색
            }

            cardView.setCardBackgroundColor(color)
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
