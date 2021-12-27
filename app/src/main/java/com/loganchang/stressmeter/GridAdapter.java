package com.loganchang.stressmeter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Grid Adapter for Grid View
 */
public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private final int[] mImages;

    public GridAdapter(Context mContext, int[] mImages) {
        this.mContext = mContext;
        this.mImages = mImages;
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public Integer getItem(int position) {
        return mImages[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Set up the right image in each grid
     *
     * @param position    position in grid
     * @param convertView view to show in grid
     * @param parent      parent view group
     * @return view to show in grid
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mImages[position]);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }
}
