package com.example.spoteam_android

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment
import com.example.spoteam_android.ui.mypage.ParticipatingStudyFragment

class BoardAdapter(private val itemList: ArrayList<BoardItem>) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemRecyclerViewPlusToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class BoardViewHolder(val binding: ItemRecyclerViewPlusToggleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BoardItem) {
            binding.tvTime.text = item.studyName
            binding.tvTitle.text = item.studyObject
            binding.tvName.text = item.studyTO.toString()
            binding.tvName2.text = item.studyPO.toString()
            binding.tvName3.text = item.like.toString()
            binding.tvName4.text = item.watch.toString()

            binding.toggle.setOnClickListener {
                showPopupMenu(it)
            }
        }

        private fun showPopupMenu(view: View) {
            val popupMenu = PopupMenu(view.context, view)
            val inflater: MenuInflater = popupMenu.menuInflater
            val exit = ExitStudyPopupFragment(view.context)
            val report = ReportStudymemberFragment(view.context)
            inflater.inflate(R.menu.menu_item_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.exit_study -> {
                        exit.start()
                        true
                    }
                    R.id.report_study -> {
                        report.start()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    fun filterList(filteredList: ArrayList<BoardItem>) {
        itemList.clear()
        itemList.addAll(filteredList)
        notifyDataSetChanged()
    }

}
