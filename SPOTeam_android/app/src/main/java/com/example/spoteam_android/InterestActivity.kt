package com.example.spoteam_android

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InterestActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        val img : ImageView = findViewById(R.id.ImageView2)

        img.setOnClickListener({
            val intent = Intent(this, InterestFilterActivity::class.java)
            startActivity(intent)
        })

        val rv_board = findViewById<RecyclerView>(R.id.rv_board)


        val itemList = ArrayList<BoardItem>()

        itemList.add(BoardItem("피아노 스터디","스터디 목표",10,1,1,600))
        itemList.add(BoardItem("태권도 스터디","스터디 목표",10,2,1,500))
        itemList.add(BoardItem("보컬 스터디","스터디 목표",10,3,1,400))
        itemList.add(BoardItem("기타 스터디","스터디 목표",10,4,1,300))
        itemList.add(BoardItem("롤 스터디","스터디 목표",10,5,5,200))
        itemList.add(BoardItem("안드로이드 스터디","스터디 목표",10,5,200,200))
        itemList.add(BoardItem("ios 스터디","스터디 목표",10,7,1,200))
        itemList.add(BoardItem("Server 스터디","스터디 목표",10,8,1,200))
        itemList.add(BoardItem("Kotlin 스터디","스터디 목표",10,5,1,200))
        itemList.add(BoardItem("Java 스터디","스터디 목표",10,5,1,200))


        val boardAdapter = BoardAdapter(itemList)
        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }
}