package com.ubuntux.shared.ubuntux.shell.command.environment;

import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.shell.command.environment.ShellEnvironmentUtils;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;

import java.util.HashMap;

/**
 * Environment for {@link UbuntuxConstants#UBUNTUX_API_PACKAGE_NAME} app.
 */
public class UbuntuxAPIShellEnvironment {

    /** Environment variable prefix for the Termux:API app. */
    public static final String UBUNTUX_API_APP_ENV_PREFIX = UbuntuxConstants.UBUNTUX_ENV_PREFIX_ROOT + "_API_APP__";

    /** Environment variable for the Termux:API app version. */
    public static final String ENV_TERMUX_API_APP__VERSION_NAME = UBUNTUX_API_APP_ENV_PREFIX + "VERSION_NAME";

    /** Get shell environment for Termux:API app. */
    @Nullable
    public static HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext) {
        if (UbuntuxUtils.isTermuxAPIAppInstalled(currentPackageContext) != null) return null;

        String packageName = UbuntuxConstants.UBUNTUX_API_PACKAGE_NAME;
        PackageInfo packageInfo = PackageUtils.getPackageInfoForPackage(currentPackageContext, packageName);
        if (packageInfo == null) return null;

        HashMap<String, String> environment = new HashMap<>();

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_API_APP__VERSION_NAME, PackageUtils.getVersionNameForPackage(packageInfo));

        return environment;
    }

}
