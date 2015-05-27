package com.codepath.apps.twitterclient.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.apps.TwitterApplication;
import com.codepath.apps.twitterclient.helpers.Constants;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.net.TwitterClient;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.Header;
import org.json.JSONObject;


public class ComposeTweetFragment extends DialogFragment {
    TextView tvUserName, tvTextCount, tvScreenName;
    EditText etTweetText;
    ImageView ivUserProfile;
    TwitterClient client = TwitterApplication.getRestClient();
    View view;
    Button bTweet;

    public ComposeTweetFragment() {

    }

    public interface ComposeTweetFragmentListener {
        void onFinishComposeTweet(Tweet tweet);
    }

    public static ComposeTweetFragment newInstance(User user) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable("currentUser", user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.show();

// Set title divider color
        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

        view = inflater.inflate(R.layout.compose_tweet_fragment, container);
        etTweetText = (EditText) view.findViewById(R.id.etTweetText);
        tvScreenName = (TextView) view.findViewById(R.id.tvComposeScreenName);
        tvUserName = (TextView) view.findViewById(R.id.tvComposeUsername);
        ivUserProfile = (ImageView) view.findViewById(R.id.ivUserProfile);

        // Show soft keyboard automatically
    /*    etTweetText.requestFocus();
        etTweetText.requestFocusFromTouch();*/

        // GET currentUser values from Parcelable
        User currentUser = getArguments().getParcelable("currentUser");
        if (currentUser != null) {
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
            Picasso.with(getActivity()).load(currentUser.getProfileImageUrl())
                    .transform(transformation)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(ivUserProfile);
        }
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
        return view;
    }

    public void textChanged() {
        if (tvTextCount == null || etTweetText == null)
            return;
        final String tweetText = etTweetText.getText().toString();
        if (tweetText != null && tweetText.length() > 0) {
            int remainingChars = Constants.MAX_TWEET_COUNT - tweetText.length();
            tvTextCount.setText(Integer.toString(remainingChars) + "  ");
            if (remainingChars <= 0 || remainingChars == Constants.MAX_TWEET_COUNT) {
                bTweet.setEnabled(false);
            } else {
                bTweet.setEnabled(true);
            }
        } else {
            tvTextCount.setText("140" + "  ");
        }
        bTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.saveNewTweet(etTweetText.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tweet newTweet = Tweet.fromJSON(response);
                        ComposeTweetFragmentListener listener = (ComposeTweetFragmentListener) getActivity();
                        listener.onFinishComposeTweet(newTweet);
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", throwable.toString());
                        // Handle error
                    }

                });
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_compose_tweet, menu);
    }
}
