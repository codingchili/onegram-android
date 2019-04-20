package com.github.codingchili.model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.github.codingchili.onegram.R;
import com.github.codingchili.api.ClientProtocol;
import com.github.codingchili.api.exception.QueryException;
import com.github.codingchili.api.Response;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Robin Duda
 *
 * An adapter that queries the server
 * for tag-completion.
 */

public class TagSuggestionAdapter extends ArrayAdapter<String> {
    private ClientProtocol client;
    private GetTagSuggestionsTask task;
    private Context context;
    private TagSuggestionAdapterListener listener;

    public TagSuggestionAdapter(Context context) {
        super(context, R.layout.list_item_search_tags);
        this.context = context;
    }


    public void setClient(ClientProtocol client) {
        this.client = client;
    }

    public void setQuery(String query) {
            if (task != null
                    && task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancel(true);
            }

            task = (GetTagSuggestionsTask) new GetTagSuggestionsTask().execute(query);
    }

    public void shutdown() {
        if (task != null
                && task.getStatus() == AsyncTask.Status.RUNNING)
            task.cancel(true);
    }

    public void setListener(TagSuggestionAdapterListener listener) {
        this.listener = listener;
    }


    class GetTagSuggestionsTask extends AsyncTask<String, Void, Response> {

        private void addToSuggestions(String data) throws JSONException {
            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {
                add('#' + array.getString(i));
            }

            if (listener != null)
                listener.onSuggestionCountChange(getCount());
        }

        @Override
        protected Response doInBackground(String... query) {
            return client.getTagSuggestions(query[0]);
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                try {
                    throw response.getException();
                } catch (QueryException e) {
                    e.printStackTrace();
                }
            } else {
                clear();
                addDefault();
                if (response.hasData()) {
                    try {
                        addToSuggestions(response.getData());
                    } catch (JSONException ignored) {
                    }
                }
            }
        }
    }

    private void addDefault() {
        add(context.getString(R.string.search_latest));

        if (listener != null)
            listener.onSuggestionCountChange(getCount());
    }
}
