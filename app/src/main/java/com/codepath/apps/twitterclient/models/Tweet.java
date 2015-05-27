package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterclient.helpers.Constants;
import com.codepath.apps.twitterclient.helpers.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name="Tweets")
public class Tweet extends Model implements Parcelable {

    // list out the attributes of the data
    @Column(name = "body")
    private String body;
    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uniqueId; // unigue id for tweet
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;
    @Column(name = "createdDate")
    private Date createdAt;
    private String strCreatedAt;
    @Column(name = "favorited")
    private Boolean favorited;
    @Column(name = "retweetedFrom")
    private User retweetedFrom;
    @Column(name = "retweeted")
    private Boolean retweeted;
    @Column(name = "retweetCount")
    private int retweetCount;
    @Column(name = "favouritesCount")
    private int favouritesCount;
    @Column(name = "retweetedUserId")
    private long retweetedUserId;
    @Column(name = "displayUrl")
    private String displayUrl;
    @Column(name = "actualUrl")
    private String actualUrl;
    @Column(name = "media_url")
    private String media_url;
    @Column(name = "media_width")
    private int mediaWidth;
    @Column(name = "media_height")
    private int mediaHeight;
    @Column(name = "media_type")
    private String mediaType;
    @Column(name = "video_url")
    private String videoUrl;


    @Column(name = "replied")
    private String replied;


    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        try {
            if (jsonObject.getString("text") != null)
                tweet.body = jsonObject.getString("text");
            else {
                Log.v("TweetModel", "Invalid Tweet");
                tweet.body = "";
            }
            if (jsonObject.getString("id") != null)
                tweet.uniqueId = jsonObject.getLong("id"); // unigue id for tweet
            else {
                Log.v("TweetModel", "Invalid Id");
                tweet.uniqueId = 0;
            }
            tweet.user= User.findOrCreateUserFromJSON(jsonObject.getJSONObject("user"));
            if (jsonObject.getString("created_at") != null) {
                try {
                    tweet.createdAt = Utilities.convertToDate(jsonObject.getString("created_at"));
                    tweet.strCreatedAt = jsonObject.getString("created_at");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                DateFormat dateFormat = new SimpleDateFormat(Constants.TWITTER_DATE_FORMAT);
                //get current date time with Date()
                Date date = new Date();
                tweet.strCreatedAt = dateFormat.format(date);
                tweet.createdAt = dateFormat.parse(tweet.strCreatedAt );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(jsonObject.optString("in_reply_to_screen_name")!=null)
            {
                tweet.replied = jsonObject.getString("in_reply_to_screen_name") ;
            }


            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");

            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favouritesCount = jsonObject.getInt("favorite_count");
            JSONArray media = jsonObject.getJSONObject("entities").optJSONArray("media");
            if (media != null) {
                tweet.media_url = media.getJSONObject(0).optString("media_url");
                JSONObject sizes = media.getJSONObject(0).optJSONObject("sizes");
                if (sizes != null) {
                    tweet.mediaWidth = sizes.getJSONObject("small").getInt("w");
                    tweet.mediaHeight = sizes.getJSONObject("small").getInt("h");
                }
                tweet.mediaType = "image";
                if ("video".equals(media.getJSONObject(0).optString("type"))) {
                    tweet.mediaType = "video";
                    tweet.videoUrl = media.getJSONObject(0).getJSONObject("video_info").getJSONArray("variants").getJSONObject(0).optString("url");
                }
            }

            if (jsonObject.has("retweeted_status")) {
                final JSONObject retweetedStatus = jsonObject
                        .getJSONObject("retweeted_status");
                final User retweetedUser = User.fromJson(retweetedStatus
                        .getJSONObject("user"));
                tweet.retweetedUserId = retweetedUser.userId;
                tweet.body = retweetedStatus.getString("text");
                tweet.retweetCount = retweetedStatus.getInt("retweet_count");
                tweet.favouritesCount = retweetedStatus
                        .getInt("favorite_count");
            }


        } catch (JSONException e) {

            e.printStackTrace();
        }
        return  tweet;
    }
    // converts jsonArray to arraylist
   public static ArrayList<Tweet> fromJSONArray (JSONArray jsonArray){
       ArrayList<Tweet> arrayListTweets = new ArrayList<>();
    for(int i =0; i<jsonArray.length(); i++){
        try {
            JSONObject tweetJson = jsonArray.getJSONObject(i);
            Tweet tweet = Tweet.fromJSON(tweetJson);
            if(tweet!=null){
                arrayListTweets.add(tweet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            continue; // added inorder to continue even if one tweet fails to load
        }



    }
       return arrayListTweets;
   }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        return body;
    }
    public String getMediaType() {
        return mediaType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
    public long getUniqueId() {
        return uniqueId;
    }

    public User getUser() {
        return user;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public long getRetweetedUserId() {
        return retweetedUserId;
    }
    public String getReplied() {
        return replied;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public String getActualUrl() {
        return actualUrl;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }
    public String getStrCreatedAt() {
        return strCreatedAt;
    }

    public int getMediaWidth() {
        return mediaWidth;
    }

    public int getMediaHeight() {
        return mediaHeight;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setStrCreatedAt(String strCreatedAt) {
        this.strCreatedAt = strCreatedAt;
    }

    public User getRetweetedFrom() {
        return retweetedFrom;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public Tweet() {
        super();
    }

    public static Tweet saveFromJSON(JSONObject json) {
        Tweet tweet = Tweet.fromJSON(json);
        if (tweet.uniqueId != 0) {
            tweet.save();
        }
        return tweet;
    }

    public static void makeChanges(long tweetId){
        Tweet updatedTweet = null;
        updatedTweet = new Select().from(Tweet.class).where("tweet_id = ?", tweetId).executeSingle();
        if (updatedTweet != null) {
            updatedTweet.delete();
        }
        updatedTweet.save();

    }

    public static List<Tweet> getAll() {
        // This is how you execute a query
        return new Select()
                .from(Tweet.class)
                .orderBy("tweet_id DESC")
                .execute();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uniqueId);
        dest.writeParcelable(this.user, 0);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeString(this.strCreatedAt);
        dest.writeValue(this.favorited);
        dest.writeParcelable(this.retweetedFrom, 0);
        dest.writeValue(this.retweeted);
        dest.writeInt(this.retweetCount);
        dest.writeInt(this.favouritesCount);
        dest.writeLong(this.retweetedUserId);
        dest.writeString(this.displayUrl);
        dest.writeString(this.actualUrl);
        dest.writeString(this.media_url);
        dest.writeInt(this.mediaWidth);
        dest.writeInt(this.mediaHeight);
        dest.writeString(this.mediaType);
        dest.writeString(this.videoUrl);
        dest.writeString(this.replied);
    }

    private Tweet(Parcel in) {
        this.body = in.readString();
        this.uniqueId = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.strCreatedAt = in.readString();
        this.favorited = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.retweetedFrom = in.readParcelable(User.class.getClassLoader());
        this.retweeted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.retweetCount = in.readInt();
        this.favouritesCount = in.readInt();
        this.retweetedUserId = in.readLong();
        this.displayUrl = in.readString();
        this.actualUrl = in.readString();
        this.media_url = in.readString();
        this.mediaWidth = in.readInt();
        this.mediaHeight = in.readInt();
        this.mediaType = in.readString();
        this.videoUrl = in.readString();
        this.replied = in.readString();
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
