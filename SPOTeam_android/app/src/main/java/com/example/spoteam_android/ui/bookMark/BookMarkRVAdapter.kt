//package com.example.spoteam_android.ui.bookMark
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.spoteam_android.R
//import com.example.spoteam_android.StudyItem
//import com.example.spoteam_android.databinding.ItemRecyclerViewBinding
//
//class BookMarkRVAdapter(
//    private val itemList: ArrayList<StudyItem>,
//    private val onItemClick: (StudyItem) -> Unit,
//    private val onLikeClick: (StudyItem, ImageView) -> Unit
//) : RecyclerView.Adapter<BookMarkRVAdapter.BookmarkViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
//        val binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return BookmarkViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
//        val currentItem = itemList[position]
//        holder.bind(currentItem)
//
//        holder.itemView.setOnClickListener {
//            onItemClick(currentItem)
//        }
//
//        holder.likeButton.setOnClickListener {
//            onLikeClick(currentItem, holder.likeButton)
//        }
//    }
//
//    override fun getItemCount(): Int = itemList.size
//
//    inner class BookmarkViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
//        val likeButton: ImageView = binding.heartCountIv
//
//
//        fun bind(item: StudyItem) {
//            binding.tvTime.text = item.title
//            binding.tvTitle.text = item.goal
//            binding.tvName.text = item.maxPeople.toString()
//            binding.tvName2.text = item.memberCount.toString()
//            binding.tvName3.text = item.heartCount.toString()
//            binding.tvName4.text = item.hitNum.toString()
//
//            // Glide를 사용하여 imageUrl을 ImageView에 로드
//            Glide.with(binding.root.context)
//                .load(item.imageUrl)
//                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
//                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
//                .into(binding.ImageView4)
//
//            // 하트 아이콘 상태 설정
//            val heartIcon = if (item.liked) R.drawable.ic_heart_filled else R.drawable.study_like
//            binding.heartCountIv.setImageResource(heartIcon)
//
//    inner class ViewHolder(val binding : ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(data: BoardItem){
//            binding.tvTime.text = data.title
//            binding.tvTitle.text = data.goal
//            binding.tvName.text = data.maxPeople.toString()
//            binding.tvName2.text = data.memberCount.toString()
//            binding.tvName3.text = data.heartCount.toString()
//            binding.tvName4.text = data.hitNum.toString()
//        }
//    }
//
//    fun updateList(newList: List<StudyItem>) {
//        Log.d("BookMarkRVAdapter", "updateList called with ${newList.size} items")
//        itemList.clear()
//        itemList.addAll(newList)
//        Log.d("BookMarkRVAdapter", "itemList size after update: ${itemList.size}")
//        notifyDataSetChanged()
//    }
//
//}
