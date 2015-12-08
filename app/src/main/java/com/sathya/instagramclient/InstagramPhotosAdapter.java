package com.sathya.instagramclient;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by s.srinivas2 on 12/7/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        TextView tvCaption = (TextView) convertView.findViewById(R.id.txtCaption);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.txtUserName);
        TextView tvRelativeTime = (TextView) convertView.findViewById(R.id.txtRelativeTime);
        TextView tvLikeCount = (TextView) convertView.findViewById(R.id.txtLikeCount);
        TextView tvCommentCount = (TextView) convertView.findViewById(R.id.txtCommentCount);

        ImageView ivPostImage = (ImageView) convertView.findViewById(R.id.imgPostImage);
        ImageView ivUserProfilePic = (ImageView) convertView.findViewById(R.id.imgUserProfilePic);

        tvCaption.setText(photo.caption);
        tvUsername.setText(photo.username);
        Long createdTime = Long.parseLong(photo.createdTime) * 1000;
        String postTime = DateUtils.getRelativeTimeSpanString(createdTime).toString();
        tvRelativeTime.setText(postTime);
        ivPostImage.setImageResource(0);

        // ivPostImage.getLayoutParams().height = photo.imageHeight;
        // ivPostImage.getLayoutParams().width = photo.imageWidth;

        // Trying to set the image with the right aspect ratio using picasso
        Picasso.with(getContext()).load(photo.imageUrl).resize(photo.imageWidth, 0).into(ivPostImage);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(getContext())
                .load(photo.userProfilePic)
                .resize(150, 0)
                .transform(transformation)
                .into(ivUserProfilePic);
        String likeCount = photo.likesCount + " Likes";
        tvLikeCount.setText(likeCount);
        String commentCount = photo.commentsCount + " Comments";
        tvCommentCount.setText(commentCount);

        return convertView;
    }
}
