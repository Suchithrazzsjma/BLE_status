package com.enginecal.kioskapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {



    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    proceedWithAppLaunch();
                } else {
                    // Permission denied, handle it (show a rationale if needed)
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void proceedWithAppLaunch(){
            Intent intent=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);

    }

    public static String[] storage_permissions = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.MODIFY_PHONE_STATE,
            Manifest.permission.WRITE_SETTINGS

    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static String[] storage_permissions_33 = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.MODIFY_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.DISABLE_KEYGUARD,
            Manifest.permission.USE_BIOMETRIC


    };
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("val","new");
            p = storage_permissions_33;
        } else {
            Log.d("val","old");
            p = storage_permissions;
        }
        return p;
    }

    private void requestPermissions(){
        //Permission checks for location
        try {
            ActivityCompat.requestPermissions(SplashActivity.this, permissions(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}