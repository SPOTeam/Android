import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment
import com.example.spoteam_android.ReportStudymemberFragment
import com.example.spoteam_android.StudyItem

class StudyAdapter(private val itemList: ArrayList<StudyItem>) :
    RecyclerView.Adapter<StudyAdapter.StudyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val binding = ItemRecyclerViewPlusToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = itemList.size

    inner class StudyViewHolder(val binding: ItemRecyclerViewPlusToggleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StudyItem) {
//            val imageUri = Uri.parse(item.imageUrl)
//            // ImageView에 URI 설정
//            binding.ImageView4.setImageURI(imageUri)
            binding.tvTime.text = item.title
            binding.tvTitle.text = item.introduction
            binding.tvName.text = item.studyTO.toString()
            binding.tvName2.text = item.memberCount.toString()
            binding.tvName3.text = item.heartCount.toString()
            binding.tvName4.text = item.hitNum.toString()
        }
    }

    fun updateList(newList: List<StudyItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}
