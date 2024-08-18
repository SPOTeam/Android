import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment
import com.example.spoteam_android.ReportStudymemberFragment
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.databinding.ItemRecyclerViewBinding

import com.bumptech.glide.Glide

class StudyAdapter(
    private val itemList: ArrayList<StudyItem>,
    private val onItemClick: (StudyItem) -> Unit
) : RecyclerView.Adapter<StudyAdapter.StudyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class StudyViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StudyItem) {
            binding.tvTime.text = item.title
            binding.tvTitle.text = item.goal
            binding.tvName.text = item.maxPeople.toString()
            binding.tvName2.text = item.memberCount.toString()
            binding.tvName3.text = item.heartCount.toString()
            binding.tvName4.text = item.hitNum.toString()

            // Glide를 사용하여 imageUrl을 ImageView에 로드
            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .into(binding.ImageView4) // ImageView4에 이미지를 로드
        }
    }

    fun updateList(newList: List<StudyItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}

