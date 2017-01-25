package com.cn.jerey.permissiontools.Callback;

import java.util.List;

/**
 * Created by Xiamin on 2017/1/24.
 */

public interface PermissionCallbacks {
    /**
     * request successful list
     * @param requestCode
     * @param perms
     */
    void onPermissionsGranted(int requestCode, List<String> perms);

    /**
     * request denied list
     * @param requestCode
     * @param perms
     */
    void onPermissionsDenied(int requestCode, List<String> perms);
}
