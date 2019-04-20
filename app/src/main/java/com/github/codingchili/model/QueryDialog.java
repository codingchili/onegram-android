package com.github.codingchili.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.github.codingchili.onegram.R;

/**
 * @author Robin Duda
 *
 * Displays a simple notification dialog.
 */

public class QueryDialog {
    private Context activity;

    public QueryDialog(Context activity) {
        this.activity = activity;
    }

    public void show(String title, String message) {
        AlertDialog.Builder dialog = makeDialog();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();
    }

    private AlertDialog.Builder makeDialog() {
        return new AlertDialog.Builder(activity)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.baseline_warning_black);
    }
}
