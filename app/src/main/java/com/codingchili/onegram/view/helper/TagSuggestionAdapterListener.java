package com.codingchili.onegram.view.helper;

/**
 * @author Robin Duda
 *
 * Interface for change in the number of suggestions
 * on the TagSuggestionsAdapter.
 */

public interface TagSuggestionAdapterListener {
    public void onSuggestionCountChange(int count);
}
