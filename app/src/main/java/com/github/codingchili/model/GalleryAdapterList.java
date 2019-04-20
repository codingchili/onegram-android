package com.github.codingchili.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.codingchili.onegram.R;

/**
 * @author Robin Duda
 *
 * Implements the GalleryAdapter with a List as View.
 */

public class GalleryAdapterList extends GalleryAdapter {
    private Context context;

    public GalleryAdapterList(Context context) {
        super(context, R.layout.list_item_gallery);
        this.context = context;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final GalleryImage image = getItem(position);
        view = getHolderView(view, parent);
        GalleryImageHolder holder = (GalleryImageHolder) view.getTag();

            holder.action.setBackgroundResource(android.R.drawable.ic_menu_close_clear_cancel);
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeImage(image);
                    notifyDataSetChanged();
                }
            });

        if (image.isLoaded()) {
            holder.image.setImageBitmap(image.getBitmap());
            holder.image.setVisibility(View.VISIBLE);
        } else {
            if (image.isFailed()) {
                // fail silently
                super.removeImage(image);
            }

            if (!image.isLoading()) {
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

    private class GalleryImageHolder {
        ImageView image;
        TextView date;
        TextView description;
        Button action;
    }
}
