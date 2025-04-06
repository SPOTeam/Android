package com.example.spoteam_android.search

import androidx.room.*

@Dao
interface SearchQueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(searchQuery: SearchQuery)

    @Query("SELECT * FROM search_queries ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getRecentQueries(limit: Int): List<SearchQuery>

    @Query("DELETE FROM search_queries WHERE `query` = :query")
    suspend fun deleteQuery(query: String)

    @Query("DELETE FROM search_queries WHERE id NOT IN (SELECT id FROM search_queries ORDER BY timestamp DESC LIMIT :limit)")
    suspend fun deleteOldQueries(limit: Int)
}
