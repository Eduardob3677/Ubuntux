package com.ubuntux.shared.ubuntux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.settings.preferences.AppSharedPreferences;
import com.ubuntux.shared.settings.preferences.SharedPreferenceUtils;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxPreferenceConstants.UBUNTUX_WIDGET_APP;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;

import java.util.UUID;

public class UbuntuxWidgetAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "UbuntuxWidgetAppSharedPreferences";

    private UbuntuxWidgetAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                UbuntuxConstants.UBUNTUX_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link UbuntuxWidgetAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_WIDGET_PACKAGE_NAME}.
     * @return Returns the {@link UbuntuxWidgetAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static UbuntuxWidgetAppSharedPreferences build(@NonNull final Context context) {
        Context termuxWidgetPackageContext = PackageUtils.getContextForPackage(context, UbuntuxConstants.UBUNTUX_WIDGET_PACKAGE_NAME);
        if (termuxWidgetPackageContext == null)
            return null;
        else
            return new UbuntuxWidgetAppSharedPreferences(termuxWidgetPackageContext);
    }

    /**
     * Get the {@link UbuntuxWidgetAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link UbuntuxConstants#UBUNTUX_WIDGET_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link UbuntuxWidgetAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static UbuntuxWidgetAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context termuxWidgetPackageContext = UbuntuxUtils.getContextForPackageOrExitApp(context, UbuntuxConstants.UBUNTUX_WIDGET_PACKAGE_NAME, exitAppOnError);
        if (termuxWidgetPackageContext == null)
            return null;
        else
            return new UbuntuxWidgetAppSharedPreferences(termuxWidgetPackageContext);
    }



    public static String getGeneratedToken(@NonNull Context context) {
        UbuntuxWidgetAppSharedPreferences preferences = UbuntuxWidgetAppSharedPreferences.build(context, true);
        if (preferences == null) return null;
        return preferences.getGeneratedToken();
    }

    public String getGeneratedToken() {
        String token =  SharedPreferenceUtils.getString(mSharedPreferences, UBUNTUX_WIDGET_APP.KEY_TOKEN, null, true);
        if (token == null) {
            token = UUID.randomUUID().toString();
            SharedPreferenceUtils.setString(mSharedPreferences, UBUNTUX_WIDGET_APP.KEY_TOKEN, token, true);
        }
        return token;
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, UBUNTUX_WIDGET_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, UBUNTUX_WIDGET_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, UBUNTUX_WIDGET_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
