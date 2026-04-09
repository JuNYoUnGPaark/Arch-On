package com.example.week01;  // 클래스 파일 위치

import androidx.appcompat.app.AppCompatActivity;  // MainActivity를 사용해서 화면을 만들기 위한 부모 클래스

import android.os.Bundle;  // 앱 상태나 임시 정보를 저장하는 클래스
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;  // 팝업
import android.content.DialogInterface;  // Dialog 버튼 이벤트

import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {  // 클래스 생성

    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextPhoneNumber;
    TextView textViewUserInfo;
    Button buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // 앱이 실행되면 가장 먼저 실행되는 함수
        super.onCreate(savedInstanceState);  // 부모 클래스의 create 실행
        setContentView(R.layout.activity_main);  // Java파일과 XML파일 연결

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        textViewUserInfo = findViewById(R.id.textViewUserInfo);
        buttonSignup = findViewById(R.id.buttonSignup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_launcher);
            getSupportActionBar().setTitle("Hello Android");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onSignupButtonClick(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();

        String userInfo = "아이디: " + username + "\n패스워드: " + password + "\n전화번호: " + phoneNumber;
//      textViewUserInfo.setText(userInfo);  // 1. setText
//      Toast.makeText(getApplicationContext(), userInfo, Toast.LENGTH_SHORT).show(); // 2. Toast
        Snackbar.make(view, "회원가입 완료", Snackbar.LENGTH_SHORT)  // 3. Snackbar
                .setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "확인 클릭", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
