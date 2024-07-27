package com.example.spoteam_android.ui.bookMark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding

class BookMarkRVAdapter(private val dataList: ArrayList<BoardItem>) : RecyclerView.Adapter<BookMarkRVAdapter.ViewHolder>() {

//    interface OnItemClickListener {
//        fun onItemClick(data : BoardItem)
//    }
//
//    private lateinit var itemClickListener : OnItemClickListener
//
//    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
//        this.itemClickListener = onItemClickListener
//    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRecyclerViewBinding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(val binding : ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BoardItem){
            binding.tvTime.text = data.studyName
            binding.tvTitle.text = data.studyObject
            binding.tvName.text = data.studyTO.toString()
            binding.tvName2.text = data.studyPO.toString()
            binding.tvName3.text = data.like.toString()
            binding.tvName4.text = data.watch.toString()
        }
    }
}