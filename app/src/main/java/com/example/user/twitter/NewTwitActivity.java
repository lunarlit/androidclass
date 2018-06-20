package com.example.user.twitter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// NewTwitActivity
// 트윗 작성 화면의 기능을 정의하는 클래스
public class NewTwitActivity extends AppCompatActivity {

    // ------------------------ 레이아웃 관련 변수 -----------------------------
    // 화면에서 컨트롤할 필요가 있는 요소들을 할당하기 위해 변수를 만들어둔다.

    // 종료(취소) 버튼
    Button close;

    // 새 트윗 보내기 버튼
    Button newTwit;

    Button addPic;

    // 새 트윗 입력 텍스트창
    EditText message;
    ImageView image;

    Uri imageUri;
    // -------------------------------------------------------------------------

    // =========================================================================
    // onCreate
    // 화면이 시작할 때 실행되는 함수
    // -------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ------------------------- 화면 요소 세팅 --------------------------------
        // 화면 요소들을 변수에 연결한다.
        // activity_new_twit.xml을 이 화면의 레이아웃으로 연결
        setContentView(R.layout.activity_new_twit);

        // 레이아웃에 정의한 버튼과 텍스트 편집창 id를 변수에 연결
        close = findViewById(R.id.close);
        newTwit = findViewById(R.id.new_twit);
        message = findViewById(R.id.message);
        addPic = findViewById(R.id.add_picture);
        image = findViewById(R.id.image);
        // -------------------------------------------------------------------------

        final int requestCode = getIntent().getIntExtra("request_code", 100);

        // ------------------------- 작동 기능 정의 --------------------------------
        // newTwit (새 트윗 보내기 버튼)를 눌렀을 때 실행될 기능 정의
        newTwit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 새로운 Intent (화면 간에 전달할 소포)를 만든다.
                // 출발지, 도착지는 입력하지 않는다.
                // (startActivityForResult의 결과를 돌려주는 것이므로 소포에 내용을 담아 반송하는 것)
                // (반송할 때는 출발지, 도착지를 입력하지 않아도 되는 것과 마찬가지)
                Intent intent = new Intent();

                // 새 트윗 텍스트창의 내용을 가져와 msg 변수에 담는다.
                String msg = message.getText().toString();

                // 소포에 message라는 이름을 붙여 내용 데이터를 담는다.
                intent.putExtra("message", msg);

                if(imageUri != null)
                    intent.putExtra("image_uri", imageUri.toString());

                // 결과를 성공 (내용을 정상적으로 담았음)으로 설정하고 소포를 부친다.
                setResult(RESULT_OK, intent);

                // 화면을 끝낸다.
                finish();
            }
        });

        // close (X 버튼)를 눌렀을 때 실행될 기능 정의
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 새로운 Intent (화면 간에 전달할 소포)를 만든다.
                Intent intent = new Intent();

                // 결과를 실패 (내용을 담지 않고 종료)으로 설정하고 소포를 부친다.
                setResult(RESULT_CANCELED, intent);

                // 화면을 끝낸다.
                finish();
            }
        });

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
            }
        });

        // -------------------------------------------------------------------------
    }
    // -------------------------------------------------------------------------
    // onCreate 함수 종료
    // =========================================================================


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if(resultCode == RESULT_OK && data != null) {
                Log.d("NewTwitActivity", "image arrived");
                Uri selectedImage = data.getData();

                GlideApp.with(getApplicationContext()).load(selectedImage).into(image);

                imageUri = selectedImage;

            }
        }
    }
}
// NewTwitActivity 종료