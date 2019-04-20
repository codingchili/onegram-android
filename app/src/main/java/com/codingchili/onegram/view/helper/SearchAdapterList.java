package com.codingchili.onegram.view.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingchili.onegram.R;
import com.codingchili.onegram.model.api.exception.AccountNotVerifiedException;
import com.codingchili.onegram.model.api.exception.QueryException;
import com.codingchili.onegram.model.api.Response;

import java.util.ArrayList;

/**
 * @author Robin Duda
 *
 * Extends the GalleryAdapter to show search results in a list
 * with asynchronous loading.
 */

public class SearchAdapterList extends GalleryAdapter {
    private Context context;

    public SearchAdapterList(Context context) {
        super(context, R.layout.list_item_gallery);
        this.context = context;
    }


    /**
     * From Android Documentation
     * http://developer.android.com/guide/topics/ui/dialogs.html
     */
    private void showReportDialog(final GalleryImage image) {
        final ArrayList<String> selected = new ArrayList<>();
        final String[] options = context.getResources().getStringArray(R.array.report_reason);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.report)
                .setMultiChoiceItems(R.array.report_reason, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    selected.add(options[which]);
                                } else if (selected.contains(options[which])) {
                                    selected.remove(options[which]);
                                }
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ImageReport report = new ImageReport(image, selected);
                        new ReportImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, report);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        builder.show();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final GalleryImage image = getItem(position);
        view = getHolderView(view, parent);
        GalleryImageHolder holder = (GalleryImageHolder) view.getTag();


            holder.action.setBackgroundResource(android.R.drawable.ic_menu_add);
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddImageToGalleryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image.getId());
                }
            });

            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showReportDialog(image);
                    return false;
                }
            });

        if (image.isLoaded()) {
            holder.image.setImageBitmap(image.getBitmap());
            holder.image.setVisibility(View.VISIBLE);
        } else {
            if (image.isFailed()) {
                // fail silently
                removeImage(image);
            }

            if (!image.isLoading()) {
                image.setLoading();
                loadImage(image);
            }

            if (image.isLoading()) {
                holder.image.setVisibility(View.INVISIBLE);
            }
        }

        holder.date.setText(image.getDateString());
        holder.description.setText(image.getDescription());

        return view;
    }

    private View getHolderView(View view, ViewGroup parent) {
        final GalleryImageHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_gallery, parent, false);

            holder = new GalleryImageHolder();
            holder.image = (ImageView) view.findViewById(R.id.gallery_image);
            holder.date = (TextView) view.findViewById(R.id.gallery_date);
            holder.description = (TextView) view.findViewById(R.id.gallery_description);
            holder.action = (Button) view.findViewById(R.id.button_action);
            view.setTag(holder);
        }

        return view;
    }

    class ReportImageTask extends AsyncTask<ImageReport, Void, Response> {

        @Override
        protected Response doInBackground(ImageReport... report) {
            return getClient().reportImage(report[0]);
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                try {
                    throw response.getException();
                } catch (AccountNotVerifiedException e) {
                    showDialog(R.string.report, R.string.report_unverified);
                } catch (QueryException ignored) {
                    showDialog(R.string.app_name, R.string.application_error);
                }
            }
        }
    }


    class AddImageToGalleryTask extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... id) {
            return getClient().addToGallery(id[0]);
        }

        protected void onPostExecute(Response response) {
            if (response.hasException()) {
                try {
                    throw response.getException();
                } catch (QueryException e) {
                    e.printStackTrace();
                }
            } else {
                showDialog(R.string.action_gallery, R.string.added_to_gallery);
            }
        }
    }

    private void showDialog(int title, int message) {
        new QueryDialog(context).show(context.getString(title), context.getString(message));
    }

    private class GalleryImageHolder {
        ImageView image;
        TextView date;
        TextView description;
        Button action;
    }
}
