package com.ubuntux.app;

import android.app.Application;
import android.content.Context;

import com.ubuntux.BuildConfig;
import com.ubuntux.shared.errors.Error;
import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.ubuntux.UbuntuxBootstrap;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;
import com.ubuntux.shared.ubuntux.crash.UbuntuxCrashUtils;
import com.ubuntux.shared.ubuntux.file.UbuntuxFileUtils;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxAppSharedPreferences;
import com.ubuntux.shared.ubuntux.settings.properties.UbuntuxAppSharedProperties;
import com.ubuntux.shared.ubuntux.shell.command.environment.UbuntuxShellEnvironment;
import com.ubuntux.shared.ubuntux.shell.am.UbuntuxAmSocketServer;
import com.ubuntux.shared.ubuntux.shell.UbuntuxShellManager;
import com.ubuntux.shared.ubuntux.theme.UbuntuxThemeUtils;

public class UbuntuxApplication extends Application {

    private static final String LOG_TAG = "UbuntuxApplication";

    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        // Set crash handler for the app
        UbuntuxCrashUtils.setDefaultCrashHandler(this);

        // Set log config for the app
        setLogConfig(context);

        Logger.logDebug("Starting Application");

        // Set UbuntuxBootstrap.UBUNTUX_APP_PACKAGE_MANAGER and UbuntuxBootstrap.UBUNTUX_APP_PACKAGE_VARIANT
        UbuntuxBootstrap.setTermuxPackageManagerAndVariant(BuildConfig.UBUNTUX_PACKAGE_VARIANT);

        // Init app wide SharedProperties loaded from termux.properties
        UbuntuxAppSharedProperties properties = UbuntuxAppSharedProperties.init(context);

        // Init app wide shell manager
        UbuntuxShellManager shellManager = UbuntuxShellManager.init(context);

        // Set NightMode.APP_NIGHT_MODE
        UbuntuxThemeUtils.setAppNightMode(properties.getNightMode());

        // Check and create termux files directory. If failed to access it like in case of secondary
        // user or external sd card installation, then don't run files directory related code
        Error error = UbuntuxFileUtils.isTermuxFilesDirectoryAccessible(this, true, true);
        boolean isTermuxFilesDirectoryAccessible = error == null;
        if (isTermuxFilesDirectoryAccessible) {
            Logger.logInfo(LOG_TAG, "Termux files directory is accessible");

            error = UbuntuxFileUtils.isAppsTermuxAppDirectoryAccessible(true, true);
            if (error != null) {
                Logger.logErrorExtended(LOG_TAG, "Create apps/termux-app directory failed\n" + error);
                return;
            }

            // Setup termux-am-socket server
            UbuntuxAmSocketServer.setupTermuxAmSocketServer(context);
        } else {
            Logger.logErrorExtended(LOG_TAG, "Termux files directory is not accessible\n" + error);
        }

        // Init UbuntuxShellEnvironment constants and caches after everything has been setup including termux-am-socket server
        UbuntuxShellEnvironment.init(this);

        if (isTermuxFilesDirectoryAccessible) {
            UbuntuxShellEnvironment.writeEnvironmentToFile(this);
        }
    }

    public static void setLogConfig(Context context) {
        Logger.setDefaultLogTag(UbuntuxConstants.UBUNTUX_APP_NAME);

        // Load the log level from shared preferences and set it to the {@link Logger.CURRENT_LOG_LEVEL}
        UbuntuxAppSharedPreferences preferences = UbuntuxAppSharedPreferences.build(context);
        if (preferences == null) return;
        preferences.setLogLevel(null, preferences.getLogLevel());
    }

}
