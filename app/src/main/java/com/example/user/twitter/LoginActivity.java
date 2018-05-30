package com.example.user.twitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager = CallbackManager.Factory.create();
    LoginButton loginButton;
    Button customLogin;
    LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        customLogin = findViewById(R.id.custom_login);

        loginButton.setReadPermissions("public_profile");

        loginManager = LoginManager.getInstance();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn){
            getProfileAndProceed(accessToken);
        } else {
            FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getProfileAndProceed(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_SHORT).show();
                }
            };

            loginButton.registerCallback(callbackManager, facebookCallback);

            loginManager.registerCallback(callbackManager, facebookCallback);

            customLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> permissions = new ArrayList<>();
                    permissions.add("public_profile");
                    loginManager.logInWithReadPermissions(LoginActivity.this, permissions);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getProfileAndProceed(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            String name = (String) response.getJSONObject().get("name");
                            String id = (String) response.getJSONObject().get("id");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            intent.putExtra("user_name", name);
                            intent.putExtra("user_id", id);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name");
        parameters.putString("locale", "ko_KR");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
