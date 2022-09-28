package com.lucho.rs_podcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final Timer timer = new Timer();
    private Boolean permOk=false;
    private Intent intent;
    private final static int REQUEST_CODE_ASK_PERMISSIONS=1;
    private static final String[] REQUIRED_SDK_PERMISSIONS=new String[]{
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(this.getApplication(), PodcastService.class);
        checkPermissions();
        timer.schedule(timerTask, 0,500);
    }

    final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(permOk) {
                startService(intent);
                Intent intentExit = new Intent(Intent.ACTION_MAIN);
                intentExit.addCategory(Intent.CATEGORY_HOME);
                startActivity(intentExit);
                timer.cancel();
            }
        }
    };

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        for (final String permission:REQUIRED_SDK_PERMISSIONS) {
            final int result= ContextCompat.checkSelfPermission(this, permission);
            if(result!= PackageManager.PERMISSION_GRANTED) missingPermissions.add(permission);
        }
        if (!missingPermissions.isEmpty()) {
            final String[] permissions = missingPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS, grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE_ASK_PERMISSIONS){
            for (int index = permissions.length - 1; index >= 0; --index) {
                if (grantResults[index]!=PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Required permission '" + permissions[index] + "' not granted, exiting", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            permOk=true;
        }
    }
}