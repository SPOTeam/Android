import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spot.android.R
import com.spot.android.ui.community.PostImages

class StudyContentImageRVAdapter(private val images: List<PostImages>) :
    RecyclerView.Adapter<StudyContentImageRVAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_preview_iv)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_image_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_recycler, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position].imageUrl
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .into(holder.imageView)

        holder.deleteButton.visibility = View.GONE
    }

    override fun getItemCount(): Int = images.size
}
