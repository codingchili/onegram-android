package com.codingchili.onegram.view.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codingchili.onegram.R;
import com.codingchili.onegram.view.fragment.FullscreenImageFragment;

/**
 * @author Robin Duda
 *
 * Implements the GalleryAdapter and shows its content in a Grid.
 */

public class GalleryAdapterThumbnails extends GalleryAdapter {
    private Context context;

    public GalleryAdapterThumbnails(Context context) {
        super(context, R.layout.list_item_thumbs);
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        return getGridView(view, position, parent);
    }


    private View getGridView(View view, final int position, ViewGroup parent) {
        final GalleryImage image = getItem(position);
        view = getHolderView(view, parent);
        GalleryThumbHolder holder = (GalleryThumbHolder) view.getTag();

        GridView grid = (GridView) parent;

        if (parent.getWidth() != 0) {
            int margin = getUniformMarginDp(
                    holder.box.getLayoutParams().width,
                    grid.getNumColumns(),
                    parent.getWidth());

            grid.setVerticalSpacing(margin);
        }

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.gallery_remove_image)
                        .setTitle(R.string.action_gallery)
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeImage(image);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
                return false;
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction transaction = ((Activity) context).getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new FullscreenImageFragment().setArguments(image),
                        context.getString(R.string.fragment_fullscreen));
                transaction.addToBackStack(context.getString(R.string.fragment_gallery));
                transaction.commit();
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
                loadImage(image);
            }

            if (image.isLoading()) {
                holder.box.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.INVISIBLE);
            }
        }

        return view;

    }

    private View getHolderView(View view, ViewGroup parent) {
        final GalleryThumbHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_thumbs, parent, false);

            holder = new GalleryThumbHolder();

            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.box = (RelativeLayout) view.findViewById(R.id.box);

            view.setTag(holder);
        }

        return view;
    }

    /**
     * Calculates the amount of padding to apply between rows based on the
     * automatically calculated column padding.
     * @param width of every column item.
     * @param columns number of columns in the grid.
     * @param gridWidth how wide the whole grid is.
     * @return the amount of vertical padding that matches the horizontal.
     */
    private int getUniformMarginDp(int width, int columns, int gridWidth) {
        int contentWidth = columns * width;
        int rowMargin = gridWidth - contentWidth;
        return rowMargin / columns;
    }

    private class GalleryThumbHolder {
        ImageView image;
        RelativeLayout box;
    }
}
