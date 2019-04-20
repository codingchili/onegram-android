package com.codingchili.onegram.view.helper;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.codingchili.onegram.R;
import com.codingchili.onegram.view.fragment.UploadFragment;
import com.codingchili.onegram.view.helper.filters.BitmapFilters;

import java.util.ArrayList;

/**
 * @author Robin Duda
 *
 * An adapter of the images that should be uploaded,
 * the items are displayed in a list.
 */

public class UploadAdapter extends ArrayAdapter {
    private ArrayList<ImageUpload> uploads = new ArrayList<>();
    private Context context;
    private UploadFragment activity;

    public UploadAdapter(UploadFragment activity) {
        super(activity.getActivity(), R.layout.list_item_upload);

        this.activity = activity;
        this.context = activity.getActivity();
    }

    public void addItem(ImageUpload image) {
        uploads.add(image);
        notifyDataSetChanged();
    }

    public ImageUpload getItem(int position) {
        return uploads.get(position);
    }

    @Override
    public int getCount() {
        activity.listCount(uploads.size());
        return uploads.size();
    }

    public void remove(ImageUpload image) {
        uploads.remove(image);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {


        if (uploads.get(position).isUploading()) {
            view = getUploadProgressView(view, position, parent);
        } else
            view = getUploadItemView(view, position, parent);


        return view;
    }

    private View getUploadProgressView(View view, final int position, ViewGroup parent) {
        final ViewHolderProgress holder;

        if (view == null || !(view.getTag() instanceof ViewHolderProgress)) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_progress, parent, false);

            holder = new ViewHolderProgress();
            holder.description = (TextView) view.findViewById(R.id.upload_title);
            holder.progress = (ProgressBar) view.findViewById(R.id.upload_bar);
            view.setTag(holder);
        } else {
            holder = (ViewHolderProgress) view.getTag();
        }

        holder.description.setText(uploads.get(position).getDescription());

        return view;
    }

    private View getUploadItemView(View view, final int position, ViewGroup parent) {
        final ViewHolderUploads holder;
        final ImageUpload upload = uploads.get(position);

        if (view == null || !(view.getTag() instanceof ViewHolderUploads)) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_upload, parent, false);

            holder = new ViewHolderUploads();
            holder.image = (ImageView) view.findViewById(R.id.upload_image);
            holder.description = (TextView) view.findViewById(R.id.upload_description);
            holder.remove = (Button) view.findViewById(R.id.button_remove);
            holder.rotate = (Button) view.findViewById(R.id.button_rotate);
            holder.upload = (Button) view.findViewById(R.id.button_upload);
            holder.filter = (Spinner) view.findViewById(R.id.filter_list);
            holder.save = (CheckBox) view.findViewById(R.id.upload_save);

            holder.image.setImageBitmap(upload.getBitmap());
            view.setTag(holder);
        } else {
            holder = (ViewHolderUploads) view.getTag();
        }

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploads.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload.addRotation(90);
                notifyDataSetChanged();
            }
        });

        holder.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.description.getText().toString().equals(""))
                    upload.setDescription(holder.description.getHint().toString());
                else {
                    upload.setDescription(holder.description.getText().toString());
                }

                upload.setSaveToGallery(holder.save.isChecked());
                upload.setUploading(true);
                activity.uploadImage(upload);
                notifyDataSetChanged();
            }
        });

        holder.description.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                upload.setDescription(holder.description.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ((Spinner) view.findViewById(R.id.filter_list)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                // todo this has to be refactored.
                if (parentView.getSelectedItem().toString().equals("No Filter"))
                    upload.setFilter(BitmapFilters.Filter.None);

                if (parentView.getSelectedItem().toString().equals("Scream"))
                    upload.setFilter(BitmapFilters.Filter.Scream);

                if (parentView.getSelectedItem().toString().equals("Painting"))
                    upload.setFilter(BitmapFilters.Filter.Painting);

                if (parentView.getSelectedItem().toString().equals("Darken"))
                    upload.setFilter(BitmapFilters.Filter.Darken);

                if (parentView.getSelectedItem().toString().equals("Burn"))
                    upload.setFilter(BitmapFilters.Filter.Burn);

                if (parentView.getSelectedItem().toString().equals("Red"))
                    upload.setFilter(BitmapFilters.Filter.Red);

                if (parentView.getSelectedItem().toString().equals("Green"))
                    upload.setFilter(BitmapFilters.Filter.Green);

                if (parentView.getSelectedItem().toString().equals("Blue"))
                    upload.setFilter(BitmapFilters.Filter.Blue);

                notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        holder.image.setImageBitmap(upload.getBitmap());
        holder.description.setText(upload.getDescription());
        return view;

    }

    private class ViewHolderUploads {
        ImageView image;
        TextView description;
        Button remove;
        Button rotate;
        Button upload;
        Spinner filter;
        CheckBox save;
    }

    private class ViewHolderProgress {
        TextView description;
        ProgressBar progress;
    }
}
