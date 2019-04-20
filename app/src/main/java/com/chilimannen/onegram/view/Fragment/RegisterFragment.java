package com.chilimannen.onegram.view.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.chilimannen.onegram.R;
import com.chilimannen.onegram.model.API.Client;
import com.chilimannen.onegram.model.API.ClientProtocol;
import com.chilimannen.onegram.model.API.Exception.EmailInvalidException;
import com.chilimannen.onegram.model.API.Exception.QueryConflictException;
import com.chilimannen.onegram.model.API.Exception.QueryConnectionException;
import com.chilimannen.onegram.model.API.Exception.QueryException;
import com.chilimannen.onegram.model.API.Exception.QueryNotAcceptedException;
import com.chilimannen.onegram.model.API.Response;
import com.chilimannen.onegram.view.Activity.LoginActivity;
import com.chilimannen.onegram.view.Helper.CertificateHelper;
import com.chilimannen.onegram.view.Helper.PreferenceHelper;
import com.chilimannen.onegram.view.Helper.QueryDialog;

/**
 * @author Robin Duda
 *
 * Fragment used for registration.
 */

public class RegisterFragment extends Fragment {
    private ClientProtocol client;
    private RegisterAccountTask registerTask;

    public RegisterFragment() {
        setRetainInstance(true);
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ((LoginActivity) getActivity()).setActionBarTitle(R.string.app_name);
    }

    private View.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isPasswordsMatching()) {
                showLoading();

                if (registerTask == null
                        || registerTask.getStatus() == AsyncTask.Status.FINISHED)
                    registerTask = (RegisterAccountTask) new RegisterAccountTask().execute();
            } else
                passwordsMismatchDialog();
        }
    };

    @Override
    public void onPause() {
        if (registerTask != null
                && registerTask.getStatus() == AsyncTask.Status.RUNNING)
            registerTask.cancel(true);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideLoading();
    }

    private void showLoading() {
        getActivity().findViewById(R.id.loading_frame).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.edit_password).setVisibility(View.GONE);
        getActivity().findViewById(R.id.edit_username).setVisibility(View.GONE);
        getActivity().findViewById(R.id.edit_password_repeat).setVisibility(View.GONE);
    }

    private void hideLoading() {
        getActivity().findViewById(R.id.loading_frame).setVisibility(View.GONE);
        getActivity().findViewById(R.id.edit_password).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.edit_username).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.edit_password_repeat).setVisibility(View.VISIBLE);
    }

    private void passwordsMismatchDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.login)
                .setMessage(R.string.registration_passwordmismatch)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.baseline_warning_black)
                .show();
    }

    private String getUsername() {
        return ((EditText) getActivity().findViewById(R.id.edit_username)).getText().toString();
    }

    private String getPassword() {
        return ((EditText) getActivity().findViewById(R.id.edit_password)).getText().toString();
    }

    private String getPasswordRepeat() {
        return ((EditText) getActivity().findViewById(R.id.edit_password_repeat)).getText().toString();
    }

    private boolean isPasswordsMatching() {
        return (getPassword().equals(getPasswordRepeat()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        view.findViewById(R.id.button_register).setOnClickListener(register);

        ((ImageView) view.findViewById(R.id.app_logo)).getDrawable().setColorFilter(
                getContext().getColor(R.color.solid),
                PorterDuff.Mode.SRC_IN);

        return view;
    }


    private void showDialog(int title, int message) {
        new QueryDialog(getActivity()).show(getString(title), getString(message));
    }

    class RegisterAccountTask extends AsyncTask<String, Void, Response> {
        private String password;
        private String username;

        @Override
        public void onPreExecute() {
            this.username = getUsername();
            this.password = getPassword();

            if (client == null)
                client = new Client(CertificateHelper.getCertificate(getActivity()));

        }

        @Override
        protected Response doInBackground(String... strings) {
            return (client.registerAndLogin(username, password));

        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                hideLoading();
                try {
                    throw response.getException();
                } catch (QueryNotAcceptedException e) {
                    showDialog(R.string.registration, R.string.registration_credential_weak);
                } catch (EmailInvalidException e) {
                    showDialog(R.string.registration, R.string.email_registration_mail_failed);
                } catch (QueryConflictException e) {
                    showDialog(R.string.registration, R.string.registration_exists);
                } catch (QueryConnectionException e) {
                    showDialog(R.string.registration, R.string.connection_error);
                } catch (QueryException e) {
                    showDialog(R.string.registration, R.string.application_error);
                }
            } else {
                PreferenceHelper preferences = new PreferenceHelper(getActivity());
                preferences.setUsername(getUsername());
                showGallery();
            }
        }
    }

    private void showGallery() {
        ((LoginActivity) getActivity()).userLoggedOn(client.getToken());
    }
}