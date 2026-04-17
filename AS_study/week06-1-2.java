package com.example.week01;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnRotate, btnZoomIn, btnZoomOut, btnMoveRight, btnMoveLeft, btnTilt, btnReset;
    Button btnExit;
    Button btnRotateY, btnAlpha, btnAnimate;
    float currentRotation = 0f;
    float currentScale = 1.0f;
    float currentX = 0f;
    float currentY = 0f;
    float currentRotationX = 0f;
    float currentRotationY = 0f;
    int alphaState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        btnRotate = findViewById(R.id.btnRotate);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnMoveLeft = findViewById(R.id.btnMoveLeft);
        btnMoveRight = findViewById(R.id.btnMoveRight);
        btnTilt = findViewById(R.id.btnTilt);
        btnReset = findViewById(R.id.btnReset);
        btnExit = findViewById(R.id.btnExit);
        btnRotateY = findViewById(R.id.btnRotateY);
        btnAlpha = findViewById(R.id.btnAlpha);
        btnAnimate = findViewById(R.id.btnAnimate);

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRotation += 30f;
                imageView.setRotation(currentRotation);
            }
        });

        btnRotateY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRotationY += 30f;
                imageView.setRotation(currentRotationY);
            }
        });

        btnAlpha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (alphaState == 0) {
                    imageView.setAlpha(0.5f);
                    alphaState = 1;
                } else if (alphaState == 1) {
                    imageView.setAlpha(0f);
                    alphaState = 2;
                } else {
                    imageView.setAlpha(1.0f);
                    alphaState = 0;
                }
            }
        });

        btnAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.animate()
                        .rotation(currentRotation + 360f)
                        .setDuration(1000);
                currentRotation += 360f;
            }
        });

        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentScale += 0.2f;
                imageView.setScaleX(currentScale);
                imageView.setScaleY(currentScale);
            }
        });

        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentScale -= 0.2f;

                if (currentScale < 0.2f) {
                    currentScale = 0.2f;
                }
                imageView.setScaleX(currentScale);
                imageView.setScaleY(currentScale);
            }
        });

        btnMoveRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentX += 50f;
                imageView.setTranslationX(currentX);
            }
        });

        btnMoveLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentX -= 50f;
                imageView.setTranslationX(currentX);
            }
        });

        btnTilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRotationX += 15f;
                imageView.setRotationX(currentRotationX);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentRotation = 0f;
                currentScale = 1.0f;
                currentX = 0f;
                currentY = 0f;
                currentRotationX = 0f;
                currentRotationY = 0f;
                alphaState = 0;

                imageView.setRotation(currentRotation);
                imageView.setRotationX(currentRotationX);
                imageView.setTranslationX(currentX);
                imageView.setTranslationY(currentY);
                imageView.setScaleX(currentScale);
                imageView.setScaleY(currentScale);
                imageView.setRotationY(currentRotationY);
                imageView.setAlpha(1.0f);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });


    }
}
