package com.chilimannen.onegram.view.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.chilimannen.onegram.R;
import com.chilimannen.onegram.view.Activity.UserActivity;
import com.chilimannen.onegram.view.Helper.NetworkStatistics;
import com.chilimannen.onegram.view.Helper.PreferenceHelper;

/**
 * @author Robin Duda
 *
 * Fragment used for settings and statistics.
 */

public class SettingsFragment extends Fragment {
    private View view;

    public SettingsFragment() {
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    public SettingsFragment setArguments() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_action_save:
                getFragmentManager().popBackStackImmediate();
                return true;
            case R.id.fragment_action_reset:
                clearStatisticsDialog();
            default:
                return false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ((UserActivity) getActivity()).setActionBarTitle(R.string.action_settings);

        ((ImageView) view.findViewById(R.id.app_logo)).getDrawable().setColorFilter(
                getContext().getColor(R.color.solid),
                PorterDuff.Mode.SRC_IN);

        ((Switch) view.findViewById(R.id.gallery_mode)).setChecked(
                new PreferenceHelper(getActivity())
                .isGalleryThumbnails());

        ((Switch) view.findViewById(R.id.gallery_mode)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enabled) {
                new PreferenceHelper(getActivity()).setGalleryThumbnails(enabled);
            }
        });

        loadStats();
        return view;
    }

    private void clearStatisticsDialog() {
        new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new NetworkStatistics(getActivity()).resetStatistics();
                        loadStats();
                    }
                })
                .setIcon(R.drawable.baseline_warning_black)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.reset_stats_dialog))
                .show();
        }

    private void loadStats() {
        NetworkStatistics stats = new NetworkStatistics(getActivity());
        ((TextView) view.findViewById(R.id.network_upload)).setText(stats.getUploaded());
        ((TextView) view.findViewById(R.id.network_download)).setText(stats.getDownloaded());
        ((TextView) view.findViewById(R.id.network_ping)).setText(stats.getPingAverage());
    }
}
