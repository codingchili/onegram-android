package com.codingchili.onegram.view.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codingchili.onegram.R;
import com.codingchili.onegram.model.api.ClientProtocol;
import com.codingchili.onegram.model.api.exception.AccountNotVerifiedException;
import com.codingchili.onegram.model.api.exception.QueryConnectionException;
import com.codingchili.onegram.view.helper.QueryDialog;
import com.codingchili.onegram.model.api.exception.QueryException;
import com.codingchili.onegram.model.api.exception.QueryUnauthenticatedException;
import com.codingchili.onegram.model.api.Response;
import com.codingchili.onegram.view.activity.LoginActivity;
import com.codingchili.onegram.view.activity.UserActivity;
import com.codingchili.onegram.view.exception.CameraFailureException;
import com.codingchili.onegram.view.exception.FileFailureException;
import com.codingchili.onegram.view.exception.NoPictureTakenException;
import com.codingchili.onegram.view.helper.CameraHelper;
import com.codingchili.onegram.view.helper.ImageUpload;
import com.codingchili.onegram.view.helper.ScaledBitmapReader;
import com.codingchili.onegram.view.helper.UploadAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Robin Duda
 *
 * Fragment for uploading new images.
 */

public class UploadFragment extends ListFragment {
    private static final int SELECT_PHOTO = 100;
    private ClientProtocol client;
    private CameraHelper camera;
    private UploadAdapter adapter;

    public UploadFragment() {
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.camera = new CameraHelper(getActivity());
        adapter = new UploadAdapter(this);
        setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_upload, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    public UploadFragment setArguments(ClientProtocol client) {
        this.client = client;
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_action_add:
                takePicture();
                return true;
            case R.id.fragment_action_file:
                filePicture();
                return true;
            default:
                return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        view.findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        view.findViewById(R.id.button_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePicture();
            }
        });

        view.findViewById(R.id.image_license).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(R.string.image_license, R.string.image_license_text);
            }
        });

        ((UserActivity) getActivity()).setActionBarTitle(R.string.action_add);

        return view;
    }

    private void takePicture() {
        try {
            startActivityForResult(camera.makeIntent(), CameraHelper.REQUEST_IMAGE_CAPTURE);
        } catch (CameraFailureException e) {
            e.printStackTrace();
            toastCameraError();
        } catch (FileFailureException e) {
            e.printStackTrace();
            toastFileError();
        }
    }

    private void filePicture() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PHOTO:
                handleGalleryPickResult(requestCode, resultCode, data);
                break;
            case CameraHelper.REQUEST_IMAGE_CAPTURE:
                handleCameraResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleGalleryPickResult(int requestCode, int resultCode, Intent intent) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                Uri selected = intent.getData();
                Bitmap bitmap = ScaledBitmapReader.readStream(selected, getActivity().getContentResolver());
                addImageToUploads(new ImageUpload(bitmap));
            }
        } catch (FileNotFoundException e) {
            toastFileError();
            e.printStackTrace();
        }
    }

    private void handleCameraResult(int requestCode, int resultCode, Intent data) {
        try {
            Bitmap bitmap = camera.onCameraResult(requestCode, resultCode, data);
            ImageUpload image = new ImageUpload(bitmap);
            image.addRotation(camera.getShotRotation());
            addImageToUploads(image);

        } catch (NoPictureTakenException e) {
            e.printStackTrace();
        } catch (FileFailureException | IOException e) {
            e.printStackTrace();
            toastFileError();
        }
    }

    private void addImageToUploads(ImageUpload image) {
        adapter.addItem(image);
    }

    private void toastCameraError() {
        toast(R.string.camera_failure);
    }

    private void toastFileError() {
        toast(R.string.file_failure);
    }

    private void toast(int text) {
        Toast.makeText(getActivity(),
                getActivity().getString(text), Toast.LENGTH_SHORT).show();
    }

    public void uploadImage(ImageUpload image) {
        new UploadImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image);
    }

    public void listCount(int size) {
        if (size == 0) {
            showHelp();
        } else {
            showUploads();
        }
    }

    public void showUploads() {
        getListView().setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.help_frame).setVisibility(View.GONE);
    }

    public void showHelp() {
        getListView().setVisibility(View.GONE);
        getActivity().findViewById(R.id.help_frame).setVisibility(View.VISIBLE);
    }

    class UploadImageTask extends AsyncTask<ImageUpload, Void, Response> {
        private ImageUpload image;

        @Override
        protected Response doInBackground(ImageUpload... images) {
            image = images[0];
            return client.uploadImage(image);
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                image.setUploading(false);
                adapter.notifyDataSetChanged();
                try {
                    throw response.getException();
                } catch (AccountNotVerifiedException e) {
                    showDialog(R.string.app_name, R.string.account_unverified);
                } catch (QueryUnauthenticatedException e) {
                    showLoginFragment(LoginActivity.APP_SESSION_TIMEOUT);
                } catch (QueryConnectionException e) {
                    showLoginFragment(LoginActivity.APP_CONNECTION_ERROR);
                } catch (QueryException e) {
                    showDialog(R.string.app_name, R.string.application_error);
                }
            } else {
                try {
                    JSONObject data = response.getJSON();

                    if (data.has("id") && image.isSaveToGallery()) {
                        saveImageIdToGallery(data.getString("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showLoginFragment(LoginActivity.APP_CONNECTION_ERROR);
                }

                if (getActivity() != null) {
                    adapter.remove(image);
                }
            }
        }
    }

    private void saveImageIdToGallery(String id) {
        new SaveToGallery().execute(id);
    }

    class SaveToGallery extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... id) {
            return client.addToGallery(id[0]);
        }

        protected void onPostExecute(Response response) {
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
            }
        }
    }

    private void showDialog(int title, int message) {
        new QueryDialog(getActivity()).show(getString(title), getString(message));
    }

    private void showLoginFragment(int errorCode) {
        if (getActivity() != null) {
            getActivity().setResult(errorCode);
            getActivity().finish();
        }
    }
}
