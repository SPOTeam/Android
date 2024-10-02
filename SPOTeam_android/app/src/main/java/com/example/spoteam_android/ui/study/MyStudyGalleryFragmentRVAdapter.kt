package com.example.spoteam_android.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.GalleryItems
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemGalleryPictureBinding
import com.example.spoteam_android.ui.community.contentComment.ContentCommentMultiViewRVAdapter.ItemClick

class MyStudyGalleryFragmentRVAdapter(private val dataList: List<GalleryItems>) : RecyclerView.Adapter<MyStudyGalleryFragmentRVAdapter.ViewHolder>() {

    interface ItemClick {
        fun onItemClick(data : GalleryItems)
    }

    var itemClick: ItemClick? = null


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // ItemGalleryPictureBinding을 사용해 뷰 홀더를 생성
        val binding: ItemGalleryPictureBinding = ItemGalleryPictureBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 현재 위치의 데이터를 뷰 홀더에 바인딩
        holder.bind(dataList[position])

        holder.itemView.setOnClickListener{
            itemClick?.onItemClick(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        // 데이터 리스트의 크기를 반환
        return dataList.size
    }

    inner class ViewHolder(private val binding: ItemGalleryPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GalleryItems) {
            // Glide를 사용해 이미지를 로드
            Glide.with(binding.root.context)
                .load(data.imageUrl)  // 이미지 URL // 로딩 중에 표시할 플레이스홀더 이미지
                .error(R.drawable.study_spot_logo) // 오류 시 표시할 이미지
                .into(binding.mypageGalleryPicture) // 이미지를 로드할 ImageView
        }
    }
}
