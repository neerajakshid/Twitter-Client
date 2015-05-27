package com.codepath.apps.twitterclient.net;

import android.content.Context;

import com.codepath.apps.twitterclient.helpers.Constants;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {


	public TwitterClient(Context context) {
		super(context, Constants.REST_API_CLASS, Constants.BASE_REST_URL, Constants.REST_CONSUMER_KEY, Constants.REST_CONSUMER_SECRET, Constants.REST_CALLBACK_URL);
	}

/*    GET

            count = 25
    since_id = 1*/

    // getting home timeline details
    public void getHomeTimeline(long maxID, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl(Constants.REST_HOME_TIMELINE_URL);
        RequestParams params = new RequestParams();
        // define params
        params.put("count", 25);
        params.put("since_id",1);
        // Execute the request
        if (maxID > 0) {
            params.put("max_id", maxID);
        }
        getClient().get(apiUrl, params,handler);
    }

   // getting authenticated user details
   public void verifyAccountCredentials(AsyncHttpResponseHandler handler){
       String apiUrl = getApiUrl(Constants.REST_VERIFY_CREDENTIALS_URL);
       RequestParams params = new RequestParams();
       // define params
       params.put("skip_status", false);
       params.put("include_email",false);
       // Execute the request
       getClient().get(apiUrl, params, handler);
   }

    public static TwitterClient getInstance(Context context) {
        return (TwitterClient) OAuthBaseClient.getInstance(TwitterClient.class,
                context);
    }

    // save new  tweet
    public void saveNewTweet(String tweet, AsyncHttpResponseHandler handler) {
        String apiURL = getApiUrl(Constants.REST_UPDATE_STATUS_URL);
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        getClient().post(apiURL, params, handler);
    }
    // reply to another tweet
    public void reply(String tweet, long replyToID, AsyncHttpResponseHandler handler) {
        String apiURL = getApiUrl(Constants.REST_UPDATE_STATUS_URL);
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        params.put("in_reply_to_status_id", replyToID);
        getClient().post(apiURL, params, handler);
    }
// create favourite tweet
    public void favoriteTweet(long tweetId, boolean flag, AsyncHttpResponseHandler handler) {
        String apiUrl;
        if (flag==true)
            apiUrl = getApiUrl(Constants.REST_CREATE_FAVORITES_URL);
        else apiUrl = getApiUrl(Constants.REST_DESTROY_FAVORITES_URL);

        RequestParams params = new RequestParams();
        if (tweetId > 0) {
            params.put("id", Long.toString(tweetId));
        }
        client.post(apiUrl, params, handler);
    }


    // retrieve user based on user id
    public void retrieveUser(long userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl(Constants.REST_SHOW_USER_URL);
        RequestParams params = new RequestParams();
        if (userId > 0) {
            params.put("id", Long.toString(userId));
        }
        client.post(apiUrl, params, handler);
    }

    // update retweet
    public void retweet(long tweetId, boolean flag,  AsyncHttpResponseHandler handler) {
        String apiUrl;
        String id = Long.toString(tweetId);
        if(flag==true)
            apiUrl = getApiUrl(Constants.REST_RETWEET_URL + id +".json");
        else
            apiUrl = getApiUrl(Constants.REST_DESTROY_RETWEET_URL + id +".json");
        RequestParams params = new RequestParams();
        client.post(apiUrl, params, handler);
    }
 }