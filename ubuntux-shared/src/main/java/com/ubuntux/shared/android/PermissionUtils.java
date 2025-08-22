package com.ubuntux.shared.android;

import android.content.Context;
import android.content.pm.PackageManager;
import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

/**
 * Permission utilities for Ubuntu backend.
 */
public class PermissionUtils {

    public static final int REQUEST_GRANT_STORAGE_PERMISSION = 1000;

    /**
     * Check if permission is granted.
     * Simplified implementation for Ubuntu backend.
     */
    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check multiple permissions.
     * Simplified implementation for Ubuntu backend.
     */
    public static boolean checkPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Request permission from Activity.
     * Simplified implementation for Ubuntu backend.
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * Request permission from AppCompatActivity.
     * Simplified implementation for Ubuntu backend.
     */
    public static void requestPermission(AppCompatActivity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * Validate display over other apps permission.
     * Simplified implementation for Ubuntu backend.
     */
    public static boolean validateDisplayOverOtherAppsPermissionForPostAndroid10(Context context, boolean requestPermission) {
        // Simplified implementation - just return true for Ubuntu backend
        return true;
    }

    /**
     * Check and request legacy or manage external storage permission.
     * Simplified implementation for Ubuntu backend.
     */
    public static boolean checkAndRequestLegacyOrManageExternalStoragePermission(Activity activity, int requestCode, boolean isPermissionCallback) {
        // Simplified implementation for Ubuntu backend
        return true;
    }

    /**
     * Check if battery optimizations are disabled.
     * Simplified implementation for Ubuntu backend.
     */
    public static boolean checkIfBatteryOptimizationsDisabled(Context context) {
        // Simplified implementation for Ubuntu backend
        return true;
    }

    /**
     * Request to disable battery optimizations.
     * Simplified implementation for Ubuntu backend.
     */
    public static void requestDisableBatteryOptimizations(Context context) {
        // Simplified implementation for Ubuntu backend - do nothing
    }
}
