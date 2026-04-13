package com.example.week01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;


public class MainActivity extends AppCompatActivity {

    EditText etAmericano;
    EditText etLatte;
    EditText etCake;
    EditText etSandwich;
    TextView txtResult;
    Button btnOrder;
    Button btnReset;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOrder = findViewById(R.id.btnOrder);
        btnReset = findViewById(R.id.btnReset);
        etAmericano = findViewById(R.id.etAmericano);
        etLatte = findViewById(R.id.etLatte);
        etCake = findViewById(R.id.etCake);
        etSandwich = findViewById(R.id.etSandwich);
        txtResult = findViewById(R.id.txtResult);

        btnOrder.setOnClickListener(v -> orderMenu());
        btnReset.setOnClickListener(v -> resetMenu());
    }

    public int getNumber1() {
        String s1 = etAmericano.getText().toString().trim();
        if (s1.equals("")) return 0;
        return Integer.parseInt(s1);
    }

    public int getNumber2() {
        String s2 = etLatte.getText().toString().trim();
        if (s2.equals("")) return 0;
        return Integer.parseInt(s2);
    }

    public int getNumber3() {
        String s3 = etCake.getText().toString().trim();
        if (s3.equals("")) return 0;
        return Integer.parseInt(s3);
    }

    public int getNumber4() {
        String s4 = etSandwich.getText().toString().trim();
        if (s4.equals("")) return 0;
        return Integer.parseInt(s4);
    }

    public void orderMenu() {
        int americano = getNumber1();
        int latte = getNumber2();
        int cake = getNumber3();
        int sandwich = getNumber4();

        if (americano == 0 && latte == 0 && cake == 0 && sandwich == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("최소 1개 이상의 메뉴를 주문해 주세요.")
                    .setPositiveButton("확인", null)
                    .show();
            return;
        }

        total = americano * 3000 + latte * 4000 + cake * 5500 + sandwich * 4500;

        String result = "[주문 내역]\n"
                + "아메리카노 " + americano + "잔 = " + (americano * 3000) + "원\n"
                + "카페라떼 " + latte + "잔 = " + (latte * 4000) + "원\n"
                + "케이크 " + cake + "개 = " + (cake * 5500) + "원\n"
                + "샌드위치 " + sandwich + "개 = " + (sandwich * 4500) + "원\n\n"
                + "==========\n"
                + "총 결제 금액: " + total + "원";

        txtResult.setText(result);
    }

    public void resetMenu() {
        etAmericano.setText("");
        etLatte.setText("");
        etCake.setText("");
        etSandwich.setText("");

        total = 0;
        txtResult.setText("아직 주문 내역이 없습니다.");
    }
}