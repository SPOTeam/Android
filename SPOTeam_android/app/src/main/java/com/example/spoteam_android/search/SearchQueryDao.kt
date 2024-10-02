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
    @Query("SELECT * FROM search_history ORDER BY timestamp ASC")
    fun getAllSearchQueries(): List<SearchQuery>

    @Query("DELETE FROM search_history")
    fun deleteAllSearchQueries()

    // 가장 오래된 검색어 삭제
    @Query("DELETE FROM search_history WHERE id = (SELECT id FROM search_history ORDER BY timestamp ASC LIMIT 1)")
    fun deleteOldestSearchQuery()

    // 검색어 개수 확인
    @Query("SELECT COUNT(*) FROM search_history")
    fun getSearchQueryCount(): Int
}
