package com.codepath.apps.twitterclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.apps.TwitterApplication;
import com.codepath.apps.twitterclient.helpers.Constants;
import com.codepath.apps.twitterclient.helpers.Utilities;
import com.codepath.apps.twitterclient.models.User;
import com.codepath.apps.twitterclient.net.TwitterClient;
import com.codepath.apps.twitterclient.adapters.HomeTweetsAdapter;
import com.codepath.apps.twitterclient.helpers.EndlessScrollListener;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity{
    private TwitterClient client;
    private ArrayList<Tweet> alTweet;
    private HomeTweetsAdapter adTweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
// adding logo to the actionbar at runtime
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // swipecontainer
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine(Constants.DEFAULT_MAXID);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvTweets = (ListView) findViewById(R.id.lvTweets); // find the list view
        //construct the arrayList
        alTweet = new ArrayList<>();
        //construct the adapter from the data source
        adTweets= new HomeTweetsAdapter(this, alTweet);
        //creat adapter to List View
        lvTweets.setAdapter(adTweets);
        // get the client
        client = TwitterApplication.getRestClient();
        populateTimeLine(Constants.DEFAULT_MAXID);
        // Attach the listener to the AdapterView onCreate for Infinity scrolling
        lvTweets.setOnScrollListener(new EndlessScrollListener(10) {
            @Override
            public void onLoadMore(int maxID, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView


                    populateTimeLine(maxID);
                }

        });
        //creat adapter to List View
        lvTweets.setAdapter(adTweets);

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tweet selectedTweet = alTweet.get(position);
                showDetialTweet(selectedTweet);
            }
        });
        loadCache();
    }
    // loading Cached Tweets
    private void loadCache() {
        List<Tweet> tweets = new Select().from(Tweet.class)
                .orderBy("createdDate").limit(100).execute();
        adTweets.addAll(tweets);
    }
    // delete cached tweets and users
    private void deleteCache() {
        new Delete().from(Tweet.class).execute();
        new Delete().from(User.class).execute();
    }


    //fill the twitter client with json values
   private void populateTimeLine(final long maxID) {
       if(!Utilities.isNetworkAvailable(this))
       {

           if(adTweets.getCount()==0) {
               List<Tweet> tweetList = Tweet.getAll();
               adTweets.clear();
               adTweets.addAll(tweetList);
           }
       } else {
           client.getHomeTimeline(maxID, new JsonHttpResponseHandler() {
               // Success

               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                   if (maxID <= Constants.DEFAULT_MAXID) {
                       deleteCache();
                       adTweets.clear();
                   }
                   alTweet = Tweet.fromJSONArray(response);
                   if (!alTweet.isEmpty()) {
                       adTweets.addAll(alTweet);
                   }
                   adTweets.notifyDataSetChanged();
                   Log.d("DEBUG SUCCESS", adTweets.toString());
                   swipeContainer.setRefreshing(false);
               }

               // failure
               @Override
               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                   Log.d("DEBUG home timeline", throwable.toString());
                   swipeContainer.setRefreshing(false);
               }
           });
       }
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.compose_tweet) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


   public void onComposeClick(MenuItem menuItem){
            Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
            startActivityForResult(i, Constants.REQUEST_CODE);
   }
    // onclick of an item in a list view
    private void showDetialTweet(Tweet selectedTweet){
        Intent intent = new Intent (TimelineActivity.this, DetailTweetActivity.class);
        intent.putExtra("selectedTweet", selectedTweet);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet newTweet = (Tweet) result.getExtras().get("newTweet");
            adTweets.insert(newTweet, 0);
            adTweets.notifyDataSetChanged();
        } else if(resultCode == RESULT_OK){
            Tweet newTweet = (Tweet) result.getExtras().get("newTweet");
            adTweets.insert(newTweet, 0);
            adTweets.notifyDataSetChanged();
        }
    }

}
