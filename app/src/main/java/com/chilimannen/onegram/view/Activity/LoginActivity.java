package com.chilimannen.onegram.view.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.chilimannen.onegram.R;
import com.chilimannen.onegram.view.Helper.QueryDialog;
import com.chilimannen.onegram.view.Exception.NoTokenStoredException;
import com.chilimannen.onegram.view.Fragment.LoginFragment;
import com.chilimannen.onegram.view.Helper.PreferenceHelper;

/**
 * @author Robin Duda
 *
 * Login Activity,
 * Handles the menu and fragments that are
 * available before the user has logged on.
 */

public class LoginActivity extends Activity {
    public static final int APP_EXIT = 1;
    public static final int APP_SESSION_TIMEOUT = 2;
    public static final int APP_CONNECTION_ERROR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
        startGalleryIfSession();
    }

    private void startGalleryIfSession() {
        PreferenceHelper preferences = new PreferenceHelper(this);
        try {
            String token = preferences.getToken();
            userLoggedOn(token);
        } catch (NoTokenStoredException ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case APP_EXIT:
                finish();
                break;
            case APP_SESSION_TIMEOUT:
                clearToken();
                showTokenExpired();
                break;
            case APP_CONNECTION_ERROR:
                clearToken();
                showConnectionError();
                break;
        }
    }

    private void showTokenExpired() {
        showDialog(R.string.login, R.string.token_expiry);
    }

    private void showConnectionError() {
        showDialog(R.string.app_name, R.string.connection_error);
    }

    private void showDialog(int title, int message) {
        new QueryDialog(this).show(getString(title), getString(message));
    }

    private void clearToken() {
        PreferenceHelper preference = new PreferenceHelper(this);
        preference.setToken(null);
    }

    private void storeToken(String token) {
        PreferenceHelper preference = new PreferenceHelper(this);
        preference.setToken(token);
    }

    public void userLoggedOn(String token) {
        storeToken(token);
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("token", token);
        startActivityForResult(intent, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public void setActionBarTitle(int title) {
        ActionBar actionbar = getActionBar();

        if (actionbar != null) {
            actionbar.setTitle(getString(title));
        }
    }
}