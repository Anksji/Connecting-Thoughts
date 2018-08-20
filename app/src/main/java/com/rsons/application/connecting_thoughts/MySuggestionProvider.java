package com.rsons.application.connecting_thoughts;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by ankit on 2/12/2018.
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.rsons.application.connecting_thoughts";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}