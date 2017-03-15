package com.cn.jerey.permissiontools;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.cn.jerey.permissiontools.Callback.PermissionCallbacks;

/**
 * Created by xiamin on 3/15/17.
 */

public abstract class PermissionActivity extends AppCompatActivity implements PermissionCallbacks {
    private int RESQUEST_CODE = 99;
    PermissionTools permissionTools;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionTools = new PermissionTools.Builder(this)
                .setRequestCode(RESQUEST_CODE)
                .setOnPermissionCallbacks(this)
                .build();
    }

    protected void requesetPermissions(String... permissions) {
        permissionTools.requestPermissions(permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionTools.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
