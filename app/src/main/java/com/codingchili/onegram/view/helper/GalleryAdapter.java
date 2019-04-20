package com.codingchili.onegram.view.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codingchili.onegram.model.api.ClientProtocol;
import com.codingchili.onegram.model.api.exception.QueryException;
import com.codingchili.onegram.model.api.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Robin Duda
 *
 * Base adapter that loads a gallery, must be extended with view data.
 */

public abstract class GalleryAdapter extends ArrayAdapter {
    private ArrayList<GalleryImage> images = new ArrayList<>();
    private ClientProtocol client;
    private AdapterChangeListener listener;


    public GalleryAdapter(Context context, int list) {
        super(context, list);
    }

    public void setListener(AdapterChangeListener listener) {
        this.listener = listener;
    }

    public void setClient(ClientProtocol client) {
        this.client = client;
    }

    public ClientProtocol getClient() {
        return this.client;
    }

    public GalleryImage getItem(int position) {
        return images.get(position);
    }

    public void addAll(ArrayList<GalleryImage> data) {
        images.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public abstract View getView(int position, View view, ViewGroup parent);

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(images, new GalleryImageComparator());
        super.notifyDataSetChanged();
    }

    /**
     * @param array of items to be added to the adapter,
     *              items that already exists in the adapter
     *              will not be re-added.
     */
    public void addItems(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.getJSONObject(i);

            Long date = data.getLong("date");
            String description = data.getString("description");
            String id = data.getString("_id");

            GalleryImage image = new GalleryImage(date, description, id);

            if (!images.contains(image))
                images.add(new GalleryImage(date, description, id));
        }
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        images.clear();

        if (listener != null)
            listener.onAdapterSize(images.size());

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (listener != null)
            listener.onAdapterSize(images.size());

        return images.size();
    }

    /**
     * Removes an image from the gallery and notifies the server.
     */
    public void removeImage(GalleryImage image) {
        images.remove(image);
        new RemoveImageFromGalleryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image.getId());
        notifyDataSetChanged();
    }

    /**
     * Start the image loading process.
     */
    public void loadImage(GalleryImage image) {
        image.setLoading();
        new DownloadImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image);
    }

    public ArrayList<GalleryImage> getAll() {
        return images;
    }

    class DownloadImageTask extends AsyncTask<GalleryImage, Void, Response> {
        private GalleryImage image;

        @Override
        protected Response doInBackground(GalleryImage... image) {
            this.image = image[0];
            return client.downloadImage(this.image.getId());
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                try {
                    throw response.getException();
                } catch (QueryException e) {
                    image.setFailed();
                }
            } else {
                try {
                    image.loadImageFromString(response.getJSON().getString("image"));
                    image.setLoaded();
                } catch (JSONException e) {
                    e.printStackTrace();
                    image.setFailed();
                }
            }
            notifyDataSetChanged();
        }
    }


    class RemoveImageFromGalleryTask extends AsyncTask<String, Void, Response> {
        @Override
        protected Response doInBackground(String... id) {
            return client.removeFromGallery(id[0]);
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                try {
                    throw response.getException();
                } catch (QueryException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
