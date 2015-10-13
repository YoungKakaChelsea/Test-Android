package com.example.youngkaka.testlogin;

import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    private TextView mName,mEmail;
    private ImageView mImage;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mName = (TextView) findViewById(R.id.mName);
        mEmail = (TextView) findViewById(R.id.mEmail);
        mImage = (ImageView) findViewById(R.id.mImage);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_google:
                return true;
            case R.id.action_twitter:
                startActivity(new Intent(this, TwitterMainActivity.class));
                return true;
            case R.id.action_facbook:
                startActivity(new Intent(this, FacebookMainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void login_click(View view) {
        onSignInClicked();
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();


    }

    public void logout_click(View view) {
        onSignOutClicked();
    }

    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        showSignedOutUI();
    }

    private void showSignedOutUI() {
        mName.setText("");
        mEmail.setText("");
        mImage.setImageResource(R.drawable.chelsea);
    }

    private void showSignedInUI() {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mShouldResolve = false;

        // Show the signed-in UI
        showSignedInUI();

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            mName.setText(personName);
            mEmail.setText(email);
            new DownloadImageTask(mImage).execute(personPhoto);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {

                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //  showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            showSignedOutUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }


}
