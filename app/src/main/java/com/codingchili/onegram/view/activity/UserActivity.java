package com.codingchili.onegram.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.codingchili.onegram.R;
import com.codingchili.onegram.model.api.Client;
import com.codingchili.onegram.model.api.ClientProtocol;
import com.codingchili.onegram.view.fragment.GalleryFragment;
import com.codingchili.onegram.view.fragment.SearchFragment;
import com.codingchili.onegram.view.fragment.SettingsFragment;
import com.codingchili.onegram.view.fragment.UploadFragment;
import com.codingchili.onegram.view.helper.CertificateHelper;
import com.codingchili.onegram.view.helper.NetworkStatistics;
import com.codingchili.onegram.view.helper.PreferenceHelper;
import com.codingchili.onegram.view.helper.QueryDialog;

/**
 * @author Robin Duda
 *         <p/>
 *         UserActivity,
 *         Handles fragments that are available
 *         when the user is logged on.
 *         <p/>
 *         The user is returned to the LoginActivity
 *         whenever the token has expired or the user
 *         has selected the menu option to log out.
 */

public class UserActivity extends Activity {
    private ClientProtocol client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getClient();

        if (savedInstanceState == null)
            showGallery();
    }

    /**
     * Recreates the client from the bundle using the passed token.
     */
    private void getClient() {
        client = new Client(getIntent().getExtras().getString("token"),
                CertificateHelper.getCertificate(getApplicationContext()));

        client.setStatisticsHandler(new NetworkStatistics(getApplicationContext()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                menuActionAdd();
                break;
            case R.id.action_search:
                menuActionSearch();
                break;
            case R.id.action_settings:
                menuActionSettings();
                break;
            case R.id.action_about:
                menuActionAbout();
                break;
            case R.id.action_logout:
                menuActionLogout();
                break;
            default:
                break;
        }

        return false;
    }

    private void menuActionAdd() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new UploadFragment().setArguments(client))
                .addToBackStack(null)
                .commit();
    }

    private void menuActionSearch() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SearchFragment().setArguments(client))
                .addToBackStack(null)
                .commit();
    }

    private void showGallery() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .replace(R.id.container, new GalleryFragment().setArguments(client));

        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void menuActionSettings() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment().setArguments())
                .addToBackStack(null)
                .commit();
    }

    private void menuActionAbout() {
        showDialog(R.string.about_title, R.string.about_text);
    }


    private void showDialog(int title, int message) {
        new QueryDialog(this).show(getString(title), getString(message));
    }


    private void menuActionLogout() {
        clearStoredToken();
        finish();
    }

    private void clearStoredToken() {
        PreferenceHelper preferences = new PreferenceHelper(this);
        preferences.setToken(null);
    }

    public void setActionBarTitle(int title) {
        ActionBar actionbar = getActionBar();

        if (actionbar != null) {
            actionbar.setTitle(getString(title));
        }
    }

    /**
     * Exiting from this activity using the back button
     * does not show the LoginActivity, rather closes
     * the application and stores the client token.
     */
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            setResult(LoginActivity.APP_EXIT);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
