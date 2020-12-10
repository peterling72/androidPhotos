package com.example.androidphotos50;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom Adapter used to display a Photo and its thumbnail in the album view
 */
public class ImageAdapter extends ArrayAdapter<Photo> {
    private int resource;
    private Context context;

    public ImageAdapter(Context context, int resource, List<Photo> items) {
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(resource, null);
        }

        Photo p = getItem(position);

        if (p != null) {
            ImageView imageView = v.findViewById(R.id.imageView);
            TextView caption = v.findViewById(R.id.caption);

            //Display image
            imageView.setImageURI(Uri.parse(p.getPath()));

            //Add caption
            caption.setText(p.getCaption());
        }

        return v;
    }

}
