package com.example.spoteam_android.ui.study

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spoteam_android.R

class WriteContentImageRVadapter(private val images: MutableList<Any>) :
    RecyclerView.Adapter<WriteContentImageRVadapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_preview_iv)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_image_iv)

        init {
            deleteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    removeImage(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_recycler, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = images[position]

        when (item) {
            is Uri -> {
                holder.imageView.setImageURI(item) // ✅ 갤러리 이미지
            }
            is String -> {
                Glide.with(holder.itemView.context)
                    .load(item) // ✅ 서버에서 받은 URL
                    .placeholder(R.drawable.spot_logo)
                    .error(R.drawable.spot_logo)
                    .into(holder.imageView)
            }
        }
    }

    override fun getItemCount(): Int = images.size

    fun removeImage(position: Int) {
        images.removeAt(position)
        notifyItemRemoved(position)
    }
}
