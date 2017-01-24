package com.cn.jerey.permissiontools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Xiamin on 2017/1/24.
 */

public class PermissionUtils {
    @TargetApi(11)
    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }
}
