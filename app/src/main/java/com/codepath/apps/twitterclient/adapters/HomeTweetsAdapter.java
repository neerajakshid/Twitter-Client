package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.apps.TwitterApplication;
import com.codepath.apps.twitterclient.helpers.Constants;
import com.codepath.apps.twitterclient.helpers.Utilities;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.net.TwitterClient;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.util.List;


public class HomeTweetsAdapter extends ArrayAdapter<Tweet>{
    TwitterClient client = TwitterApplication.getRestClient();

    public HomeTweetsAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    private static class ViewHolder {
        TextView tvBody;
        TextView tvTimeofPost;
        ImageView ivProfilePicture, ivPostImage;
        TextView tvUserName, tvRetweetCount, tvFavoriteCount, tvReply;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        final ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvTimeofPost = (TextView) convertView.findViewById(R.id.tvTimeofPost);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.ivProfilePicture = (ImageView) convertView.findViewById(R.id.ivProfilePicture);
            viewHolder.ivPostImage = (ImageView) convertView.findViewById(R.id.ivPostImage);
            viewHolder.tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.tvFavoriteCount = (TextView) convertView.findViewById(R.id.tvFavoriteCount);
            viewHolder.tvReply = (TextView) convertView.findViewById(R.id.tvReply);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        // converting time to relative time span
        String relativeDate="";
        try {
            //error processing.
            String createdTime = tweet.getStrCreatedAt();
            if(createdTime!=null) {
                relativeDate = Utilities.getRelativeTimeAgo(tweet.getStrCreatedAt());
            }
            viewHolder.tvTimeofPost.setText(relativeDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        viewHolder.tvUserName.setText(Html.fromHtml("<b>"+tweet.getUser().getUserName()+"</b> <font color='"+ Constants.GRAY_COLOR+"'> @"+tweet.getUser().getScreenName()+"</font>"));
        viewHolder.tvBody.setText(tweet.getBody());

        if(tweet.getMedia_url()!=null){
            resizeImageView(viewHolder.ivPostImage, true);
            Picasso.with(getContext()).load(tweet.getMedia_url())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .resize(tweet.getMediaWidth(), tweet.getMediaHeight())
                    .into(viewHolder.ivPostImage);
        } else {
            resizeImageView(viewHolder.ivPostImage, false);
        }

        viewHolder.ivProfilePicture.setImageResource(android.R.color.transparent); // clear out the old image
        // For Rounded image in profile picture
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .transform(transformation)
                .placeholder(R.mipmap.ic_launcher)
                .into(viewHolder.ivProfilePicture);

        if (tweet.getFavorited()) {
            viewHolder.tvFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on_small, 0, 0, 0);
            viewHolder.tvFavoriteCount.setTextColor(Color.parseColor("#FFAC33"));
        } else {
            viewHolder.tvFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_small, 0, 0, 0);
            viewHolder.tvFavoriteCount.setTextColor(Color.parseColor("#808080"));
        }

        if(tweet.getRetweeted())
        {
            viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on_small, 0, 0, 0);
            viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#5C913B"));
        } else {
            viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_small, 0, 0, 0);
            viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#808080"));
        }




        final User currentUser = tweet.getUser();


        viewHolder.tvReply.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(getContext(), ComposeTweetActivity.class);
                i.putExtra("replyToTweet",tweet);
                getContext().startActivity(i);*/
                if(!Utilities.isNetworkAvailable(getContext()))
                {
                    Utilities.showAlertDialog(getContext(), getContext().getString(R.string.no_internet_error), false);
                } else {
                    Utilities.composeTweet(getContext(), tweet, Constants.ACTION_REPLY, 0);
                }
             }
        });

        viewHolder.tvRetweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utilities.isNetworkAvailable(getContext()))
                {
                    Utilities.showAlertDialog(getContext(), getContext().getString(R.string.no_internet_error), false);
                }
                else {
                    if (tweet.getRetweeted()) {
                        Utilities.reTweet(tweet, false, getContext());
                        tweet.setRetweeted(false);
                        tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                        viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_small, 0, 0, 0);
                        viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#808080"));
                        viewHolder.tvRetweetCount.setText(tweet.getRetweetCount()+"");

                    } else {
                        Utilities.reTweet(tweet, true, getContext());
                        tweet.setRetweeted(true);
                        tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                        viewHolder.tvRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on_small, 0, 0, 0);
                        viewHolder.tvRetweetCount.setTextColor(Color.parseColor("#5C913B"));
                        viewHolder.tvRetweetCount.setText(tweet.getRetweetCount()+"");
                        Utilities.composeTweet(getContext(), tweet, Constants.ACTION_RETWEET, 0); // Quote a tweet
                    }
                }
            }
        });

        viewHolder.tvRetweetCount.setText(tweet.getRetweetCount()+"");

        viewHolder.tvFavoriteCount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!Utilities.isNetworkAvailable(getContext()))
                {
                    Utilities.showAlertDialog(getContext(), getContext().getString(R.string.no_internet_error), false);
                }
                else {
                    if (tweet.getFavorited()) {
                        Utilities.favourite(tweet, false, getContext());
                        tweet.setFavorited(false);
                        tweet.setFavouritesCount(tweet.getFavouritesCount() - 1);
                        viewHolder.tvFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_small, 0, 0, 0);
                        viewHolder.tvFavoriteCount.setText(tweet.getFavouritesCount()+"");
                        viewHolder.tvFavoriteCount.setTextColor(Color.parseColor("#808080"));
                    } else {
                        Utilities.favourite(tweet, true, getContext());
                        tweet.setFavorited(true);
                        tweet.setFavouritesCount(tweet.getFavouritesCount() + 1);
                        viewHolder.tvFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on_small, 0, 0, 0);
                        viewHolder.tvFavoriteCount.setTextColor(Color.parseColor("#FFAC33"));
                        viewHolder.tvFavoriteCount.setText(tweet.getFavouritesCount()+"");
                    }
                }

            }
        });

        viewHolder.tvFavoriteCount.setText(tweet.getFavouritesCount()+"");
        return convertView;
    }

    private void resizeImageView(ImageView imageView, boolean flag) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.height = flag ? (int) getContext().getResources().getDimension(R.dimen.preview_image_height) : 0;
        imageView.setLayoutParams(params);
    }

 }
