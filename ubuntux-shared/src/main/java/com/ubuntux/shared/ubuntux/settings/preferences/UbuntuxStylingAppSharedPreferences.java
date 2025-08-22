package com.ubuntux.shared.ubuntux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.settings.preferences.AppSharedPreferences;
import com.ubuntux.shared.settings.preferences.SharedPreferenceUtils;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxPreferenceConstants.UBUNTUX_STYLING_APP;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;

public class UbuntuxStylingAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "UbuntuxStylingAppSharedPreferences";

    private UbuntuxStylingAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_STYLING_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_STYLING_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link UbuntuxStylingAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_STYLING_PACKAGE_NAME}.
     * @return Returns the {@link UbuntuxStylingAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static UbuntuxStylingAppSharedPreferences build(@NonNull final Context context) {
        Context termuxStylingPackageContext = PackageUtils.getContextForPackage(context, UbuntuxConstants.UBUNTUX_STYLING_PACKAGE_NAME);
        if (termuxStylingPackageContext == null)
            return null;
        else
            return new UbuntuxStylingAppSharedPreferences(termuxStylingPackageContext);
    }

    /**
     * Get {@link UbuntuxStylingAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_STYLING_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link UbuntuxStylingAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static UbuntuxStylingAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context termuxStylingPackageContext = UbuntuxUtils.getContextForPackageOrExitApp(context, UbuntuxConstants.UBUNTUX_STYLING_PACKAGE_NAME, exitAppOnError);
        if (termuxStylingPackageContext == null)
            return null;
        else
            return new UbuntuxStylingAppSharedPreferences(termuxStylingPackageContext);
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, UBUNTUX_STYLING_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, UBUNTUX_STYLING_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, UBUNTUX_STYLING_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
