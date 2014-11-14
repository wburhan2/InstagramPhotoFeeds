package com.wilsonburhan.instagramphotofeeds;

import android.content.Context;
import android.graphics.Point;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wilson on 11/12/14.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> photos) {
        super(context, android.R.layout.simple_list_item_1, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        // Create a new view if there is none
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_item, parent, false);

        ImageView avatar = (ImageView)convertView.findViewById(R.id.avatar);
        TextView username = (TextView)convertView.findViewById(R.id.username);
        TextView time = (TextView)convertView.findViewById(R.id.time);
        ImageView pic = (ImageView)convertView.findViewById(R.id.photo);
        TextView likeCount = (TextView)convertView.findViewById(R.id.like_count);

        // clears the image in case is a recycled view and set the avatar image.
        avatar.setImageResource(0);
        Picasso.with(getContext()).load(photo.avatarUrl).into(avatar);

        // Sets the username
        username.setText(photo.username);

        // Sets the date
        Date createdAtDate = new Date(photo.timestamp * 1000);
        Date now = new Date();
        time.setText(DateUtils.getRelativeTimeSpanString(createdAtDate.getTime(), now.getTime(), DateUtils.SECOND_IN_MILLIS));

        // Sets how many likes.
        likeCount.setText(NumberFormat.getInstance().format(photo.likesCount) + " likes");

        // Set the size of the image based on the screen size while maintaining the image ratio.
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point windowSize = new Point();
        display.getSize(windowSize);
        pic.getLayoutParams().height = windowSize.x;

        // clears the image in case is a recycled view and set the photo.
        pic.setImageResource(0);
        Picasso.with(getContext()).load(photo.url).into(pic);

        return convertView;
    }
}
