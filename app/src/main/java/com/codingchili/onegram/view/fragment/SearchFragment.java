package com.codingchili.onegram.view.fragment;

import android.app.ActionBar;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

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
import com.codingchili.onegram.view.helper.QueryDialog;
import com.codingchili.onegram.view.helper.SearchAdapterList;
import com.codingchili.onegram.view.helper.TagSuggestionAdapter;
import com.codingchili.onegram.view.helper.TagSuggestionAdapterListener;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Robin Duda
 *         <p/>
 *         Fragment used for searching images.
 */

public class SearchFragment extends ListFragment implements AdapterChangeListener, SearchView.OnQueryTextListener,
        TagSuggestionAdapterListener {
    private ClientProtocol client;
    private GalleryAdapter galleryAdapter;
    private TagSuggestionAdapter tagAdapter;
    private SearchView searchView;
    private DownloadSearchTask searchTask;

    public SearchFragment() {
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }


    @Override
    public void onPause() {
        if (searchTask != null
                && searchTask.getStatus() == AsyncTask.Status.RUNNING)
            searchTask.cancel(true);

        tagAdapter.shutdown();

        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        tagAdapter = new TagSuggestionAdapter(getActivity());
        tagAdapter.setClient(client);
        tagAdapter.setListener(this);
        ((ListView) getActivity().findViewById(R.id.suggestions)).setAdapter(tagAdapter);

        galleryAdapter = new SearchAdapterList(getActivity());
        galleryAdapter.setClient(client);
        galleryAdapter.setListener(this);
        setListAdapter(galleryAdapter);

        showSuggestions();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        searchView = (SearchView) menu.findItem(R.id.fragment_action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.search_help));
        TextView searchText = (TextView) searchView.findViewById(searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null));

        searchText.setHintTextColor(getContext().getColor(R.color.text_solid));
        searchText.setTextColor(getContext().getColor(R.color.text_solid));

        MenuItem item = menu.findItem(R.id.fragment_action_search);
        item.expandActionView();

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                // setting query to empty string prevents onQueryTextChange from being
                // called after the SearchView has collapsed. The SearchView is collapsed
                // after this method has completed, the Fragment would already be popped by then.
                searchView.setQuery("", false);
                getFragmentManager().popBackStack();
                return false;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }
        });


        ActionBar actionbar = getActivity().getActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        startSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (searchTask == null
                || searchTask.getStatus() == AsyncTask.Status.FINISHED) {
            hideList();
            tagAdapter.setQuery(newText);
        }
        return false;
    }

    @Override
    public void onSuggestionCountChange(int count) {
        if (searchTask == null
                || searchTask.getStatus() == AsyncTask.Status.FINISHED) {
            if (count == 0)
                hideSuggestions();
            else
                showSuggestions();
        }
    }


    public SearchFragment setArguments(ClientProtocol client) {
        this.client = client;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ((UserActivity) getActivity()).setActionBarTitle(R.string.action_search);
        ListView tagList = ((ListView) view.findViewById(R.id.suggestions));
        tagList.setClickable(true);
        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == 0) {
                    startSearch("");
                    searchView.setQuery("", false);
                } else {
                    String suggestion = tagAdapter.getItem(position);
                    suggestion = suggestion.replace("#", "");
                    searchView.setQuery(suggestion, false);
                    startSearch(suggestion);
                }
            }
        });

        return view;
    }

    private void startSearch(String query) {
        if (searchTask == null
                || searchTask.getStatus() == AsyncTask.Status.FINISHED) {
            searchTask = (DownloadSearchTask) new DownloadSearchTask().execute(query);
            hideList();
            searchView.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_action_search:
                // handled by SearchView listener.
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onAdapterSize(int size) {
        if (size == 0) {
            showSuggestions();
        } else {
            hideSuggestions();
            showList();
        }
    }

    class DownloadSearchTask extends AsyncTask<String, Void, Response> {

        protected void onPreExecute() {
            galleryAdapter.clear();
            hideSuggestions();
            showProgress();
        }

        @Override
        protected Response doInBackground(String... strings) {
            String query = strings[0];
            return client.getSearch(query);
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                hideProgress();
                try {
                    throw response.getException();
                } catch (QueryUnauthenticatedException e) {
                    showLoginFragment(LoginActivity.APP_SESSION_TIMEOUT);
                } catch (QueryConnectionException e) {
                    showLoginFragment(LoginActivity.APP_CONNECTION_ERROR);
                } catch (QueryException ignored) {
                    // invalid search: no response.
                }
            } else {
                hideProgress();

                if (response.getData() != null) {
                    try {
                        if (!this.isCancelled())
                            galleryAdapter.addItems(new JSONArray(response.getData()));
                        galleryAdapter.getCount();
                    } catch (JSONException e) {
                        showDialog(R.string.app_name, R.string.application_error);
                        e.printStackTrace();
                    }
                } else
                    showDialog(R.string.app_name, R.string.application_error);
            }
        }
    }

    private void showList() {
        getListView().setVisibility(View.VISIBLE);

    }

    private void hideList() {
        getListView().setVisibility(View.GONE);
    }

    private void showProgress() {
        getActivity().findViewById(R.id.search_help).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.suggestions_frame).setVisibility(View.GONE);
        getListView().setVisibility(View.GONE);
    }

    private void hideProgress() {
        getActivity().findViewById(R.id.search_help).setVisibility(View.GONE);
        getListView().setVisibility(View.GONE);
    }

    private void showSuggestions() {
        getActivity().findViewById(R.id.search_help).setVisibility(View.GONE);
        getActivity().findViewById(R.id.suggestions_frame).setVisibility(View.VISIBLE);
        getListView().setVisibility(View.GONE);
    }

    private void hideSuggestions() {
        getActivity().findViewById(R.id.suggestions_frame).setVisibility(View.GONE);
    }

    private void showDialog(int title, int message) {
        new QueryDialog(getActivity()).show(getString(title), getString(message));
    }

    private void showLoginFragment(int errorCode) {
        getActivity().setResult(errorCode);
        getActivity().finish();
    }
}
