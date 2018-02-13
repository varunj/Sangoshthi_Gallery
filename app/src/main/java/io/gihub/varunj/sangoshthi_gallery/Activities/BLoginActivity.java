package io.gihub.varunj.sangoshthi_gallery.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.gihub.varunj.sangoshthi_gallery.R;

/**
 * Created by Varun on 04-03-2017.
 */

public class BLoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    EditText editTextName, editTextPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, CMainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_login);
            findViewById(R.id.sign_in_button).setOnClickListener(this);
            editTextName = (EditText)findViewById(R.id.name);
            editTextPhone = (EditText)findViewById(R.id.phone);

            // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button: signIn();break;
        }
    }

    private void signIn() {

        // fetch list of phone numbers authorised to access
        String pathAccessFile = Environment.getExternalStorageDirectory().getAbsolutePath() +  getString(R.string.dropbox_access_path);
        int flag = 0;
        File directoryAccess = new File(pathAccessFile);
        try {
            BufferedReader br = new BufferedReader(new FileReader(directoryAccess));
            String line;
            String[] splitt;
            while ((line = br.readLine()) != null) {
                splitt = line.split(",");
                if (splitt[0].equals(editTextPhone.getText().toString())) {
                    flag = 1;
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
            br.close();
        }
        catch (IOException e) {
            Toast.makeText(BLoginActivity.this, getString(R.string.toast_unauth_phoneNum), Toast.LENGTH_LONG).show();
        }
        if (flag == 0) {
            Toast.makeText(BLoginActivity.this, getString(R.string.toast_unauth_phoneNum), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(editTextName.getText().toString().length() >= Integer.parseInt(getString(R.string.register_minLen_name))) {
            if(editTextPhone.getText().toString().length() == Integer.parseInt(getString(R.string.register_len_phoneNum))) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("name", editTextName.getText().toString());
                editor.putString("phoneNum", editTextPhone.getText().toString());
                editor.putBoolean("isLoggedIn" , true);
                editor.commit();
                Intent intent = new Intent(this, CMainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(BLoginActivity.this, getString(R.string.toast_wrong_phoneNum), Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(BLoginActivity.this, getString(R.string.toast_wrong_name), Toast.LENGTH_LONG).show();
        }

        // Signed in successfully.
        if (result.isSuccess()) {
            if(editTextName.getText().toString().length() >= Integer.parseInt(getString(R.string.register_minLen_name))) {
                if(editTextPhone.getText().toString().length() == Integer.parseInt(getString(R.string.register_len_phoneNum))) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("name", editTextName.getText().toString());
                    editor.putString("phoneNum", editTextPhone.getText().toString());
                    editor.putBoolean("isLoggedIn" , true);
                    editor.putString("googleEmail", acct.getEmail());
                    editor.commit();
                    Intent intent = new Intent(this, CMainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(BLoginActivity.this, getString(R.string.toast_wrong_phoneNum), Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(BLoginActivity.this, getString(R.string.toast_wrong_name), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
