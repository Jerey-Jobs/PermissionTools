package com.cn.jerey.permissiontool;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.cn.jerey.permissiontools.Callback.PermissionCallbacks;
import com.cn.jerey.permissiontools.PermissionTools;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{
    PermissionTools permissionTools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                permissionTools =  new PermissionTools.Builder(this)
                        .setOnPermissionCallbacks(new PermissionCallbacks() {
                            @Override
                            public void onPermissionsGranted(int requestCode, List<String> perms) {
                                Toast.makeText(MainActivity.this,"权限申请通过",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionsDenied(int requestCode, List<String> perms) {
                                Toast.makeText(MainActivity.this,"权限申请被拒绝",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setRequestCode(111)
                        .build();
                permissionTools.requestPermissions(Manifest.permission.CAMERA);

                break;
            case R.id.button2:
                permissionTools =  new PermissionTools.Builder(this)
                        .setOnPermissionCallbacks(new PermissionCallbacks() {
                            @Override
                            public void onPermissionsGranted(int requestCode, List<String> perms) {
                                Toast.makeText(MainActivity.this,"权限申请通过" + perms,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionsDenied(int requestCode, List<String> perms) {
                                Toast.makeText(MainActivity.this,"权限申请被拒绝" + perms,Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setRequestCode(111)
                        .build();
                permissionTools.requestPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionTools.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
