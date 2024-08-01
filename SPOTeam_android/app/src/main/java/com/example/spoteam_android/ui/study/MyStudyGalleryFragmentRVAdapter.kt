package com.example.spoteam_android.ui.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.GalleryItem
import com.example.spoteam_android.databinding.ItemGalleryPictureBinding
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding

class MyStudyGalleryFragmentRVAdapter(private val dataList: ArrayList<GalleryItem>) : RecyclerView.Adapter<MyStudyGalleryFragmentRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemGalleryPictureBinding = ItemGalleryPictureBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding : ItemGalleryPictureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GalleryItem){
            binding.mypageGalleryPicture.setImageResource(data.imgId)
        }
    }
}