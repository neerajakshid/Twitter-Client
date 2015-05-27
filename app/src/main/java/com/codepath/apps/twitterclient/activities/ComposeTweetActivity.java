package com.codepath.apps.twitterclient.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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

public class ComposeTweetActivity extends Activity {
    static Boolean  flag = false;
    TextView tvUserName, tvTextCount, tvScreenName;
    EditText etTweetText;
    ImageView ivUserProfile;
    TwitterClient client = TwitterApplication.getRestClient();
    MenuItem miTweet;
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showAsPopup(ComposeTweetActivity.this);
        if (flag == true) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setLogo(R.mipmap.ic_launcher);
            getActionBar().setDisplayUseLogoEnabled(true);

            setContentView(R.layout.compose_tweet_fragment);
            etTweetText = (EditText) findViewById(R.id.etTweetText);
            tvScreenName = (TextView) findViewById(R.id.tvComposeScreenName);
            tvUserName = (TextView) findViewById(R.id.tvComposeUsername);
            ivUserProfile = (ImageView) findViewById(R.id.ivUserProfile);
            Tweet replyToTweet = (Tweet)getIntent().getParcelableExtra("replyToTweet");
            String action = getIntent().getStringExtra("action");
            String text = "";
            // soft keyboard
            etTweetText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etTweetText, InputMethodManager.SHOW_IMPLICIT);
            if (replyToTweet!=null){
                if (action.equals(Constants.ACTION_REPLY) ) {
                    text = "@" + replyToTweet.getUser().getScreenName() + " ";
                } else if (action.equals(Constants.ACTION_RETWEET) ){
                    text =  replyToTweet.getBody() + " ";
                }
                etTweetText.setText(text);
                if(text.length() > 0) {
                    etTweetText.setSelection(etTweetText.getText().length());
                }
            }

            client.verifyAccountCredentials(new JsonHttpResponseHandler() {
                        // Success

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            currentUser = User.fromJson(response);
                            tvScreenName.setText("@" + currentUser.getScreenName());
                            tvUserName.setText(currentUser.getUserName());
                            ivUserProfile.setImageResource(android.R.color.transparent); // clear out the old image
                            // For Rounded image in profile picture
                            Transformation transformation = new RoundedTransformationBuilder()
                                    .borderColor(Color.BLACK)
                                    .borderWidthDp(1)
                                    .cornerRadiusDp(30)
                                    .oval(false)
                                    .build();
                            Picasso.with(ComposeTweetActivity.this).load(currentUser.getProfileImageUrl())
                                    .transform(transformation)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(ivUserProfile);

                    }

                        // failure

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG current user!!", throwable.toString());
                        }


            } );

            etTweetText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }


                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        textChanged();

                    }
                });
                textChanged();

            }
}
    public void textChanged() {
        if (tvTextCount == null || etTweetText.getText().toString() == null)
            return;
        final String tweetText = etTweetText.getText().toString();
        if (tweetText != null && tweetText.length() > 0) {
            int remainingChars = Constants.MAX_TWEET_COUNT - tweetText.length();
            tvTextCount.setText(Integer.toString(remainingChars) + "  ");
            if (remainingChars <= 0 || remainingChars == Constants.MAX_TWEET_COUNT) {
                miTweet.setEnabled(false);
            } else {
                miTweet.setEnabled(true);
            }
        } else {
            tvTextCount.setText("140" + "  ");
        }
    }

    public static boolean showAsPopup(Activity activity) {
        //To show activity as dialog and dim the background, you need to declare android:theme="@style/PopupTheme" on for the chosen activity on the manifest
        activity.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.height = 550; //fixed height
        params.width = 550; //fixed width
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        activity.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        flag = true;
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_tweet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.miTweet:
                if (!Utilities.isNetworkAvailable(this)) {
                    Utilities.showAlertDialog(ComposeTweetActivity.this, getBaseContext().getString(R.string.no_internet_error), false);
                } else {
                    // hide soft keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etTweetText.getWindowToken(), 0);
                    String tweetText = etTweetText.getText().toString();
                    if (tweetText != null && tweetText.length() > 0) {
                        client.saveNewTweet(etTweetText.getText().toString(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Tweet newTweet = Tweet.fromJSON(response);
                                newTweet.save();
                                Intent result = new Intent();
                                result.putExtra("newTweet", newTweet);
                                setResult(RESULT_OK, result);
                                finish();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.d("DEBUG", throwable.toString());
                                // Handle error
                                throwable.printStackTrace();
                            }
                        });
                    }

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem miCharacterCount = menu.findItem(R.id.miCharacterCount);
        miTweet = menu.findItem(R.id.miTweet);
        miCharacterCount.setEnabled(false);
        miTweet.setEnabled(false);
        tvTextCount = (TextView) miCharacterCount.getActionView();
        etTweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence text, int arg1, int arg2,
                                      int arg3) {
                textChanged();
            }
        });
        textChanged();
        return super.onPrepareOptionsMenu(menu);
    }
}
