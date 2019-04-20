package com.codingchili.onegram.view.fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codingchili.onegram.R;
import com.codingchili.onegram.view.helper.GalleryImage;

/**
 * @author Robin Duda
 *
 * Used to display an Image from the Gallery in fullscreen.
 *
 * The user has the option to click on the image to
 * save it to the phones gallery.
 */

public class FullscreenImageFragment extends Fragment {
    private GalleryImage image;

    public FullscreenImageFragment() {
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    public FullscreenImageFragment setArguments(GalleryImage image) {
        this.image = image;
        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideActionBar();
        hideStatusBar();
    }

    private void hideActionBar() {
        ActionBar bar = getActivity().getActionBar();

        if (bar != null) {
            bar.hide();
        }
    }

    private void hideStatusBar() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

   @Override
    public void onPause() {
        showActionBar();
        showStatusBar();

        super.onPause();
    }

    private void showActionBar() {
        ActionBar bar = getActivity().getActionBar();

        if (bar != null) {
            bar.show();
        }
    }

    private void showStatusBar() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        ((ImageView) view.findViewById(R.id.image)).setImageBitmap(image.getBitmap());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.save))
                        .setMessage(getString(R.string.save_to_phone))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MediaStore.Images.Media.insertImage(
                                        getActivity().getContentResolver(),
                                        image.getBitmap(),
                                        getActivity().getString(R.string.app_name) + "-" +
                                            image.getDate().toString(),
                                        image.getDescription());
                                getActivity().getFragmentManager().popBackStackImmediate();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //
                            }
                        })
                        .show();
            }
        });

        return view;
    }
}
