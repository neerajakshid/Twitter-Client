package com.codepath.apps.twitterclient.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.ComposeTweetActivity;
import com.codepath.apps.twitterclient.apps.TwitterApplication;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.net.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utilities {
    private static TwitterClient client = TwitterApplication.getRestClient();

    public static Date convertToDate(String date) throws ParseException {
            SimpleDateFormat sf = new SimpleDateFormat(
                    Constants.TWITTER_DATE_FORMAT, Locale.ENGLISH);
            sf.setLenient(true);
            return sf.parse(date);

    }

    public static String getRelativeTimeAgo(String date) throws ParseException {

            Date tweetDate = convertToDate(date);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(tweetDate);
            final long currentTime = System.currentTimeMillis();
            final long time = calendar.getTimeInMillis();
            final long deltaSeconds = (currentTime - time) / 1000;
            if (deltaSeconds < 10) {
                return "Now";
            }
            if (deltaSeconds < 60) {
                return deltaSeconds + "s";
            }
            final long deltaMins = deltaSeconds / 60;
            if (deltaMins < 60) {
                return deltaMins + "m";
            }
            final long deltaHrs = deltaMins / 60;
            if (deltaHrs < 24) {
                return deltaHrs + "h";
            }
            final long deltaDays = deltaHrs / 24;
            if (deltaDays < 7) {
                return deltaDays + "d";
            }
            calendar.setTimeInMillis(currentTime);
            final long currentYear = calendar.get(Calendar.YEAR);
            calendar.setTime(tweetDate);
            final long year = calendar.get(Calendar.YEAR);
            final Locale locale = Locale.getDefault();
            final String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            final String month = calendar.getDisplayName(Calendar.MONTH,
                    Calendar.SHORT, locale);
            if (year == currentYear) {
                return day + " " + month;
            } else {
                final String yearString = calendar.getDisplayName(Calendar.YEAR,
                        Calendar.SHORT, locale);
                return day + " " + month + " " + yearString;
            }

    }


    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public static void showAlertDialog(Context context, String title, Boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setInverseBackgroundForced(false)
                .setCancelable(false)
                .setIcon((status) ? R.mipmap.success_icon : R.mipmap.error_icon)
                .setNegativeButton(context.getString(R.string.bClose), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void composeTweet(final Context context, final Tweet replyToTweet, final String action, final int requestCode) {
        Intent intent = new Intent(context, ComposeTweetActivity.class);
        if (replyToTweet != null) {
            intent.putExtra("replyToTweet", replyToTweet);
        }
        intent.putExtra("action", action);
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            activity.startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }
    public static void reTweet (Tweet tweet, boolean flag, Context context)
    {
        final boolean localFlag = flag;
        final Context localContext = context;

        client.retweet(tweet.getUniqueId(), flag, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (localFlag) {
                    Tweet tweet = Tweet.fromJSON(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
    public static void favourite (Tweet tweet, boolean flag, Context context)
    {
        final boolean localFlag = flag;
        final Context localContext = context;

        client.favoriteTweet(tweet.getUniqueId(), flag, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (localFlag) {
                    Toast.makeText(localContext, "Tweet Added to Favourites", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(localContext, "Tweet Removed from Favourites ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

}

