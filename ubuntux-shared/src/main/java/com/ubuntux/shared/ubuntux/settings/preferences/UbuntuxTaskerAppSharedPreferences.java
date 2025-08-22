package com.ubuntux.shared.ubuntux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.settings.preferences.AppSharedPreferences;
import com.ubuntux.shared.settings.preferences.SharedPreferenceUtils;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxPreferenceConstants.UBUNTUX_TASKER_APP;
import com.ubuntux.shared.logger.Logger;

public class UbuntuxTaskerAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "UbuntuxTaskerAppSharedPreferences";

    private  UbuntuxTaskerAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_TASKER_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_TASKER_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link UbuntuxTaskerAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_TASKER_PACKAGE_NAME}.
     * @return Returns the {@link UbuntuxTaskerAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static UbuntuxTaskerAppSharedPreferences build(@NonNull final Context context) {
        Context termuxTaskerPackageContext = PackageUtils.getContextForPackage(context, UbuntuxConstants.UBUNTUX_TASKER_PACKAGE_NAME);
        if (termuxTaskerPackageContext == null)
            return null;
        else
            return new UbuntuxTaskerAppSharedPreferences(termuxTaskerPackageContext);
    }

    /**
     * Get {@link UbuntuxTaskerAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_TASKER_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link UbuntuxTaskerAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static  UbuntuxTaskerAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context termuxTaskerPackageContext = UbuntuxUtils.getContextForPackageOrExitApp(context, UbuntuxConstants.UBUNTUX_TASKER_PACKAGE_NAME, exitAppOnError);
        if (termuxTaskerPackageContext == null)
            return null;
        else
            return new UbuntuxTaskerAppSharedPreferences(termuxTaskerPackageContext);
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, UBUNTUX_TASKER_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, UBUNTUX_TASKER_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, UBUNTUX_TASKER_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }



    public int getLastPendingIntentRequestCode() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, UBUNTUX_TASKER_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, UBUNTUX_TASKER_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE);
    }

    public void setLastPendingIntentRequestCode(int lastPendingIntentRequestCode) {
        SharedPreferenceUtils.setInt(mSharedPreferences, UBUNTUX_TASKER_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, lastPendingIntentRequestCode, false);
    }

}
