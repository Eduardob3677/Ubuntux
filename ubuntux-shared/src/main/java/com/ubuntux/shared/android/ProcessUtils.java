package com.ubuntux.shared.android;

import android.content.Context;

/**
 * Process utilities for Ubuntu backend.
 */
public class ProcessUtils {

    /**
     * Get app process name for given PID.
     * Simplified implementation for Ubuntu backend.
     */
    public static String getAppProcessNameForPid(Context context, int pid) {
        // Simplified implementation for Ubuntu backend
        return "ubuntux-process-" + pid;
    }
}
