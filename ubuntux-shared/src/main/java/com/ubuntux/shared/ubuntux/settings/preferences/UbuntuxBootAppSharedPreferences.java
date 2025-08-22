package com.ubuntux.shared.ubuntux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.settings.preferences.AppSharedPreferences;
import com.ubuntux.shared.settings.preferences.SharedPreferenceUtils;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxPreferenceConstants.UBUNTUX_BOOT_APP;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;

public class UbuntuxBootAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "UbuntuxBootAppSharedPreferences";

    private UbuntuxBootAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_BOOT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_BOOT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link UbuntuxBootAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_BOOT_PACKAGE_NAME}.
     * @return Returns the {@link UbuntuxBootAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static UbuntuxBootAppSharedPreferences build(@NonNull final Context context) {
        Context termuxBootPackageContext = PackageUtils.getContextForPackage(context, UbuntuxConstants.UBUNTUX_BOOT_PACKAGE_NAME);
        if (termuxBootPackageContext == null)
            return null;
        else
            return new UbuntuxBootAppSharedPreferences(termuxBootPackageContext);
    }

    /**
     * Get {@link UbuntuxBootAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_BOOT_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link UbuntuxBootAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static UbuntuxBootAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context termuxBootPackageContext = UbuntuxUtils.getContextForPackageOrExitApp(context, UbuntuxConstants.UBUNTUX_BOOT_PACKAGE_NAME, exitAppOnError);
        if (termuxBootPackageContext == null)
            return null;
        else
            return new UbuntuxBootAppSharedPreferences(termuxBootPackageContext);
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, UBUNTUX_BOOT_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, UBUNTUX_BOOT_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, UBUNTUX_BOOT_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
