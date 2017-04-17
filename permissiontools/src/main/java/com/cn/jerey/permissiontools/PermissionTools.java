package com.cn.jerey.permissiontools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.cn.jerey.permissiontools.Callback.PermissionCallbacks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * use :
 * permissionTools =  new PermissionTools.Builder(this)
 * .setOnPermissionCallbacks(new PermissionCallbacks() {
 * <p>
 * public void onPermissionsGranted(int requestCode, Listperms) {
 * Toast.makeText(MainActivity.this,"权限申请通过",Toast.LENGTH_SHORT).show();
 * }
 * public void onPermissionsDenied(int requestCode, List perms) {
 * Toast.makeText(MainActivity.this,"权限申请被拒绝",Toast.LENGTH_SHORT).show();
 * }
 * })
 * .setRequestCode(111)
 * .build();
 * <p>
 * permissionTools.requestPermissions(Manifest.permission.CAMERA)
 * public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
 * super.onRequestPermissionsResult(requestCode, permissions, grantResults);
 * permissionTools.onRequestPermissionsResult(requestCode,permissions,grantResults);
 * }
 */
public class PermissionTools {
    private static final String TAG = "PermissionTools";
    public static final String REQUEST_PERMISSION_STRING = "request_permission";
    private Context mContext;
    private PermissionCallbacks mPermissionCallbacks;
    private int mHintRequestID = -1;
    private int mHintNeverAskID = -1;
    private int mRequestCode = -1;

    private int mPositiveBtnIDForReq = -1;
    private int mNegativeBtnIDForReq = -1;
    private int mPositiveBtnIDForNeverAsk = -1;
    private int mNegativeBtnIDForNeverAsk = -1;

    /**
     * @param context      Activity
     * @param callbacks    申请成功或者失败的回调接口
     * @param hintRequest  申请时提醒语句
     * @param hintNeverAsk 被设置为永不提醒后申请失败的提示语句
     * @param requestCode
     */
    private PermissionTools(Context context,
                            PermissionCallbacks callbacks,
                            int hintRequest,
                            int hintNeverAsk,
                            int requestCode) {
        this.mContext = context;
        this.mPermissionCallbacks = callbacks;
        this.mHintRequestID = hintRequest;
        this.mHintNeverAskID = hintNeverAsk;
        this.mRequestCode = requestCode;

        if (mContext == null || mPermissionCallbacks == null ||
                mRequestCode == -1) {
            throw new IllegalArgumentException("PermissionTools init error");
        }

        if (mHintRequestID == -1) {
            mHintRequestID = R.string.perm_we_need;
        }

        if (mHintNeverAskID == -1) {
            mHintNeverAskID = R.string.perm_never_ask;
        }

        if (mPositiveBtnIDForReq == -1) mPositiveBtnIDForReq = android.R.string.ok;
        if (mNegativeBtnIDForReq == -1) mNegativeBtnIDForReq = android.R.string.cancel;
        if (mPositiveBtnIDForNeverAsk == -1) mPositiveBtnIDForNeverAsk = R.string.perm_setting;
        if (mNegativeBtnIDForNeverAsk == -1) mNegativeBtnIDForNeverAsk = android.R.string.cancel;

    }

    /**
     * 需要申请的权限
     *
     * @param permissions
     */
    public void requestPermissions(final String... permissions) {
        Log.w(TAG, "start requestPermissions");
        checkCallingObjectSuitability(mContext);

        if (!hasPermissions(mContext, permissions)) {
            Log.w(TAG, "permission check does not pass, start check Whether require a dialog");
            boolean shouldShowRationale = false;
            if (Build.VERSION.SDK_INT >= 23) {
                for (String perm : permissions) {
                    shouldShowRationale = shouldShowRationale |
                            shouldShowRequestPermissionRationale(mContext, perm);
                }
            }

            if (shouldShowRationale) {
                Log.w(TAG, "ned dialog");
                Activity activity = PermissionUtils.getActivity(mContext);
                if (null == activity) {
                    return;
                }
                Log.w(TAG, "new Dialog");
                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setMessage(mHintRequestID)
                        .setPositiveButton(mPositiveBtnIDForReq, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.w(TAG, "click PositiveButton executePermissionsRequest");
                                executePermissionsRequest(mContext, permissions, mRequestCode);
                            }
                        })
                        .setNegativeButton(mNegativeBtnIDForReq, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.w(TAG, "click NegativeButton and checkDeniedPermissionsNeverAskAgain");
                                checkDeniedPermissionsNeverAskAgain(
                                        mContext,
                                        mContext.getString(mHintNeverAskID),
                                        mPositiveBtnIDForNeverAsk,
                                        mNegativeBtnIDForNeverAsk,
                                        Arrays.asList(permissions)
                                );
                                if (mPermissionCallbacks != null) {

                                    mPermissionCallbacks.onPermissionsDenied(mRequestCode, Arrays.asList(permissions));
                                }
                            }
                        })
                        .create();
                dialog.show();
            } else {
                Log.w(TAG, "no need dialog");

//                checkDeniedPermissionsNeverAskAgain(
//                        mContext,
//                        mContext.getString(mHintNeverAskID),
//                        mPositiveBtnIDForNeverAsk,
//                        mNegativeBtnIDForNeverAsk,
//                        Arrays.asList(permissions)
//                );
                executePermissionsRequest(mContext, permissions, mRequestCode);
            }

        } else {
            Log.w(TAG, "has permissions");
            if (mPermissionCallbacks != null) {
                mPermissionCallbacks.onPermissionsGranted(mRequestCode, Arrays.asList(permissions));
            }
        }
    }

    /**
     * 需要在你的Activity中的onRequestPermissionsResult中使用该方法回调结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        checkCallingObjectSuitability(mContext);

        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        /**
         * 通知申请成功的权限
         */
        if (!granted.isEmpty()) {
            if (mPermissionCallbacks != null) {
                mPermissionCallbacks.onPermissionsGranted(requestCode, granted);
            }
        }

        /**
         * 通知申请失败的权限
         */
        if (!denied.isEmpty()) {
            checkDeniedPermissionsNeverAskAgain(
                    mContext,
                    mContext.getString(mHintNeverAskID),
                    mPositiveBtnIDForNeverAsk,
                    mNegativeBtnIDForNeverAsk,
                    denied
            );
            if (mPermissionCallbacks != null) {
                mPermissionCallbacks.onPermissionsDenied(requestCode, denied);
            }
        }
    }

    /**
     * 检查当前APP是否已有权限或者无需申请权限
     *
     * @param context 请求界面的context
     * @param perms   申请的权限列表
     * @return 如果为true, 表示不需要申请权限, 如果为false, 表示需要申请权限
     */
    public static boolean hasPermissions(Context context, String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            return true;
        } else {
            for (String perm : perms) {
                boolean hasPerm = (context.checkSelfPermission(perm) ==
                        PackageManager.PERMISSION_GRANTED);
                if (!hasPerm) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkCallingObjectSuitability(Object object) {
        // 确保object是一个Activity或者Fragment
        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        boolean isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        if (!(isSupportFragment || isActivity || (isAppFragment && isMinSdkM))) {
            if (isAppFragment) {
                throw new IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }

    @TargetApi(23)
    private void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);

        if (object instanceof Activity) {
            ((Activity) object).requestPermissions(perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    private boolean checkDeniedPermissionsNeverAskAgain(final Object object,
                                                        String rationale,
                                                        int positiveButton,
                                                        int negativeButton,
                                                        List<String> deniedPerms) {
        return checkDeniedPermissionsNeverAskAgain(object, rationale,
                positiveButton, negativeButton, null, deniedPerms);
    }

    /**
     * 检查被拒绝提供的权限是否选中了不再询问
     */
    private boolean checkDeniedPermissionsNeverAskAgain(final Object object,
                                                        String rationale,
                                                        int positiveButton,
                                                        int negativeButton,
                                                        DialogInterface.OnClickListener negativeButtonOnClickListener,
                                                        List<String> deniedPerms) {
        boolean shouldShowRationale;
        for (String perm : deniedPerms) {
            shouldShowRationale = shouldShowRequestPermissionRationale(object, perm);
            if (!shouldShowRationale) {
                final Activity activity = PermissionUtils.getActivity(object);
                if (null == activity) {
                    return true;
                }

                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setMessage(rationale)
                        .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                startAppSettingsScreen(object, intent);
                            }
                        })
                        .setNegativeButton(negativeButton, negativeButtonOnClickListener)
                        .create();
                dialog.show();

                return true;
            }
        }

        return false;
    }

    @TargetApi(11)
    private void startAppSettingsScreen(Object object,
                                        Intent intent) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, mRequestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, mRequestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, mRequestCode);
        }
    }

    @TargetApi(23)
    private boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ((Activity) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    /**
     * PermissionTools的Builder.
     */
    public static final class Builder {
        private Context mContext;
        private PermissionCallbacks mPermissionCallbacks;
        private int mHintRequest = -1;
        private int mHintNeverAsk = -1;
        private int mRequestCode = -1;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setOnPermissionCallbacks(PermissionCallbacks permissionCallbacks) {
            this.mPermissionCallbacks = permissionCallbacks;
            return this;
        }

        /**
         * 设置申请提醒语句，默认：我们需要您提供相关权限以使我们能够更好的为您服务.
         *
         * @param requestHint
         * @return
         */
        public Builder setRequestHint(int requestHint) {
            this.mHintRequest = requestHint;
            return this;
        }

        /**
         * 设置被拒绝的语句 默认：已被拒绝获取权限,请跳转至设置主动开启权限
         *
         * @param neverAsk
         * @return
         */
        public Builder setNeverAskString(int neverAsk) {
            this.mHintNeverAsk = neverAsk;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.mRequestCode = requestCode;
            return this;
        }

        public PermissionTools build() {
            return new PermissionTools(mContext,
                    mPermissionCallbacks,
                    mHintRequest,
                    mHintNeverAsk,
                    mRequestCode);
        }
    }
}
