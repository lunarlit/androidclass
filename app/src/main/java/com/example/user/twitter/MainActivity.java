package com.example.user.twitter;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// MainActivity
// 메인 화면의 기능을 정의하는 클래스
public class MainActivity extends AppCompatActivity {

    // ------------------------ 레이아웃 관련 변수 -----------------------------
    // 화면에서 컨트롤할 필요가 있는 요소들을 할당하기 위해 변수를 만들어둔다.

    // 트윗 추가 버튼
    FloatingActionButton send;

    // 트윗 리스트뷰
    ListView twitListView;

    // 리스트뷰에 들어갈 내용을 만들어서 전달할 어댑터를 담을 변수
    TwitAdapter twitAdapter;
    // -------------------------------------------------------------------------



    // ---------------------- Firebase 관련 변수 -------------------------------
    // Firebase를 JAVA 코드로 컨트롤하기 위해 변수를 만들어둔다.
    // Firebase Firestore의 컨트롤 권한을 할당하여 사용하기 위한 변수
    FirebaseFirestore db;

    // Firestore 데이터베이스 중 twits 컬렉션을 컨트롤하기 위한 변수
    CollectionReference twitRef;
    CollectionReference mentionRef;
    // -------------------------------------------------------------------------

    String userName;
    String userId;

    // =========================================================================
    // onCreate
    // 화면이 시작할 때 실행되는 함수
    // -------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userName = getIntent().getStringExtra("user_name");
        userId = getIntent().getStringExtra("user_id");

        // --------------------- Firebase 변수 세팅 --------------------------------
        // Firestore와 컬렉션을 준비해둔 변수에 연결한다.
        // Firebase Firestore 컨트롤 권한을 db에 할당
        db = FirebaseFirestore.getInstance();

        // 위의의 db 변수를통해 Firestore의 twits 컬렉션 권한을 가져와 twitRef에 할당
        twitRef = db.collection("twits");
        mentionRef = db.collection("mentions");
        // -------------------------------------------------------------------------


        // ------------------------- 화면 요소 세팅 --------------------------------
        // 화면 요소들을 변수에 연결한다.
        // activity_main.xml을 이 화면의 레이아웃으로 연결
        setContentView(R.layout.activity_main);

        // 레이아웃에 정의한 버튼과 리스트뷰 id를 변수에 연결
        send = findViewById(R.id.send);
        twitListView = findViewById(R.id.twitList);

        // 어댑터를 생성하여 준비해둔 변수에 연결한다.
        twitAdapter = new TwitAdapter(userId);

        // 리스트뷰에 내용을 책임질 어댑터를 연결한다.
        twitListView.setAdapter(twitAdapter);
        // -------------------------------------------------------------------------


        // ------------------------- 작동 기능 정의 --------------------------------
        // send (새 트윗 버튼)를 눌렀을 때 실행될 기능 정의
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 새로운 Intent (화면 간에 전달할 소포)를 만들어 출발지, 도착지를 입력한다.
                Intent intent = new Intent(MainActivity.this, NewTwitActivity.class);

                // Intent에 써있는대로 화면을 이동한다.
                // 이동한 화면에서 새 트윗 내용을 결과로 받아 다시 돌아올 것이기 때문에
                // 단순히 이동만 하는 startActivity가 아닌 startActivityForResult를 실행한다.
                // requestCode는 결과를 받을 창구 번호이다.
                startActivityForResult(intent, 100);

            }
        });

        // Firestore의 twits 컬렉션에 변경이 있을 때 실행될 기능 정의
        // .orderBy() 절을 추가하여 데이터를 timestamp 기준으로 정렬해서 가져오도록 한다.

        twitRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                // 컬렉션의 모든 Document(문서)를 반복한다.
                // 이 때 queryDocumentSnapshots를 그대로 사용하면 새로 추가된 게 아닌 트윗도 계속 반복 추가되므로
                // .getDocumentChanges를 호출하여 변경된 내역이 있는 데이터만 뽑아내 반복한다.
                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){

                    Twit twit = doc.getDocument().toObject(Twit.class);
                    twit.id = doc.getDocument().getId();

                    // 변경 내역이 ADDED(새로 추가됨) 라면
                    if(doc.getType() == DocumentChange.Type.ADDED){

                        // 변경 내역을 다시 문서로 바꾼 후(getDocument) Twit 객체로 바꾸어(toObject)
                        // twit이라는 변수에 할당한다.

                        // 그 Twit 객체 데이터를 추가한다.
                        twitAdapter.addItem(twit);

                        // 이 과정의 타입 변화가 익숙하다면 twit 변수를 생략하고 아래와 같이 축약하여 쓸 수 있다.
                        /*  twitAdapter.addItem(doc.getDocument().toObject(Twit.class));  */
                    } else if(doc.getType() == DocumentChange.Type.MODIFIED){
                        twitAdapter.replaceItem(twit);
                    }
                }
            }
        });
        // -------------------------------------------------------------------------

    }
    // -------------------------------------------------------------------------
    // onCreate 함수 종료
    // =========================================================================



    // =========================================================================
    // onActivityResult
    // startActivityForResult 함수를 이용해 떠났던 화면으로
    // 결과를 가지고 다시 돌아왔을 때 실행되는 함수
    // (onCreate -> send.setOnClickListener -> startActivityForResult와 이어짐)

    // 매개변수
    // requestCode : startActivityForResult에서 정의한 창구 번호
    // resultCode : 결과의 성공(RESULT_OK), 실패(RESULT_CANCEL) 여부
    // data : 전달된 결과물 소포(Intent)
    // -------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 창구번호 100에서 새 트윗 내용을 얻으러 출발하였으므로 다시 100으로 돌아온다.
        // 한 화면에서 갔다올 수 있는 화면이 여러 개일 때 구분하기 위해 사용
        // (현재는 하나뿐이므로 의미는 없음)
        if(requestCode == 100) {

            // 결과가 성공이라면
            // : 새 트윗 작성 화면에서 트윗 버튼을 누르면 성공
            // : X (닫기) 버튼을 누르면 실패로 정의
            if (resultCode == RESULT_OK) {

                // 결과 소포에서 message 라는 이름표가 붙은 데이터를 꺼내 msg 변수에 할당한다.
                // String 타입이므로 getStringExtra를 사용한다.
                String msg = data.getStringExtra("message");

                // --------- 내용을 얻었으므로 Firestore에 새로 데이터를 입력한다. ----------
                // 입력하기 위해서 HashMap<String, Object> 타입의 변수를 새로 만들어 채워 넣어야 한다.
                HashMap<String, Object> twit = new HashMap<>();

                // 데이터 형태는 .put() 함수에 "이름", "값"으로 집어넣는다.
                // 이름은 Twit 클래스에 정의된 멤버 변수명과 같아야 가져올 때 자동 변환 시킬 수 있다.

                twit.put("writer", userName);
                twit.put("message", msg);

                if(userId != null)
                    twit.put("picId", userId);

                // new Date()를 이용해 이 코드가 실행되는 시점의 시간을 입력한다.
                twit.put("timestamp", new Date());

                twit.put("likes", new HashMap<String, Boolean>());

                // twits 컬렉션에 이 데이터를 추가한다.
                twitRef.add(twit);
                // --------------------------------------------------------------------------
            }
        } else if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                String msg = data.getStringExtra("message");
                String mentionOn = data.getStringExtra("mention_on");

                HashMap<String, Object> mention = new HashMap<>();

                mention.put("writer", userName);
                mention.put("message", msg);
                mention.put("mentionOn", mentionOn);

                if(userId != null)
                    mention.put("picId", userId);

                mention.put("timestamp", new Date());

                mentionRef.add(mention);

            }
        }
    }
    // -------------------------------------------------------------------------
    // onActivityResult 함수 종료
    // =========================================================================

    public void likeUnlike(String twitId, Map<String, Boolean> likes) {

        if(likes == null) {
            likes = new HashMap<>();
        }

        if(likes.get(userId) != null){
            likes.remove(userId);
        } else {
            likes.put(userId, true);
        }
        twitRef.document(twitId).update("likes", likes);
    }
}
// MainActivity 종료