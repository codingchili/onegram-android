package com.codingchili.onegram.view.fragment;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.codingchili.onegram.R;
import com.codingchili.onegram.model.api.ClientProtocol;
import com.codingchili.onegram.model.api.exception.QueryConnectionException;
import com.codingchili.onegram.model.api.exception.QueryException;
import com.codingchili.onegram.model.api.exception.QueryUnauthenticatedException;
import com.codingchili.onegram.model.api.Response;
import com.codingchili.onegram.view.activity.LoginActivity;
import com.codingchili.onegram.view.activity.UserActivity;
import com.codingchili.onegram.view.helper.AdapterChangeListener;
import com.codingchili.onegram.view.helper.GalleryAdapter;
import com.codingchili.onegram.view.helper.GalleryAdapterList;
import com.codingchili.onegram.view.helper.GalleryAdapterThumbnails;
import com.codingchili.onegram.view.helper.GalleryImage;
import com.codingchili.onegram.view.helper.PreferenceHelper;
import com.codingchili.onegram.view.helper.QueryDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * @author Robin Duda
 *
 * Shows the users gallery in the Fragment.
 *
 * The gallery supports both list and grid mode
 * which may be changed in the settings.
 */

public class GalleryFragment extends ListFragment implements AdapterChangeListener {
    private ClientProtocol client;
    private GalleryAdapter adapter;
    private DownloadGalleryTask galleryTask;
    private boolean thumbnails;
    private boolean loading = true;
    private int galleryHash;


    public GalleryFragment() {
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<GalleryImage> adapterContent = new ArrayList<>();
        thumbnails = new PreferenceHelper(getActivity()).isGalleryThumbnails();

        if (adapter != null)
            adapterContent = adapter.getAll();

        if (thumbnails) {
            adapter = new GalleryAdapterThumbnails(getActivity());
            ((GridView) getActivity().findViewById(R.id.gallery_grid)).setAdapter(adapter);
        } else {
            adapter = new GalleryAdapterList(getActivity());
            setListAdapter(adapter);
        }

        setupAdapter(adapterContent);
        loadGallery();
        enableMode();
    }

    private void setupAdapter(ArrayList<GalleryImage> images) {
        adapter.addAll(images);
        adapter.setClient(client);
        adapter.setListener(this);
    }

    private void enableMode() {
        if (thumbnails)
            enableGridMode();
        else
            enableListMode();
    }

    private void enableGridMode() {
        getActivity().findViewById(R.id.gallery_grid).setVisibility(View.VISIBLE);
        getListView().setVisibility(View.GONE);
    }

    private void enableListMode() {
        getListView().setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.gallery_grid).setVisibility(View.GONE);
    }


    @Override
    public void onPause() {
        if (galleryTask != null
                && galleryTask.getStatus() == AsyncTask.Status.RUNNING)
            galleryTask.cancel(true);

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_gallery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public GalleryFragment setArguments(ClientProtocol client) {
        this.client = client;
        return this;
    }

    private void loadGallery() {
        if (galleryTask == null
                || (galleryTask.getStatus() == AsyncTask.Status.FINISHED
                || galleryTask.isCancelled())) {
            galleryTask = (DownloadGalleryTask) new DownloadGalleryTask().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        view.findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchFragment();
            }
        });

        view.findViewById(R.id.button_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadFragment();
            }
        });

        ((UserActivity) getActivity()).setActionBarTitle(R.string.action_gallery);

        return view;
    }

    private void showSearchFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SearchFragment().setArguments(client))
                .addToBackStack(null)
                .commit();
    }

    private void showUploadFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new UploadFragment().setArguments(client))
                .addToBackStack(null)
                .commit();
    }

    /**
     * Called whenever the size of the adapter changes or
     * the adapter is notified of a change in data.
     */
    @Override
    public void onAdapterSize(int size) {

        if (loading) {
            hideHelp();
        } else if (size == 0) {
            showHelp();

            hideList();
            hideGrid();
        } else {
            hideHelp();

            if (thumbnails) {
                hideList();
                showGrid();
            } else {
                hideGrid();
                showList();
            }
        }
    }

    private void hideHelp() {
        getActivity().findViewById(R.id.help_frame).setVisibility(View.GONE);
    }

    private void showHelp() {
        getActivity().findViewById(R.id.help_frame).setVisibility(View.VISIBLE);
    }

    private void hideList() {
        getListView().setVisibility(View.GONE);
    }

    private void hideGrid() {
        getActivity().findViewById(R.id.gallery_grid).setVisibility(View.GONE);
    }

    private void showGrid() {
        getActivity().findViewById(R.id.gallery_grid).setVisibility(View.VISIBLE);
    }

    private void showList() {
        getListView().setVisibility(View.VISIBLE);
    }

    class DownloadGalleryTask extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... strings) {
            return client.getGallery();
        }

        protected void onPostExecute(Response response) {
            loading = false;

            if (response.hasException()) {
                try {
                    throw response.getException();
                } catch (QueryUnauthenticatedException e) {
                    showLoginFragment(LoginActivity.APP_SESSION_TIMEOUT);
                } catch (QueryConnectionException e) {
                    showLoginFragment(LoginActivity.APP_CONNECTION_ERROR);
                } catch (QueryException e) {
                    showDialog(R.string.app_name, R.string.application_error);
                }
            } else {
                if (response.getData() != null) {
                    if (setGalleryHash(response.getData().hashCode())) {
                        try {
                            adapter.addItems(new JSONArray(response.getData()));
                        } catch (JSONException e) {
                            showDialog(R.string.app_name, R.string.application_error);
                            e.printStackTrace();
                        }
                    }
                }
            }
            adapter.getCount();
        }
    }

    /**
     * @param hashCode the new hash code of the gallery.
     * @return true if the previous hash did not match the new.
     */
    private boolean setGalleryHash(int hashCode) {
        if (galleryHash == hashCode) {
            return false;
        } else {
            galleryHash = hashCode;
            return true;
        }
    }

    private void showDialog(int title, int message) {
        new QueryDialog(getActivity()).show(getString(title), getString(message));
    }

    private void showLoginFragment(int errorCode) {
        getActivity().setResult(errorCode);
        getActivity().finish();
    }
}
