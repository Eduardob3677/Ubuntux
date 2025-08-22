package com.ubuntux.shared.ubuntux.shell.command.environment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.android.SELinuxUtils;
import com.ubuntux.shared.data.DataUtils;
import com.ubuntux.shared.shell.command.environment.ShellEnvironmentUtils;
import com.ubuntux.shared.ubuntux.UbuntuxBootstrap;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;
import com.ubuntux.shared.ubuntux.shell.am.UbuntuxAmSocketServer;

import java.util.HashMap;

/**
 * Environment for {@link UbuntuxConstants#UBUNTUX_PACKAGE_NAME} app.
 */
public class UbuntuxAppShellEnvironment {

    /** Ubuntux app environment variables. */
    public static HashMap<String, String> termuxAppEnvironment;

    /** Environment variable for the Termux app version. */
    public static final String ENV_TERMUX_VERSION = UbuntuxConstants.UBUNTUX_ENV_PREFIX_ROOT + "_VERSION";

    /** Environment variable prefix for the Termux app. */
    public static final String UBUNTUX_APP_ENV_PREFIX = UbuntuxConstants.UBUNTUX_ENV_PREFIX_ROOT + "_APP__";

    /** Environment variable for the Termux app version name. */
    public static final String ENV_TERMUX_APP__VERSION_NAME = UBUNTUX_APP_ENV_PREFIX + "VERSION_NAME";
    /** Environment variable for the Termux app version code. */
    public static final String ENV_TERMUX_APP__VERSION_CODE = UBUNTUX_APP_ENV_PREFIX + "VERSION_CODE";
    /** Environment variable for the Termux app package name. */
    public static final String ENV_TERMUX_APP__PACKAGE_NAME = UBUNTUX_APP_ENV_PREFIX + "PACKAGE_NAME";
    /** Environment variable for the Termux app process id. */
    public static final String ENV_TERMUX_APP__PID = UBUNTUX_APP_ENV_PREFIX + "PID";
    /** Environment variable for the Termux app uid. */
    public static final String ENV_TERMUX_APP__UID = UBUNTUX_APP_ENV_PREFIX + "UID";
    /** Environment variable for the Termux app targetSdkVersion. */
    public static final String ENV_TERMUX_APP__TARGET_SDK = UBUNTUX_APP_ENV_PREFIX + "TARGET_SDK";
    /** Environment variable for the Termux app is debuggable apk build. */
    public static final String ENV_TERMUX_APP__IS_DEBUGGABLE_BUILD = UBUNTUX_APP_ENV_PREFIX + "IS_DEBUGGABLE_BUILD";
    /** Environment variable for the Termux app {@link UbuntuxConstants} APK_RELEASE_*. */
    public static final String ENV_TERMUX_APP__APK_RELEASE = UBUNTUX_APP_ENV_PREFIX + "APK_RELEASE";
    /** Environment variable for the Termux app install path. */
    public static final String ENV_TERMUX_APP__APK_PATH = UBUNTUX_APP_ENV_PREFIX + "APK_PATH";
    /** Environment variable for the Termux app is installed on external/portable storage. */
    public static final String ENV_TERMUX_APP__IS_INSTALLED_ON_EXTERNAL_STORAGE = UBUNTUX_APP_ENV_PREFIX + "IS_INSTALLED_ON_EXTERNAL_STORAGE";

    /** Environment variable for the Termux app process selinux context. */
    public static final String ENV_TERMUX_APP__SE_PROCESS_CONTEXT = UBUNTUX_APP_ENV_PREFIX + "SE_PROCESS_CONTEXT";
    /** Environment variable for the Termux app data files selinux context. */
    public static final String ENV_TERMUX_APP__SE_FILE_CONTEXT = UBUNTUX_APP_ENV_PREFIX + "SE_FILE_CONTEXT";
    /** Environment variable for the Termux app seInfo tag found in selinux policy used to set app process and app data files selinux context. */
    public static final String ENV_TERMUX_APP__SE_INFO = UBUNTUX_APP_ENV_PREFIX + "SE_INFO";
    /** Environment variable for the Termux app user id. */
    public static final String ENV_TERMUX_APP__USER_ID = UBUNTUX_APP_ENV_PREFIX + "USER_ID";
    /** Environment variable for the Termux app profile owner. */
    public static final String ENV_TERMUX_APP__PROFILE_OWNER = UBUNTUX_APP_ENV_PREFIX + "PROFILE_OWNER";

    /** Environment variable for the Termux app {@link UbuntuxBootstrap#UBUNTUX_APP_PACKAGE_MANAGER}. */
    public static final String ENV_TERMUX_APP__PACKAGE_MANAGER = UBUNTUX_APP_ENV_PREFIX + "PACKAGE_MANAGER";
    /** Environment variable for the Termux app {@link UbuntuxBootstrap#UBUNTUX_APP_PACKAGE_VARIANT}. */
    public static final String ENV_TERMUX_APP__PACKAGE_VARIANT = UBUNTUX_APP_ENV_PREFIX + "PACKAGE_VARIANT";
    /** Environment variable for the Termux app files directory. */
    public static final String ENV_TERMUX_APP__FILES_DIR = UBUNTUX_APP_ENV_PREFIX + "FILES_DIR";


    /** Environment variable for the Termux app {@link UbuntuxAmSocketServer#getTermuxAppAMSocketServerEnabled(Context)}. */
    public static final String ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED = UBUNTUX_APP_ENV_PREFIX + "AM_SOCKET_SERVER_ENABLED";



    /** Get shell environment for Termux app. */
    @Nullable
    public static HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext) {
        setTermuxAppEnvironment(currentPackageContext);
        return termuxAppEnvironment;
    }

    /** Set Termux app environment variables in {@link #termuxAppEnvironment}. */
    public synchronized static void setTermuxAppEnvironment(@NonNull Context currentPackageContext) {
        boolean isTermuxApp = UbuntuxConstants.UBUNTUX_PACKAGE_NAME.equals(currentPackageContext.getPackageName());

        // If current package context is of termux app and its environment is already set, then no need to set again since it won't change
        // Other apps should always set environment again since termux app may be installed/updated/deleted in background
        if (termuxAppEnvironment != null && isTermuxApp)
            return;

        termuxAppEnvironment = null;

        String packageName = UbuntuxConstants.UBUNTUX_PACKAGE_NAME;
        PackageInfo packageInfo = PackageUtils.getPackageInfoForPackage(currentPackageContext, packageName);
        if (packageInfo == null) return;
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfoForPackage(currentPackageContext, packageName);
        if (applicationInfo == null || !applicationInfo.enabled) return;

        HashMap<String, String> environment = new HashMap<>();

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_VERSION, PackageUtils.getVersionNameForPackage(packageInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__VERSION_NAME, PackageUtils.getVersionNameForPackage(packageInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__VERSION_CODE, String.valueOf(PackageUtils.getVersionCodeForPackage(packageInfo)));

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__PACKAGE_NAME, packageName);
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__PID, UbuntuxUtils.getTermuxAppPID(currentPackageContext));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__UID, String.valueOf(PackageUtils.getUidForPackage(applicationInfo)));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__TARGET_SDK, String.valueOf(PackageUtils.getTargetSDKForPackage(applicationInfo)));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__IS_DEBUGGABLE_BUILD, PackageUtils.isAppForPackageADebuggableBuild(applicationInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__APK_PATH, PackageUtils.getBaseAPKPathForPackage(applicationInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__IS_INSTALLED_ON_EXTERNAL_STORAGE, PackageUtils.isAppInstalledOnExternalStorage(applicationInfo));

        putTermuxAPKSignature(currentPackageContext, environment);

        Context termuxPackageContext = UbuntuxUtils.getTermuxPackageContext(currentPackageContext);
        if (termuxPackageContext != null) {
            // An app that does not have the same sharedUserId as termux app will not be able to get
            // get termux context's classloader to get BuildConfig.UBUNTUX_PACKAGE_VARIANT via reflection.
            // Check UbuntuxBootstrap.setTermuxPackageManagerAndVariantFromTermuxApp()
            if (UbuntuxBootstrap.UBUNTUX_APP_PACKAGE_MANAGER != null)
                environment.put(ENV_TERMUX_APP__PACKAGE_MANAGER, UbuntuxBootstrap.UBUNTUX_APP_PACKAGE_MANAGER.getName());
            if (UbuntuxBootstrap.UBUNTUX_APP_PACKAGE_VARIANT != null)
                environment.put(ENV_TERMUX_APP__PACKAGE_VARIANT, UbuntuxBootstrap.UBUNTUX_APP_PACKAGE_VARIANT.getName());

            // Will not be set for plugins
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED,
                UbuntuxAmSocketServer.getTermuxAppAMSocketServerEnabled(currentPackageContext));

            String filesDirPath = currentPackageContext.getFilesDir().getAbsolutePath();
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__FILES_DIR, filesDirPath);

            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__SE_PROCESS_CONTEXT, SELinuxUtils.getContext());
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__SE_FILE_CONTEXT, SELinuxUtils.getFileContext(filesDirPath));

            String seInfoUser = PackageUtils.getApplicationInfoSeInfoUserForPackage(applicationInfo);
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__SE_INFO, PackageUtils.getApplicationInfoSeInfoForPackage(applicationInfo) +
                (DataUtils.isNullOrEmpty(seInfoUser) ? "" : seInfoUser));

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__USER_ID, String.valueOf(PackageUtils.getUserIdForPackage(currentPackageContext)));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__PROFILE_OWNER, PackageUtils.getProfileOwnerPackageNameForUser(currentPackageContext));
        }

        termuxAppEnvironment = environment;
    }

    /** Put {@link #ENV_TERMUX_APP__APK_RELEASE} in {@code environment}. */
    public static void putTermuxAPKSignature(@NonNull Context currentPackageContext,
                                             @NonNull HashMap<String, String> environment) {
        String signingCertificateSHA256Digest = PackageUtils.getSigningCertificateSHA256DigestForPackage(currentPackageContext,
            UbuntuxConstants.UBUNTUX_PACKAGE_NAME);
        if (signingCertificateSHA256Digest != null) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_APP__APK_RELEASE,
                UbuntuxUtils.getAPKRelease(signingCertificateSHA256Digest).replaceAll("[^a-zA-Z]", "_").toUpperCase());
        }
    }

    /** Update {@link #ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED} value in {@code environment}. */
    public synchronized static void updateTermuxAppAMSocketServerEnabled(@NonNull Context currentPackageContext) {
        if (termuxAppEnvironment == null) return;
        termuxAppEnvironment.remove(ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED);
        ShellEnvironmentUtils.putToEnvIfSet(termuxAppEnvironment, ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED,
            UbuntuxAmSocketServer.getTermuxAppAMSocketServerEnabled(currentPackageContext));
    }

}
