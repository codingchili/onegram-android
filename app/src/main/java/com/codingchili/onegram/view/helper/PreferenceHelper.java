package com.codingchili.onegram.view.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.codingchili.onegram.R;
import com.codingchili.onegram.view.exception.NoTokenStoredException;

/**
 * @author Robin Duda
 *
 * Talks to the SharedPreference,
 * reduces duplicate code, requires UI.
 */

public class PreferenceHelper {
    private SharedPreferences shared;
    private Context context;

    public PreferenceHelper(Context context) {
        this.context = context;

       shared = context.getSharedPreferences(
               context.getString(R.string.shared_preferences),
               Context.MODE_PRIVATE);
    }

    private String getUserKey() {
        return context.getString(R.string.shared_username);
    }

    private String getTokenKey() {
        return context.getString(R.string.shared_token);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(getUserKey(), username);
        editor.apply();
    }

    public String getUsername() {
        return shared.getString(getUserKey(), "");
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(getTokenKey(), token);
        editor.apply();
    }

    public String getToken() throws NoTokenStoredException {
        String token = shared.getString(getTokenKey(), null);

        if (token == null)
            throw new NoTokenStoredException();

        return token;
    }

    public boolean isGalleryThumbnails() {
        return shared.getBoolean(getThumbnailKey(), true);
    }

    public void setGalleryThumbnails(boolean thumbnails) {
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(getThumbnailKey(), thumbnails);
        editor.apply();
    }

    private String getThumbnailKey() {
        return context.getString(R.string.preference_thumbnails);
    }
}
