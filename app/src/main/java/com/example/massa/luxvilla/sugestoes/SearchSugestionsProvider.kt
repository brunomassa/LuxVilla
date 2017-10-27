package com.example.massa.luxvilla.sugestoes

import android.content.SearchRecentSuggestionsProvider

/**
 * Created by massa on 27/10/2017.
 */
class SearchSugestionsProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        val AUTHORITY = "com.example.massa.luxvilla.sugestoes.SearchSugestionsProvider"
        val MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES
    }
}
