package com.example.week01;  // 클래스 파일 위치

import androidx.appcompat.app.AppCompatActivity;  // MainActivity를 사용해서 화면을 만들기 위한 부모 클래스

import android.os.Bundle;  // 앱 상태나 임시 정보를 저장하는 클래스
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {  // 클래스 생성
    Button btnGreet;
    TextView resultText;
    EditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // 앱이 실행되면 가장 먼저 실행되는 함수
        super.onCreate(savedInstanceState);  // 부모 클래스의 create 실행
        setContentView(R.layout.activity_main);  // Java파일과 XML파일 연결

        btnGreet = findViewById(R.id.button);
        resultText = findViewById(R.id.textView);
        nameInput = findViewById(R.id.editTextText);

        btnGreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String food = nameInput.getText().toString().trim();
                String message;

                if (food.equals("떡볶이")) {
                    message = food + "요? 매콤한 걸 좋아하시는군요!";
                } else if (food.equals("치킨")) {
                    message = food + "요?" + food + "는 언제 먹어도 옳죠!";
                } else {
                    message = food + "도 맛있죠!";
                }
                resultText.setText(message);
            }
        });
    }
}
