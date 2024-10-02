package com.example.spoteam_android.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchQueryDao {

    // 검색어 추가
    @Insert
    fun insertSearchQuery(searchQuery: SearchQuery)

    // 저장된 모든 검색어 불러오기
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getAllSearchQueries(): List<SearchQuery>
}
