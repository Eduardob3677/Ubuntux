package com.ubuntux.shared.ubuntux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.settings.preferences.AppSharedPreferences;
import com.ubuntux.shared.settings.preferences.SharedPreferenceUtils;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxPreferenceConstants.UBUNTUX_API_APP;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;

public class UbuntuxAPIAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "UbuntuxAPIAppSharedPreferences";

    private UbuntuxAPIAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link UbuntuxAPIAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_API_PACKAGE_NAME}.
     * @return Returns the {@link UbuntuxAPIAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static UbuntuxAPIAppSharedPreferences build(@NonNull final Context context) {
        Context termuxAPIPackageContext = PackageUtils.getContextForPackage(context, UbuntuxConstants.UBUNTUX_API_PACKAGE_NAME);
        if (termuxAPIPackageContext == null)
            return null;
        else
            return new UbuntuxAPIAppSharedPreferences(termuxAPIPackageContext);
    }

    /**
     * Get {@link UbuntuxAPIAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_API_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link UbuntuxAPIAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static UbuntuxAPIAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context termuxAPIPackageContext = UbuntuxUtils.getContextForPackageOrExitApp(context, UbuntuxConstants.UBUNTUX_API_PACKAGE_NAME, exitAppOnError);
        if (termuxAPIPackageContext == null)
            return null;
        else
            return new UbuntuxAPIAppSharedPreferences(termuxAPIPackageContext);
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, UBUNTUX_API_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, UBUNTUX_API_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, UBUNTUX_API_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }


    public int getLastPendingIntentRequestCode() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, UBUNTUX_API_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, UBUNTUX_API_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE);
    }

    public void setLastPendingIntentRequestCode(int lastPendingIntentRequestCode) {
        SharedPreferenceUtils.setInt(mSharedPreferences, UBUNTUX_API_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, lastPendingIntentRequestCode, true);
    }

}
