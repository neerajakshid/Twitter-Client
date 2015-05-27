package com.codepath.apps.twitterclient.helpers;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class Constants {

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Api to call
    public static final String BASE_REST_URL = "https://api.twitter.com/1.1/"; // Base url of twitter API
    public static final String REST_CONSUMER_KEY = "yOr1EMCkBqPpV2kMaFXODChsT";       // Consumer Key generated while registering for twitter
    public static final String REST_CONSUMER_SECRET = "vuuKFydm7sITMA2a31FN8q9skAK4GqNVXEwiGA4eIDCccAHBrh"; // Consumer Secret Key generated while registering for twitter
    public static final String REST_CALLBACK_URL = "oauth://cptwitterclient"; // call back url

    public static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    public static final String REST_HOME_TIMELINE_URL = "statuses/home_timeline.json";
    public static final String REST_VERIFY_CREDENTIALS_URL = "account/verify_credentials.json";
    public static final String REST_UPDATE_STATUS_URL = "statuses/update.json";
    public static final String REST_SHOW_TWEET_URL = "statuses/show.json";
    public static final String REST_SEARCH_TWEET_URL = "search/tweets.json";
    public static final String REST_CREATE_FAVORITES_URL = "favorites/create.json";
    public static final String REST_DESTROY_FAVORITES_URL = "favorites/destroy.json";
    public static final String REST_SHOW_USER_URL = "users/show.json";
    public static final String REST_RETWEET_URL = "statuses/retweet/";
    public static final String REST_DESTROY_RETWEET_URL = "statuses/destroy/";
    public static final int REQUEST_CODE = 200;

    public static final String GRAY_COLOR = "#a5a5a5";
    public static final int DEFAULT_MAXID = 0;
    public static final int MAX_TWEET_COUNT = 140;

    public static final String ACTION_REPLY = "reply";
    public static final String ACTION_RETWEET ="retweet";


}
