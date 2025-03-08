package com.example.spoteam_android.ui.study

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R

class WriteContentImageRVadapter(private val images: MutableList<Uri>) :
    RecyclerView.Adapter<WriteContentImageRVadapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_preview_iv)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_image_iv) // 🔥 X 버튼 추가

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
        holder.imageView.setImageURI(images[position])
    }

    override fun getItemCount(): Int = images.size

    fun addImage(imageUri: Uri) {
        images.add(imageUri)
        notifyItemInserted(images.lastIndex) // 🔥 전체 갱신 대신 성능 최적화
    }

    fun removeImage(position: Int) {
        images.removeAt(position)
        notifyItemRemoved(position)
    }
}
