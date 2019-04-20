package com.codingchili.onegram.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.codingchili.onegram.R;
import com.codingchili.onegram.model.api.Protocol;
import com.codingchili.onegram.view.helper.QueryDialog;
import com.codingchili.onegram.view.exception.NoTokenStoredException;
import com.codingchili.onegram.view.fragment.LoginFragment;
import com.codingchili.onegram.view.helper.PreferenceHelper;

/**
 * @author Robin Duda
 * <p>
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

        Protocol.setHost(getTargetHost());

        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
        startGalleryIfSession();
    }

    private String getTargetHost() {
        return String.format("%s://%s:%s",
                fromConfig(R.string.protocol),
                fromConfig(R.string.hostname),
                fromConfig(R.string.port));
    }

    private String fromConfig(int id) {
        return this.getString(id);
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
