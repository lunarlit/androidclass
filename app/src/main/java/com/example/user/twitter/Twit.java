package com.example.user.twitter;

import java.util.Date;
import java.util.Map;

// Twit
// 트윗을 정의하기 위한 클래스
public class Twit {

    // 트윗이 갖는 데이터들 (작성자 이름, 내용, 작성 시간)을 담을 변수를 준비해둔다.
    public String writer;
    public String message;
    public String picId;
    public Date timestamp;
    public String id;
    public Map<String, Boolean> likes;
    public String imageName;

    // Firestore에 연동하려면 빈 생성자가 필수이다.
    Twit() {}

}
