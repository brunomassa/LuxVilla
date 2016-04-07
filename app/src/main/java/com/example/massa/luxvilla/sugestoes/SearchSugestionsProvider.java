package com.example.massa.luxvilla.sugestoes;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by massa on 07/04/2016.
 */
public class SearchSugestionsProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY="com.example.massa.luxvilla.sugestoes.SearchSugestionsProvider";
    public static final int MODE=DATABASE_MODE_QUERIES;


    public SearchSugestionsProvider(){
        setupSuggestions(AUTHORITY , MODE);
    }
}
