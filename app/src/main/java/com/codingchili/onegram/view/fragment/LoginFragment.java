package com.codingchili.onegram.view.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.codingchili.onegram.R;
import com.codingchili.onegram.model.api.Client;
import com.codingchili.onegram.model.api.ClientProtocol;
import com.codingchili.onegram.model.api.exception.QueryConnectionException;
import com.codingchili.onegram.model.api.exception.QueryException;
import com.codingchili.onegram.model.api.exception.QueryUnauthenticatedException;
import com.codingchili.onegram.model.api.Response;
import com.codingchili.onegram.view.activity.LoginActivity;
import com.codingchili.onegram.view.helper.CertificateHelper;
import com.codingchili.onegram.view.helper.PreferenceHelper;
import com.codingchili.onegram.view.helper.QueryDialog;

/**
 * @author Robin Duda
 *         <p/>
 *         Fragment used for login.
 */

public class LoginFragment extends Fragment {
    private ClientProtocol client;
    private LoginAccountTask loginTask;

    public LoginFragment() {
        setRetainInstance(true);
        setHasOptionsMenu(false);
    }

    private View.OnClickListener login = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showLoading();
            PreferenceHelper preferences = new PreferenceHelper(getActivity());
            preferences.setUsername(getUsername());


            if (loginTask == null
                    || loginTask.getStatus() == AsyncTask.Status.FINISHED) {
                loginTask = (LoginAccountTask) new LoginAccountTask().execute();
            }
        }
    };

    private void showLoading() {
        getActivity().findViewById(R.id.loading_frame).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.edit_password).setVisibility(View.GONE);
        getActivity().findViewById(R.id.edit_username).setVisibility(View.GONE);
    }

    private void hideLoading() {
        getActivity().findViewById(R.id.loading_frame).setVisibility(View.GONE);
        getActivity().findViewById(R.id.edit_password).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.edit_username).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        if (loginTask != null
                && loginTask.getStatus() == AsyncTask.Status.RUNNING)
            loginTask.cancel(true);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideLoading();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ((LoginActivity) getActivity()).setActionBarTitle(R.string.app_name);
    }

    private String getUsername() {
        return ((EditText) getActivity().findViewById(R.id.edit_username)).getText().toString();
    }

    private String getPassword() {
        return ((EditText) getActivity().findViewById(R.id.edit_password)).getText().toString();
    }

    private View.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showRegisterFragment();
        }
    };

    private void showRegisterFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new RegisterFragment(),
                getString(R.string.fragment_registration));
        transaction.addToBackStack(getString(R.string.fragment_login));
        transaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PreferenceHelper preferences = new PreferenceHelper(getActivity());
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.findViewById(R.id.button_login).setOnClickListener(login);
        view.findViewById(R.id.button_register).setOnClickListener(register);

        ((ImageView) view.findViewById(R.id.app_logo)).getDrawable().setColorFilter(
                getContext().getColor(R.color.solid),
                PorterDuff.Mode.SRC_IN);

        ((EditText) view.findViewById(R.id.edit_username)).setText(preferences.getUsername());

        return view;
    }


    private void showDialog(int title, int message) {
        new QueryDialog(getActivity()).show(getString(title), getString(message));
    }

    class LoginAccountTask extends AsyncTask<String, Void, Response> {
        private String password;
        private String username;

        @Override
        protected void onPreExecute() {
            this.password = getPassword();
            this.username = getUsername();

            if (client == null)
                client = new Client(CertificateHelper.getCertificate(getActivity()));
        }


        @Override
        protected Response doInBackground(String... strings) {
            return client.login(username, password);
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                hideLoading();
                try {
                    throw response.getException();
                } catch (QueryUnauthenticatedException e) {
                    showDialog(R.string.login, R.string.unauthorized);
                } catch (QueryConnectionException e) {
                    showDialog(R.string.login, R.string.connection_error);
                } catch (QueryException e) {
                    showDialog(R.string.login, R.string.application_error);
                }
            } else
                showGallery();
        }
    }

    private void showGallery() {
        ((LoginActivity) getActivity()).userLoggedOn(client.getToken());
    }
}
