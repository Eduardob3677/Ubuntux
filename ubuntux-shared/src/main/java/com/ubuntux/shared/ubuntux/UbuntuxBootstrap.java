package com.ubuntux.shared.ubuntux;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.ubuntux.UbuntuxConstants.UBUNTUX_APP;

public class UbuntuxBootstrap {

    private static final String LOG_TAG = "UbuntuxBootstrap";

    /** The field name used by Termux app to store package variant in
     * {@link UBUNTUX_APP#BUILD_CONFIG_CLASS_NAME} class. */
    public static final String BUILD_CONFIG_FIELD_TERMUX_PACKAGE_VARIANT = "UBUNTUX_PACKAGE_VARIANT";


    /** The {@link PackageManager} for the bootstrap in the app APK added in app/build.gradle. */
    public static PackageManager UBUNTUX_APP_PACKAGE_MANAGER;

    /** The {@link PackageVariant} for the bootstrap in the app APK added in app/build.gradle. */
    public static PackageVariant UBUNTUX_APP_PACKAGE_VARIANT;

    /** Set {@link #UBUNTUX_APP_PACKAGE_VARIANT} and {@link #UBUNTUX_APP_PACKAGE_MANAGER} from {@code packageVariantName} passed. */
    public static void setTermuxPackageManagerAndVariant(@Nullable String packageVariantName) {
        UBUNTUX_APP_PACKAGE_VARIANT = PackageVariant.variantOf(packageVariantName);
        if (UBUNTUX_APP_PACKAGE_VARIANT == null) {
            throw new RuntimeException("Unsupported UBUNTUX_APP_PACKAGE_VARIANT \"" + packageVariantName + "\"");
        }

        Logger.logVerbose(LOG_TAG, "Set UBUNTUX_APP_PACKAGE_VARIANT to \"" + UBUNTUX_APP_PACKAGE_VARIANT + "\"");

        // Set packageManagerName to substring before first dash "-" in packageVariantName
        int index = packageVariantName.indexOf('-');
        String packageManagerName = (index == -1) ? null : packageVariantName.substring(0, index);
        UBUNTUX_APP_PACKAGE_MANAGER = PackageManager.managerOf(packageManagerName);
        if (UBUNTUX_APP_PACKAGE_MANAGER == null) {
            throw new RuntimeException("Unsupported UBUNTUX_APP_PACKAGE_MANAGER \"" + packageManagerName + "\" with variant \"" + packageVariantName + "\"");
        }

        Logger.logVerbose(LOG_TAG, "Set UBUNTUX_APP_PACKAGE_MANAGER to \"" + UBUNTUX_APP_PACKAGE_MANAGER + "\"");
    }

    /**
     * Set {@link #UBUNTUX_APP_PACKAGE_VARIANT} and {@link #UBUNTUX_APP_PACKAGE_MANAGER} with the
     * {@link #BUILD_CONFIG_FIELD_TERMUX_PACKAGE_VARIANT} field value from the
     * {@link UBUNTUX_APP#BUILD_CONFIG_CLASS_NAME} class of the Termux app APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the Termux app and can be used
     * by plugin apps.
     *
     * @param currentPackageContext The context of current package.
     */
    public static void setTermuxPackageManagerAndVariantFromTermuxApp(@NonNull Context currentPackageContext) {
        String packageVariantName = getTermuxAppBuildConfigPackageVariantFromTermuxApp(currentPackageContext);
        if (packageVariantName != null) {
            UbuntuxBootstrap.setTermuxPackageManagerAndVariant(packageVariantName);
        } else {
            Logger.logError(LOG_TAG, "Failed to set UBUNTUX_APP_PACKAGE_VARIANT and UBUNTUX_APP_PACKAGE_MANAGER from the termux app");
        }
    }

    /**
     * Get {@link #BUILD_CONFIG_FIELD_TERMUX_PACKAGE_VARIANT} field value from the
     * {@link UBUNTUX_APP#BUILD_CONFIG_CLASS_NAME} class of the Termux app APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the Termux app.
     *
     * @param currentPackageContext The context of current package.
     * @return Returns the field value, otherwise {@code null} if an exception was raised or failed
     * to get termux app package context.
     */
    public static String getTermuxAppBuildConfigPackageVariantFromTermuxApp(@NonNull Context currentPackageContext) {
        try {
            return (String) UbuntuxUtils.getTermuxAppAPKBuildConfigClassField(currentPackageContext, BUILD_CONFIG_FIELD_TERMUX_PACKAGE_VARIANT);
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Failed to get \"" + BUILD_CONFIG_FIELD_TERMUX_PACKAGE_VARIANT + "\" value from \"" + UBUNTUX_APP.BUILD_CONFIG_CLASS_NAME + "\" class", e);
            return null;
        }
    }



    /** Is {@link PackageManager#APT} set as {@link #UBUNTUX_APP_PACKAGE_MANAGER}. */
    public static boolean isAppPackageManagerAPT() {
        return PackageManager.APT.equals(UBUNTUX_APP_PACKAGE_MANAGER);
    }

    /** Is {@link PackageManager#UBUNTU} set as {@link #UBUNTUX_APP_PACKAGE_MANAGER}. */
    public static boolean isAppPackageManagerUbuntu() {
        return PackageManager.UBUNTU.equals(UBUNTUX_APP_PACKAGE_MANAGER);
    }

    ///** Is {@link PackageManager#TAPM} set as {@link #UBUNTUX_APP_PACKAGE_MANAGER}. */
    //public static boolean isAppPackageManagerTAPM() {
    //    return PackageManager.TAPM.equals(UBUNTUX_APP_PACKAGE_MANAGER);
    //}

    ///** Is {@link PackageManager#PACMAN} set as {@link #UBUNTUX_APP_PACKAGE_MANAGER}. */
    //public static boolean isAppPackageManagerPACMAN() {
    //    return PackageManager.PACMAN.equals(UBUNTUX_APP_PACKAGE_MANAGER);
    //}



    /** Is {@link PackageVariant#UBUNTU_22_04} set as {@link #UBUNTUX_APP_PACKAGE_VARIANT}. */
    public static boolean isAppPackageVariantUbuntu2204() {
        return PackageVariant.UBUNTU_22_04.equals(UBUNTUX_APP_PACKAGE_VARIANT);
    }

    /** Is {@link PackageVariant#UBUNTU_20_04} set as {@link #UBUNTUX_APP_PACKAGE_VARIANT}. */
    public static boolean isAppPackageVariantUbuntu2004() {
        return PackageVariant.UBUNTU_20_04.equals(UBUNTUX_APP_PACKAGE_VARIANT);
    }

    ///** Is {@link PackageVariant#TAPM_ANDROID_7} set as {@link #UBUNTUX_APP_PACKAGE_VARIANT}. */
    //public static boolean isAppPackageVariantTAPMAndroid7() {
    //    return PackageVariant.TAPM_ANDROID_7.equals(UBUNTUX_APP_PACKAGE_VARIANT);
    //}

    ///** Is {@link PackageVariant#PACMAN_ANDROID_7} set as {@link #UBUNTUX_APP_PACKAGE_VARIANT}. */
    //public static boolean isAppPackageVariantTPACMANAndroid7() {
    //    return PackageVariant.PACMAN_ANDROID_7.equals(UBUNTUX_APP_PACKAGE_VARIANT);
    //}



    /** Ubuntux package manager. */
    public enum PackageManager {

        /**
         * Advanced Package Tool (APT) for managing debian deb package files.
         * https://wiki.debian.org/Apt
         * https://wiki.debian.org/deb
         */
        APT("apt"),

        /**
         * Ubuntu package management using APT.
         */
        UBUNTU("ubuntu");

        ///**
        // * Ubuntux Android Package Manager (TAPM) for managing termux apk package files.
        // * https://en.wikipedia.org/wiki/Apk_(file_format)
        // */
        //TAPM("tapm");

        ///**
        // * Package Manager (PACMAN) for managing arch linux pkg.tar package files.
        // * https://wiki.archlinux.org/title/pacman
        // * https://en.wikipedia.org/wiki/Arch_Linux#Pacman
        // */
        //PACMAN("pacman");

        private final String name;

        PackageManager(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean equalsManager(String manager) {
            return manager != null && manager.equals(this.name);
        }

        /** Get {@link PackageManager} for {@code name} if found, otherwise {@code null}. */
        @Nullable
        public static PackageManager managerOf(String name) {
            if (name == null || name.isEmpty()) return null;
            for (PackageManager v : PackageManager.values()) {
                if (v.name.equals(name)) {
                    return v;
                }
            }
            return null;
        }

    }



    /** Ubuntux package variant. The substring before first dash "-" must match one of the {@link PackageManager}. */
    public enum PackageVariant {

        /** {@link PackageManager#APT} variant for Ubuntu 22.04. */
        UBUNTU_22_04("ubuntu-22.04"),

        /** {@link PackageManager#APT} variant for Ubuntu 20.04. */
        UBUNTU_20_04("ubuntu-20.04");

        ///** {@link PackageManager#TAPM} variant for Android 7+. */
        //TAPM_ANDROID_7("tapm-android-7");

        ///** {@link PackageManager#PACMAN} variant for Android 7+. */
        //PACMAN_ANDROID_7("pacman-android-7");

        private final String name;

        PackageVariant(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean equalsVariant(String variant) {
            return variant != null && variant.equals(this.name);
        }

        /** Get {@link PackageVariant} for {@code name} if found, otherwise {@code null}. */
        @Nullable
        public static PackageVariant variantOf(String name) {
            if (name == null || name.isEmpty()) return null;
            for (PackageVariant v : PackageVariant.values()) {
                if (v.name.equals(name)) {
                    return v;
                }
            }
            return null;
        }

    }

}
