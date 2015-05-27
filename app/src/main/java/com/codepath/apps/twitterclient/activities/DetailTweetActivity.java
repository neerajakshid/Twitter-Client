package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView;

import com.codepath.apps.twitterclient.R;

import com.codepath.apps.twitterclient.apps.TwitterApplication;
import com.codepath.apps.twitterclient.helpers.Constants;

import com.codepath.apps.twitterclient.helpers.Utilities;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.Header;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailTweetActivity extends ActionBarActivity {
    Tweet tweet = null;
    TwitterClient client = TwitterApplication.getClient();
    ImageView ivProfile, ivDetailPostImage;
    TextView tvName, tvScreenName,tvBody, tvDate, tvRetweetsCount, tvFavouritesCount,tvRetweets,tvFavourites;
    TextView tvDetailFavoriteCount,tvDetailRetweetCount, ibDetailShare, ibDetailReply;
    Button btTweetStatus, btActionReply, btActionRetweet, btActionFavorite, btActionShare;
    View vTopSeparator, vBottomSeparator;
    WebView wvMedia;
    int favouriteCount=0;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tweet = getIntent().getParcelableExtra("selectedTweet");
        user = tweet.getUser();
        setupViews();
        fillViews();
    }

    private void setupViews() {

        // Add twitter bird
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        ivProfile = (ImageView) findViewById(R.id.ivDetailProfileImage);
      //  btTweetStatus = (Button) findViewById(R.id.bTweetStatusDetail);
        tvName = (TextView) findViewById(R.id.tvDetailname);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvDate = (TextView) findViewById(R.id.tvDate);
        wvMedia = (WebView) findViewById(R.id.wvMedia);
        tvRetweetsCount = (TextView) findViewById(R.id.tvRetweetsCount);
        tvFavouritesCount = (TextView) findViewById(R.id.tvFavouritesCount);
        tvRetweets = (TextView) findViewById(R.id.tvRetweets);
        tvFavourites = (TextView) findViewById(R.id.tvFavourites);
        tvDetailFavoriteCount = (TextView) findViewById(R.id.tvDetailFavoriteCount);
        tvDetailRetweetCount = (TextView) findViewById(R.id.tvDetailRetweetCount);
        vTopSeparator = (View) findViewById(R.id.vTopSeparator);
        vBottomSeparator = (View) findViewById(R.id.vBottomSeparator);
        ivDetailPostImage = (ImageView) findViewById(R.id.ivDetailPostImage);

        ibDetailReply = (TextView) findViewById(R.id.ibDetailReply);
        ibDetailShare = (TextView) findViewById(R.id.ibDetailShare);
    }

    private void fillViews() {
        user = tweet.getUser();
        tvName.setText(user.getUserName());
        tvScreenName.setText("@" + user.getScreenName());
        if (tweet.getMedia_url() != null) {
            Picasso.with(this).load(tweet.getMedia_url())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .resize(tweet.getMediaWidth(), tweet.getMediaHeight())
                    .into(ivDetailPostImage);
        }

        if (tweet.getFavorited()) {
            tvDetailFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on, 0, 0, 0);
            tvDetailFavoriteCount.setTextColor(Color.parseColor("#FFAC33"));
        } else {
            tvDetailFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite, 0, 0, 0);
            tvDetailFavoriteCount.setTextColor(Color.parseColor("#808080"));
        }

        if(tweet.getRetweeted())
        {
            tvDetailRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on, 0, 0, 0);
            tvDetailRetweetCount.setTextColor(Color.parseColor("#5C913B"));
        } else {
            tvDetailRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet, 0, 0, 0);
            tvDetailRetweetCount.setTextColor(Color.parseColor("#808080"));
        }

         // TO DO
        ibDetailReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Utilities.isNetworkAvailable(getApplicationContext()))
                {
                    Utilities.showAlertDialog(DetailTweetActivity.this, getBaseContext().getString(R.string.no_internet_error), false);
                } else {
                    Utilities.composeTweet(DetailTweetActivity.this, tweet, Constants.ACTION_REPLY, 0);
                }
            }
        });
        tvDetailRetweetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utilities.isNetworkAvailable(getApplicationContext()))
                {
                    Utilities.showAlertDialog(DetailTweetActivity.this, getBaseContext().getString(R.string.no_internet_error), false);
                }
                else {
                    if (tweet.getRetweeted()) {
                        Utilities.reTweet(tweet, false, getApplicationContext());
                        tweet.setRetweeted(false);
                        tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                        tvDetailRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet, 0, 0, 0);
                        tvDetailRetweetCount.setTextColor(Color.parseColor("#808080"));
                        tvDetailRetweetCount.setText(tweet.getRetweetCount()+"");
                        tvRetweetsCount.setText(tweet.getRetweetCount()+"");

                    } else {
                        Utilities.reTweet(tweet, true, getApplicationContext());
                        tweet.setRetweeted(true);
                        tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                        tvDetailRetweetCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_on, 0, 0, 0);
                        tvDetailRetweetCount.setTextColor(Color.parseColor("#5C913B"));
                        tvDetailRetweetCount.setText(tweet.getRetweetCount()+"");
                        Utilities.composeTweet(DetailTweetActivity.this, tweet, Constants.ACTION_RETWEET, 0); // Quote a tweet
                        tvRetweetsCount.setText(tweet.getRetweetCount()+"");
                    }
                }
            }
        });

        tvDetailRetweetCount.setText(tweet.getRetweetCount()+"");
        tvRetweetsCount.setText(tweet.getRetweetCount()+"");

        tvDetailFavoriteCount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!Utilities.isNetworkAvailable(getApplicationContext()))
                {
                    Utilities.showAlertDialog(DetailTweetActivity.this, getBaseContext().getString(R.string.no_internet_error), false);
                }
                else {
                    if (tweet.getFavorited()) {
                        Utilities.favourite(tweet, false, getApplicationContext());
                        tweet.setFavorited(false);
                        tweet.setFavouritesCount(tweet.getFavouritesCount() - 1);
                        tvDetailFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite, 0, 0, 0);
                        tvDetailFavoriteCount.setText(tweet.getFavouritesCount()+"");
                        tvDetailFavoriteCount.setTextColor(Color.parseColor("#808080"));
                        tvFavouritesCount.setText(tweet.getFavouritesCount()+"");
                    } else {
                        Utilities.favourite(tweet, true, getApplicationContext());
                        tweet.setFavorited(true);
                        tweet.setFavouritesCount(tweet.getFavouritesCount() + 1);
                        tvDetailFavoriteCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite_on, 0, 0, 0);
                        tvDetailFavoriteCount.setTextColor(Color.parseColor("#FFAC33"));
                        tvDetailFavoriteCount.setText(tweet.getFavouritesCount()+"");
                        tvFavouritesCount.setText(tweet.getFavouritesCount()+"");
                    }
                }

            }
        });

        tvDetailFavoriteCount.setText(tweet.getFavouritesCount()+"");
        tvFavouritesCount.setText(tweet.getFavouritesCount()+"");
        ibDetailShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = tweet.getUser();
                SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a",
                        Locale.US);
                String strTime = formatTime.format(tweet.getCreatedAt());
                SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yy",
                        Locale.US);
                String strDate = formatDate.format(tweet.getCreatedAt());
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String strShareText = user.getUserName() + " (@" + user.getScreenName()
                        + ") tweeted at " + strTime + " on " + strDate + ": "
                        + tweet.getBody();
                shareIntent.putExtra(Intent.EXTRA_TEXT, strShareText);
                startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });
        //setBottomBarMetadata();

        // For Rounded image in profile picture
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl())
                .transform(transformation)
                .placeholder(R.mipmap.ic_launcher)
                .into(ivProfile);

        if (tweet.getRetweetedUserId() != 0) {
            client.retrieveUser(tweet.getRetweetedUserId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (tweet.getRetweetedUserId() != 0) {
                        User retweetedUser = User.fromJson(response);
                      //  btTweetStatus.setVisibility(View.VISIBLE);
                       // btTweetStatus.setText(" "
                             //   + retweetedUser.getUserName() + " retweeted");
                    } else {
                       // btTweetStatus.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                  //  btTweetStatus.setVisibility(View.GONE);
                    throwable.printStackTrace();
                }
            });
        }

        String body = tweet.getBody();
        if (tweet.getActualUrl() != null && tweet.getDisplayUrl() != null) {
            body = body.replace(tweet.getActualUrl(), tweet.getDisplayUrl());
        }
        tvBody.setText(body);
        SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a",
                Locale.US);
        String strTime = formatTime.format(tweet.getCreatedAt());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yy",
                Locale.US);
        String strDate = formatDate.format(tweet.getCreatedAt());
        tvDate.setText(strTime + " . "+ strDate);
        if (tweet.getActualUrl() != null && tweet.getActualUrl().length() > 0) {
            wvMedia.setVisibility(View.VISIBLE);
            wvMedia.getSettings().setJavaScriptEnabled(true);
            wvMedia.loadUrl(tweet.getActualUrl());
            wvMedia.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
            });
        } else {
            wvMedia.setVisibility(View.GONE);
        }

    }


}