package com.example.week01;  // 클래스 파일 위치

import androidx.appcompat.app.AppCompatActivity;  // MainActivity를 사용해서 화면을 만들기 위한 부모 클래스

import android.os.Bundle;  // 앱 상태나 임시 정보를 저장하는 클래스
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {  // 클래스 생성

    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // 앱이 실행되면 가장 먼저 실행되는 함수
        super.onCreate(savedInstanceState);  // 부모 클래스의 create 실행
        setContentView(R.layout.activity_main);  // Java파일과 XML파일 연결

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setTitle("Hello Android");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(view, "버튼을 클릭 했어요", Snackbar.LENGTH_LONG)
                        .setAction("되돌리기", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "복구 완료", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
