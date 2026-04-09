package com.example.week01;  // 클래스 파일 위치

import androidx.appcompat.app.AppCompatActivity;  // MainActivity를 사용해서 화면을 만들기 위한 부모 클래스

import android.os.Bundle;  // 앱 상태나 임시 정보를 저장하는 클래스
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;  // 팝업
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {  // 클래스 생성
    EditText edit1, edit2, edit3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // 앱이 실행되면 가장 먼저 실행되는 함수
        super.onCreate(savedInstanceState);  // 부모 클래스의 create 실행
        setContentView(R.layout.activity_main);  // Java파일과 XML파일 연결

        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
        edit3 = (EditText) findViewById(R.id.edit3);
    }

    public void cal_plus(View e) {
        String s1 = edit1.getText().toString();
        String s2 = edit2.getText().toString();
        int result = Integer.parseInt(s1) + Integer.parseInt(s2);
//      edit3.setText("" + result);
        // 다이얼로그로도 가능
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("결과");
        builder.setMessage("입력값: " + s1 + "+" + s2 + "=" + result + "입니다.");
        builder.setPositiveButton("확인", null);
        builder.show();
    }
    public void cal_sub(View e) {
        String s1 = edit1.getText().toString();
        String s2 = edit2.getText().toString();
        int result = Integer.parseInt(s1) - Integer.parseInt(s2);
        edit3.setText("" + result);
    }

    public void cal_mul(View e) {
        String s1 = edit1.getText().toString();
        String s2 = edit2.getText().toString();
        int result = Integer.parseInt(s1) * Integer.parseInt(s2);
        edit3.setText("" + result);
    }

    public void cal_div(View e) {
        String s1 = edit1.getText().toString();
        String s2 = edit2.getText().toString();
        int result = Integer.parseInt(s1) / Integer.parseInt(s2);
        edit3.setText("" + result);
    }

    public void cal_Square(View e) {
        String s1 = edit1.getText().toString();
        if (!s1.equals("")) {
            int num1 = Integer.parseInt(s1);
            int result = num1 * num1;
            edit3.setText("" + result);
        }
    }
    public void cal_SquareRoot(View e) {
        String s1 = edit1.getText().toString();
        if (!s1.equals("")) {
            double num1 = Double.parseDouble(s1);
            double result = Math.sqrt(num1);
            edit3.setText("" + result);
        }
    }
    public void cal_Percentage(View e) {
        String s1 = edit1.getText().toString();
        String s2 = edit2.getText().toString();
        if (!s1.equals("") && !s2.equals("")) {
            double num1 = Double.parseDouble(s1);
            double num2 = Double.parseDouble(s2);
            double result = (num2 / num1) * 100;
            edit3.setText("" + result + "%");
        }
    }
    public void cal_Erase(View e) {
        edit1.setText("");
        edit2.setText("");
        edit3.setText("");
    }
}
