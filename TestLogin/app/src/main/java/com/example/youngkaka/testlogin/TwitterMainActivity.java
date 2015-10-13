package com.example.youngkaka.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;

/**
 * Created by YoungKaka on 9/26/2015.
 */
public class TwitterMainActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private static final String TWITTER_KEY = "awqpcad0EQXrWFMwpWO98Qerj";
    private static final String TWITTER_SECRET = "rqpdccrOVoy6POyvRtVNHlV29Ow7MqSUBWQYwMA7AaqHPWDzwD";

    private TextView mName,mEmail;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_twitter);
        mName = (TextView) findViewById(R.id.mName);
        mEmail = (TextView) findViewById(R.id.mEmail);
        mImage = (ImageView) findViewById(R.id.mImage);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new LoginHandler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_google:
                startActivity(new Intent(this,MainActivity.class));
                return true;
            case R.id.action_twitter:
                return true;
            case R.id.action_facbook:
                startActivity(new Intent(this, FacebookMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    private class LoginHandler extends Callback<TwitterSession> {
        @Override
        public void success(Result<TwitterSession> twitterSessionResult) {
//            long id = twitterSessionResult.data.getUserId();
//            String name = twitterSessionResult.data.getUserName();
//            mName.setText( id + " "  + name );
                   // twitterSessionResult.data.getAuthToken().token;

            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            twitterApiClient.getAccountService().verifyCredentials(false,false, new Callback<User>() {
                @Override
                public void success(Result<User> userResult) {
                    mEmail.setText( userResult.data.email);
                    String name = userResult.data.name;
                    String profileurl = userResult.data.profileImageUrl;
                      mName.setText(name);

                    new DownloadImageTask(mImage).execute(profileurl);
                }

                @Override
                public void failure(TwitterException e) {

                }
            });

            TwitterAuthClient authClient = new TwitterAuthClient();
            authClient.requestEmail(twitterSessionResult.data, new Callback<String>() {
                @Override
                public void success(Result<String> result) {
                    mEmail.setText(result.data);
                }

                @Override
                public void failure(TwitterException exception) {
                    T.s(TwitterMainActivity.this,"email failed");
                }
            });
        }

        @Override
        public void failure(TwitterException e) {
            T.s(TwitterMainActivity.this,"Login failed");
        }
    }
}
