package com.cn.jerey.permissiontools.Callback;

import java.util.List;

/**
 * Created by Xiamin on 2017/1/24.
 */

public interface PermissionCallbacks {
    /**
     * 申请权限通过的列表
     * @param requestCode
     * @param perms
     */
    void onPermissionsGranted(int requestCode, List<String> perms);

    /**
     * 被拒绝的权限的列表
     * @param requestCode
     * @param perms
     */
    void onPermissionsDenied(int requestCode, List<String> perms);
}
