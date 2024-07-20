package com.example.spoteam_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BoardAdapter(val itemList: ArrayList<BoardItem>) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_view, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.tv_time.text = itemList[position].studyname
        holder.tv_title.text = itemList[position].studyobject
        holder.tv_name.text = itemList[position].studyto.toString()
        holder.tv_name2.text = itemList[position].studypo.toString()
        holder.tv_name3.text = itemList[position].like.toString()
        holder.tv_name4.text = itemList[position].watch.toString()
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_time = itemView.findViewById<TextView>(R.id.tv_time)
        val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val tv_name2 = itemView.findViewById<TextView>(R.id.tv_name2)
        val tv_name3  = itemView.findViewById<TextView>(R.id.tv_name3)
        val tv_name4 = itemView.findViewById<TextView>(R.id.tv_name4)
    }

}