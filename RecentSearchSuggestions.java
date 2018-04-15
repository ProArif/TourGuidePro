package com.arifulislam.tourguidepro.weather;

import android.content.SearchRecentSuggestionsProvider;



public class RecentSearchSuggestions extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.arifulislam.weather.RecentSearchSuggestions";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public RecentSearchSuggestions(){
        setupSuggestions(AUTHORITY, MODE);
    }
}
