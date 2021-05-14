package com.example.travelplanner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final int PERMISSION_REQUEST_CAMERA = 1;
    private static final String TAG = "Van QRScanCode";
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!haveCameraPermission()) {
            System.out.println("I'm trying to connect to my camera T_T");
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            System.out.println("connected^^");
            mScannerView = new ZXingScannerView(this);
            //For HUAWEI phone
            mScannerView.setAspectTolerance(0.5f);
            //mScannerView = (ZBarScannerView) findViewById(R.id.zbar);
            //setContentView(R.layout.activity_scan);
            setContentView(mScannerView);
//        mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
    }

    private boolean haveCameraPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // This is because the dialog was cancelled when we recreated the activity.
        if (permissions.length == 0 || grantResults.length == 0)
            return;

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView.startCamera();
                } else {
                    finish();
                }
            }
            break;
        }
    }

    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


    @Override
    public void handleResult(Result rawResult) {
        if (rawResult != null) {
            System.out.println(rawResult.getText());
            Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();
            Log.v(TAG,rawResult.getText());
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("Key", rawResult.getText());
            startActivity(intent);
            finish();
        }
        else{
            //Wait 2 seconds to resume the preview.
            // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScannerView.resumeCameraPreview(ScanActivity.this);
                }
            }, 2000);

            return;
        }
    }
}