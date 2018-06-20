package com.example.user.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

// MainActivity
// 로그인 화면의 기능을 정의하는 클래스
public class LoginActivity extends AppCompatActivity {

    // ---------------------- Facebook Login 관련 변수 -------------------------------

    // Facebook Login 요청의 응답을 처리하는 Callback 핸들러
    CallbackManager callbackManager = CallbackManager.Factory.create();

    // Facebook에서 제공하는 자체 로그인 버튼
    // 페이스북 로그인 기능이 내장되어있다.
    LoginButton loginButton;

    // -------------------------------------------------------------------------


    // =========================================================================
    // onCreate
    // 화면이 시작할 때 실행되는 함수
    // -------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 버튼을 레이아웃의 버튼 뷰와 연결
        loginButton = findViewById(R.id.login_button);

        // 로그인 버튼을 눌렀을 때 요청할 권한을 설정한다.
        loginButton.setReadPermissions("public_profile");

        // 로그인 요청이 완료되었을 때 처리할 기능 정의
        FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {

            // 결과가 성공이라면
            @Override
            public void onSuccess(LoginResult loginResult) {
                // 전달받은 loginResult의 AccessToken을 가지고 getProfileAndProceed 함수를 실행한다.
                getProfileAndProceed(loginResult.getAccessToken());
            }

            // 취소되었다면
            @Override
            public void onCancel() {

            }

            // 에러가 발생하였다면
            @Override
            public void onError(FacebookException error) {
                // 화면에 에러 메세지 출력
                Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_SHORT).show();
            }
        };

        // 로그인 버튼에 Callback을 다룰 callbackManager와, 방금 정의한 callback을 등록한다.
        // (loginButton을 눌러 로그인 요청 완료시 callbackManager가 facebookCallback을 실행한다.)
        loginButton.registerCallback(callbackManager, facebookCallback);

    }
    // -------------------------------------------------------------------------
    // onCreate 함수 종료
    // =========================================================================


    // =========================================================================
    // onActivityResult
    // 결과를 가지고 다시 돌아왔을 때 실행되는 함수
    // (loginButton에서 보낸 로그인 요청 완료와 이어짐)

    // 매개변수
    // requestCode : 창구 번호
    // resultCode : 결과의 성공(RESULT_OK), 실패(RESULT_CANCEL) 여부
    // data : 전달된 결과물 소포(Intent)
    // -------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 다른 서비스로 떠나는 요청이 facebook login 요청 뿐이므로, 전부 callbackManager가 대신 처리하도록 한다.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // =========================================================================
    // getProfileAndProceed
    // 토큰에서 프로필 정보를 추출하여 MainActivity로 전달하는 함수

    // 매개변수
    // accessToken accessToken : Facebook에서 인증받은 AccessToken
    // -------------------------------------------------------------------------
    private void getProfileAndProceed(AccessToken accessToken) {

        // 새 프로필 요청을 정의한다.

        // 매개 변수
        // accessToken : Facebook에서 인증받은 accessToken을 전달하여 프로필을 가져올 자격이 있음을 증명한다.
        // GraphJSONObjectCallback : 프로필 요청 완료시 실행할 callback을 전달한다.
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {

                    // 요청 완료시
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            // response에서 이름과 id를 추출한다.
                            String name = (String) response.getJSONObject().get("name");
                            String id = (String) response.getJSONObject().get("id");

                            // intent에 이름과 id를 넣고 MainActivity로 이동한다.
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user_name", name);
                            intent.putExtra("user_id", id);
                            startActivity(intent);

                            // 본 Activity는 종료한다.
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        // 요청할 프로필 정보를 담는 자료구조
        Bundle parameters = new Bundle();

        // field(정보 종류)에 id, name을 포함시킨다.
        parameters.putString("fields", "id, name");

        // 지역을 한국으로 설정하여 결과값이 한국어로 나오도록 한다.
        parameters.putString("locale", "ko_KR");

        // 위에서 정의한 request에 요청 정보를 설정한다.
        request.setParameters(parameters);

        // 요청을 실행한다.
        request.executeAsync();
    }
    // -------------------------------------------------------------------------
    // getProfileAndProceed 함수 종료
    // =========================================================================

}
