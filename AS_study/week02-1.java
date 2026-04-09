package com.example.week01;  // 클래스 파일 위치

import androidx.appcompat.app.AppCompatActivity;  // MainActivity를 사용해서 화면을 만들기 위한 부모 클래스

import android.os.Bundle;  // 앱 상태나 임시 정보를 저장하는 클래스
import android.net.Uri;
import android.content.Intent;
import android.view.View;

public class MainActivity extends AppCompatActivity {  // 클래스 생성
    @Override
    protected void onCreate(Bundle savedInstanceState) {  // 앱이 실행되면 가장 먼저 실행되는 함수
        super.onCreate(savedInstanceState);  // 부모 클래스의 create 실행
        setContentView(R.layout.activity_main);  // Java파일과 XML파일 연결
    }

    public void onClickednaver(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com"));
        startActivity(intent);
    }

    Uri location = Uri.parse("geo:37.5665,126.9780");
    public void onClickedmap(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(intent);
    }

    public void onClickedemail(View v) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:example@google.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "모바일 프로그래밍 실습");
        intent.putExtra(Intent.EXTRA_TEXT, "안녕하세요, 테스트 메일입니다.");
        startActivity(intent);
    }
}
