package com.example.spoteam_android.search

import android.content.SearchRecentSuggestionsProvider

class MySuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.example.MySuggestionProvider" // 고유한 권한 이름
        const val MODE: Int = DATABASE_MODE_QUERIES // 데이터베이스 모드 (2줄 추가 가능)
    }
}
