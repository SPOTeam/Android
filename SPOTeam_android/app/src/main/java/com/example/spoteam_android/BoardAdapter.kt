package com.example.spoteam_android

import StudyAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment

class BoardAdapter(private val itemList: ArrayList<BoardItem>) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemRecyclerViewPlusToggleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)


        holder.itemView.setOnClickListener {
            holder.itemView.setOnClickListener {
                // Toast 메시지 대신 DetailStudyFragment로 전환
                val fragment = DetailStudyFragment()

                //스터디 ID bundle형태로 DetailStudyFragment 전달
                val bundle = Bundle()
                bundle.putString("studyId", currentItem.studyId.toString())
                Log.d("StudyId",currentItem.studyId.toString())
                fragment.arguments = bundle

                val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.main_frm, fragment)
                fragmentTransaction.addToBackStack(null)  // 백스택에 추가하여 뒤로가기 버튼을 사용 가능하게 함
                fragmentTransaction.commit()
            }
        }
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < itemList.size) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
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
            val exit = ExitStudyPopupFragment(view.context,this@BoardAdapter, adapterPosition)
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
