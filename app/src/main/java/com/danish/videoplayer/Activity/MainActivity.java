package com.danish.videoplayer.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.danish.videoplayer.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets)->{
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        runTimeRequest();
    }

    public void runTimeRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO};
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 1);
            }
        } else {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 2);
        }
    }

    /*
    In android 13+ don't ask again option is not available
    after 2-3 normal System automatically understand that user want "Don't ask again"
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 || requestCode == 2) {
            for (int i = 0; i < permissions.length; i++) {
                // CORRECT WAY: Check grantResults[i] instead of just i
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    boolean showRationale = shouldShowRequestPermissionRationale(permissions[i]);

                    if (!showRationale) {
                        // User selected "Never ask again"
                        new AlertDialog.Builder(this)
                                .setTitle("Permission Required")
                                .setMessage("To play Video permission is required\n\n" +
                                        "Settings > Apps > [Your App] > enable permission")
                                .setPositiveButton("Settings", (d, w)->{
                                    // Open app settings
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        // Normal denial - ask again after explaining
                        new AlertDialog.Builder(this)
                                .setTitle("Permission Needed")
                                .setMessage("App ke saare features use karne ke liye ye permission chahiye")
                                .setPositiveButton("Try Again", (d, w)->runTimeRequest())
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                }
            }
        }
    }
}