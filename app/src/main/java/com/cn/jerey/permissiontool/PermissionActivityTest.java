package com.cn.jerey.permissiontool;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.cn.jerey.permissiontools.PermissionActivity;

import java.util.List;

/**
 * Created by xiamin on 3/15/17.
 */

public class PermissionActivityTest extends PermissionActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requesetPermissions(Manifest.permission.SEND_RESPOND_VIA_MESSAGE, Manifest.permission.CAMERA);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(PermissionActivityTest.this,"权限申请通过" + perms,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(PermissionActivityTest.this,"权限申请失败" + perms,Toast.LENGTH_SHORT).show();
    }
}
