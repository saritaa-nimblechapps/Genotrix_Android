package com.mystrica.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.mystrica.R;


public class CodeActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    private ImageView imgCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        imgCamera=findViewById(R.id.img_cange_camera);
        mCodeScanner.setCamera(CodeScanner.CAMERA_FRONT);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        CoverActivity.strUserId =result.getText();

                        Intent dashboard=new Intent(getApplicationContext(),CoverActivity.class);
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dashboard);
                        finish();

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });


        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentvalue=mCodeScanner.getCamera();
//                Log.i("camera","Camera value:"+mCodeScanner.getCamera());

                //camera status get
                if(currentvalue==1){
                    mCodeScanner.setCamera(0);

                }else {
                    mCodeScanner.setCamera(1);

                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoverActivity.strUserId="";
    }
}