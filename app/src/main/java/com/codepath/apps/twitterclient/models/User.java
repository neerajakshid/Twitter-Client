package com.codepath.apps.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Users")
public class User extends Model implements Parcelable {
    @Column(name = "user_name")
    public String userName;
    @Column(name = "userId", unique = true)
    public long userId;
    @Column(name = "screen_name")
    public String screenName;
    @Column(name = "profile_image_url")
    public String profileImageUrl;


    public static User fromJson(JSONObject json) {
        User user = new User();
        try {
            if (json.getString("name") != null)
                user.userName = json.getString("name");
            else
                user.userName = "";
            if (json.getString("id") != null)
                user.userId = json.getLong("id");
            else
                user.userId = 0;
            if (json.getString("screen_name") != null)
                user.screenName = json.getString("screen_name");
            else
                user.screenName = "";
            if (json.getString("profile_image_url") != null)
                user.profileImageUrl = json.getString("profile_image_url");
            else
                user.profileImageUrl = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User findOrCreateUserFromJSON(JSONObject json) {
        User user = null;
        try {
            long unique = json.getLong("id");
            user = new Select().from(User.class).where("userId = ?", unique).executeSingle();
            if (user == null) {
                user = User.fromJson(json);
                user.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }


    //implementing Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeLong(this.userId);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
    }

    public User() {
        super();
    }


    private User(Parcel in) {
        this.userName = in.readString();
        this.userId = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}




