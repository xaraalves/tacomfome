package ufscar.tacomfome.tacomfome.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Gabriel on 18/11/2017.
 */

public class SearchableProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "ufscar.tacomfome.tacomfome.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider(){
        setupSuggestions( AUTHORITY, MODE );
    }
}
