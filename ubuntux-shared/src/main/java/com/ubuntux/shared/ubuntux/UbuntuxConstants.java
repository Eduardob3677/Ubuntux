package com.ubuntux.shared.ubuntux;

import android.annotation.SuppressLint;

import com.ubuntux.shared.shell.command.ExecutionCommand;
import com.ubuntux.shared.shell.command.ExecutionCommand.Runner;

import java.io.File;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

/*
 * Version: v0.52.0
 * SPDX-License-Identifier: MIT
 *
 * Changelog
 *
 * - 0.1.0 (2021-03-08)
 *      - Initial Release.
 *
 * - 0.2.0 (2021-03-11)
 *      - Added `_DIR` and `_FILE` substrings to paths.
 *      - Added `INTERNAL_PRIVATE_APP_DATA_DIR*`, `UBUNTUX_CACHE_DIR*`, `UBUNTUX_DATABASES_DIR*`,
 *          `UBUNTUX_SHARED_PREFERENCES_DIR*`, `UBUNTUX_BIN_PREFIX_DIR*`, `UBUNTUX_ETC_DIR*`,
 *          `UBUNTUX_INCLUDE_DIR*`, `UBUNTUX_LIB_DIR*`, `UBUNTUX_LIBEXEC_DIR*`, `UBUNTUX_SHARE_DIR*`,
 *          `UBUNTUX_TMP_DIR*`, `UBUNTUX_VAR_DIR*`, `UBUNTUX_STAGING_PREFIX_DIR*`,
 *          `UBUNTUX_STORAGE_HOME_DIR*`, `UBUNTUX_DEFAULT_PREFERENCES_FILE_BASENAME*`,
 *          `UBUNTUX_DEFAULT_PREFERENCES_FILE`.
 *      - Renamed `DATA_HOME_PATH` to `UBUNTUX_DATA_HOME_DIR_PATH`.
 *      - Renamed `CONFIG_HOME_PATH` to `UBUNTUX_CONFIG_HOME_DIR_PATH`.
 *      - Updated javadocs and spacing.
 *
 * - 0.3.0 (2021-03-12)
 *      - Remove `UBUNTUX_CACHE_DIR_PATH*`, `UBUNTUX_DATABASES_DIR_PATH*`,
 *          `UBUNTUX_SHARED_PREFERENCES_DIR_PATH*` since they may not be consistent on all devices.
 *      - Renamed `UBUNTUX_DEFAULT_PREFERENCES_FILE_BASENAME` to
 *          `UBUNTUX_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`. This should be used for
 *           accessing shared preferences between Termux app and its plugins if ever needed by first
 *           getting shared package context with {@link Context.createPackageContext(String,int}).
 *
 * - 0.4.0 (2021-03-16)
 *      - Added `BROADCAST_TERMUX_OPENED`,
 *          `UBUNTUX_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`
 *          `UBUNTUX_BOOT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `UBUNTUX_FLOAT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `UBUNTUX_STYLING_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `UBUNTUX_TASKER_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `UBUNTUX_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`.
 *
 * - 0.5.0 (2021-03-16)
 *      - Renamed "Termux Plugin app" labels to "Termux:Tasker app".
 *
 * - 0.6.0 (2021-03-16)
 *      - Added `UBUNTUX_FILE_SHARE_URI_AUTHORITY`.
 *
 * - 0.7.0 (2021-03-17)
 *      - Fixed javadocs.
 *
 * - 0.8.0 (2021-03-18)
 *      - Fixed Intent extra types javadocs.
 *      - Added following to `UBUNTUX_SERVICE`:
 *          `EXTRA_PENDING_INTENT`, `EXTRA_RESULT_BUNDLE`,
 *          `EXTRA_STDOUT`, `EXTRA_STDERR`, `EXTRA_EXIT_CODE`,
 *          `EXTRA_ERR`, `EXTRA_ERRMSG`.
 *
 * - 0.9.0 (2021-03-18)
 *      - Fixed javadocs.
 *
 * - 0.10.0 (2021-03-19)
 *      - Added following to `UBUNTUX_SERVICE`:
 *          `EXTRA_SESSION_ACTION`,
 *          `VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY`,
 *          `VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY`,
 *          `VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_DONT_OPEN_ACTIVITY`
 *          `VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY`.
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `EXTRA_SESSION_ACTION`.
 *
 * - 0.11.0 (2021-03-24)
 *      - Added following to `UBUNTUX_SERVICE`:
 *          `EXTRA_COMMAND_LABEL`, `EXTRA_COMMAND_DESCRIPTION`, `EXTRA_COMMAND_HELP`, `EXTRA_PLUGIN_API_HELP`.
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `EXTRA_COMMAND_LABEL`, `EXTRA_COMMAND_DESCRIPTION`, `EXTRA_COMMAND_HELP`.
 *      - Updated `RESULT_BUNDLE` related extras with `PLUGIN_RESULT_BUNDLE` prefixes.
 *
 * - 0.12.0 (2021-03-25)
 *      - Added following to `UBUNTUX_SERVICE`:
 *          `EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH`,
 *          `EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH`.
 *
 * - 0.13.0 (2021-03-25)
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `EXTRA_PENDING_INTENT`.
 *
 * - 0.14.0 (2021-03-25)
 *      - Added `FDROID_PACKAGES_BASE_URL`,
 *          `UBUNTUX_GITHUB_ORGANIZATION_NAME`, `UBUNTUX_GITHUB_ORGANIZATION_URL`,
 *          `UBUNTUX_GITHUB_REPO_NAME`, `UBUNTUX_GITHUB_REPO_URL`, `UBUNTUX_FDROID_PACKAGE_URL`,
 *          `UBUNTUX_API_GITHUB_REPO_NAME`,`UBUNTUX_API_GITHUB_REPO_URL`, `UBUNTUX_API_FDROID_PACKAGE_URL`,
 *          `UBUNTUX_BOOT_GITHUB_REPO_NAME`, `UBUNTUX_BOOT_GITHUB_REPO_URL`, `UBUNTUX_BOOT_FDROID_PACKAGE_URL`,
 *          `UBUNTUX_FLOAT_GITHUB_REPO_NAME`, `UBUNTUX_FLOAT_GITHUB_REPO_URL`, `UBUNTUX_FLOAT_FDROID_PACKAGE_URL`,
 *          `UBUNTUX_STYLING_GITHUB_REPO_NAME`, `UBUNTUX_STYLING_GITHUB_REPO_URL`, `UBUNTUX_STYLING_FDROID_PACKAGE_URL`,
 *          `UBUNTUX_TASKER_GITHUB_REPO_NAME`, `UBUNTUX_TASKER_GITHUB_REPO_URL`, `UBUNTUX_TASKER_FDROID_PACKAGE_URL`,
 *          `UBUNTUX_WIDGET_GITHUB_REPO_NAME`, `UBUNTUX_WIDGET_GITHUB_REPO_URL` `UBUNTUX_WIDGET_FDROID_PACKAGE_URL`.
 *
 * - 0.15.0 (2021-04-06)
 *      - Fixed some variables that had `PREFIX_` substring missing in their name.
 *      - Added `UBUNTUX_CRASH_LOG_FILE_PATH`, `UBUNTUX_CRASH_LOG_BACKUP_FILE_PATH`,
 *          `UBUNTUX_GITHUB_ISSUES_REPO_URL`, `UBUNTUX_API_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_BOOT_GITHUB_ISSUES_REPO_URL`, `UBUNTUX_FLOAT_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_STYLING_GITHUB_ISSUES_REPO_URL`, `UBUNTUX_TASKER_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_WIDGET_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_GITHUB_WIKI_REPO_URL`, `UBUNTUX_PACKAGES_GITHUB_WIKI_REPO_URL`,
 *          `UBUNTUX_PACKAGES_GITHUB_REPO_NAME`, `UBUNTUX_PACKAGES_GITHUB_REPO_URL`, `UBUNTUX_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_GAME_PACKAGES_GITHUB_REPO_NAME`, `UBUNTUX_GAME_PACKAGES_GITHUB_REPO_URL`, `UBUNTUX_GAME_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_SCIENCE_PACKAGES_GITHUB_REPO_NAME`, `UBUNTUX_SCIENCE_PACKAGES_GITHUB_REPO_URL`, `UBUNTUX_SCIENCE_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_ROOT_PACKAGES_GITHUB_REPO_NAME`, `UBUNTUX_ROOT_PACKAGES_GITHUB_REPO_URL`, `UBUNTUX_ROOT_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_UNSTABLE_PACKAGES_GITHUB_REPO_NAME`, `UBUNTUX_UNSTABLE_PACKAGES_GITHUB_REPO_URL`, `UBUNTUX_UNSTABLE_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `UBUNTUX_X11_PACKAGES_GITHUB_REPO_NAME`, `UBUNTUX_X11_PACKAGES_GITHUB_REPO_URL`, `UBUNTUX_X11_PACKAGES_GITHUB_ISSUES_REPO_URL`.
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `RUN_COMMAND_API_HELP_URL`.
 *
 * - 0.16.0 (2021-04-06)
 *      - Added `UBUNTUX_SUPPORT_EMAIL`, `UBUNTUX_SUPPORT_EMAIL_URL`, `UBUNTUX_SUPPORT_EMAIL_MAILTO_URL`,
 *          `UBUNTUX_REDDIT_SUBREDDIT`, `UBUNTUX_REDDIT_SUBREDDIT_URL`.
 *      - The `UBUNTUX_SUPPORT_EMAIL_URL` value must be fixed later when email has been set up.
 *
 * - 0.17.0 (2021-04-07)
 *      - Added `UBUNTUX_APP_NOTIFICATION_CHANNEL_ID`, `UBUNTUX_APP_NOTIFICATION_CHANNEL_NAME`, `UBUNTUX_APP_NOTIFICATION_ID`,
 *          `UBUNTUX_RUN_COMMAND_NOTIFICATION_CHANNEL_ID`, `UBUNTUX_RUN_COMMAND_NOTIFICATION_CHANNEL_NAME`, `UBUNTUX_RUN_COMMAND_NOTIFICATION_ID`,
 *          `UBUNTUX_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID`, `UBUNTUX_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME`,
 *          `UBUNTUX_CRASH_REPORTS_NOTIFICATION_CHANNEL_ID`, `UBUNTUX_CRASH_REPORTS_NOTIFICATION_CHANNEL_NAME`.
 *      - Updated javadocs.
 *
 * - 0.18.0 (2021-04-11)
 *      - Updated `UBUNTUX_SUPPORT_EMAIL_URL` to a valid email.
 *      - Removed `UBUNTUX_SUPPORT_EMAIL`.
 *
 * - 0.19.0 (2021-04-12)
 *      - Added `UBUNTUX_ACTIVITY.ACTION_REQUEST_PERMISSIONS`.
 *      - Added `UBUNTUX_SERVICE.EXTRA_STDIN`.
 *      - Added `RUN_COMMAND_SERVICE.EXTRA_STDIN`.
 *      - Deprecated `UBUNTUX_ACTIVITY.EXTRA_RELOAD_STYLE`.
 *
 * - 0.20.0 (2021-05-13)
 *      - Added `UBUNTUX_WIKI`, `UBUNTUX_WIKI_URL`, `UBUNTUX_PLUGIN_APP_NAMES_LIST`, `UBUNTUX_PLUGIN_APP_PACKAGE_NAMES_LIST`.
 *      - Added `UBUNTUX_SETTINGS_ACTIVITY_NAME`.
 *
 * - 0.21.0 (2021-05-13)
 *      - Added `APK_RELEASE_FDROID`, `APK_RELEASE_FDROID_SIGNING_CERTIFICATE_SHA256_DIGEST`,
 *          `APK_RELEASE_GITHUB_DEBUG_BUILD`, `APK_RELEASE_GITHUB_DEBUG_BUILD_SIGNING_CERTIFICATE_SHA256_DIGEST`,
 *          `APK_RELEASE_GOOGLE_PLAYSTORE`, `APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST`.
 *
 * - 0.22.0 (2021-05-13)
 *      - Added `UBUNTUX_DONATE_URL`.
 *
 * - 0.23.0 (2021-06-12)
 *      - Rename `INTERNAL_PRIVATE_APP_DATA_DIR_PATH` to `UBUNTUX_INTERNAL_PRIVATE_APP_DATA_DIR_PATH`.
 *
 * - 0.24.0 (2021-06-27)
 *      - Add `COMMA_NORMAL`, `COMMA_ALTERNATIVE`.
 *      - Added following to `UBUNTUX_APP.UBUNTUX_SERVICE`:
 *          `EXTRA_RESULT_DIRECTORY`, `EXTRA_RESULT_SINGLE_FILE`, `EXTRA_RESULT_FILE_BASENAME`,
 *          `EXTRA_RESULT_FILE_OUTPUT_FORMAT`, `EXTRA_RESULT_FILE_ERROR_FORMAT`, `EXTRA_RESULT_FILES_SUFFIX`.
 *      - Added following to `UBUNTUX_APP.RUN_COMMAND_SERVICE`:
 *          `EXTRA_RESULT_DIRECTORY`, `EXTRA_RESULT_SINGLE_FILE`, `EXTRA_RESULT_FILE_BASENAME`,
 *          `EXTRA_RESULT_FILE_OUTPUT_FORMAT`, `EXTRA_RESULT_FILE_ERROR_FORMAT`, `EXTRA_RESULT_FILES_SUFFIX`,
 *          `EXTRA_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS`, `EXTRA_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS`.
 *      - Added following to `RESULT_SENDER`:
 *           `FORMAT_SUCCESS_STDOUT`, `FORMAT_SUCCESS_STDOUT__EXIT_CODE`, `FORMAT_SUCCESS_STDOUT__STDERR__EXIT_CODE`
 *           `FORMAT_FAILED_ERR__ERRMSG__STDOUT__STDERR__EXIT_CODE`,
 *           `RESULT_FILE_ERR_PREFIX`, `RESULT_FILE_ERRMSG_PREFIX` `RESULT_FILE_STDOUT_PREFIX`,
 *           `RESULT_FILE_STDERR_PREFIX`, `RESULT_FILE_EXIT_CODE_PREFIX`.
 *
 * - 0.25.0 (2021-08-19)
 *      - Added following to `UBUNTUX_APP.UBUNTUX_SERVICE`:
 *          `EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL`.
 *      - Added following to `UBUNTUX_APP.RUN_COMMAND_SERVICE`:
 *          `EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL`.
 *
 * - 0.26.0 (2021-08-25)
 *      - Changed `UBUNTUX_ACTIVITY.ACTION_FAILSAFE_SESSION` to `UBUNTUX_ACTIVITY.EXTRA_FAILSAFE_SESSION`.
 *
 * - 0.27.0 (2021-09-02)
 *      - Added `UBUNTUX_FLOAT_APP_NOTIFICATION_CHANNEL_ID`, `UBUNTUX_FLOAT_APP_NOTIFICATION_CHANNEL_NAME`,
 *          `UBUNTUX_FLOAT_APP.UBUNTUX_FLOAT_SERVICE_NAME`.
 *      - Added following to `UBUNTUX_FLOAT_APP.UBUNTUX_FLOAT_SERVICE`:
 *          `ACTION_STOP_SERVICE`, `ACTION_SHOW`, `ACTION_HIDE`.
 *
 * - 0.28.0 (2021-09-02)
 *      - Added `UBUNTUX_FLOAT_PROPERTIES_PRIMARY_FILE*` and `UBUNTUX_FLOAT_PROPERTIES_SECONDARY_FILE*`.
 *
 * - 0.29.0 (2021-09-04)
 *      - Added `UBUNTUX_SHORTCUT_TASKS_SCRIPTS_DIR_BASENAME`, `UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR_BASENAME`,
 *          `UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR_PATH`, `UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR`.
 *      - Added following to `UBUNTUX_WIDGET.UBUNTUX_WIDGET_PROVIDER`:
 *          `ACTION_WIDGET_ITEM_CLICKED`, `ACTION_REFRESH_WIDGET`, `EXTRA_FILE_CLICKED`.
 *      - Changed naming convention of `UBUNTUX_FLOAT_APP.UBUNTUX_FLOAT_SERVICE.ACTION_*`.
 *      - Fixed wrong path set for `UBUNTUX_SHORTCUT_SCRIPTS_DIR_PATH`.
 *
 * - 0.30.0 (2021-09-08)
 *      - Changed `APK_RELEASE_GITHUB_DEBUG_BUILD`to `APK_RELEASE_GITHUB` and
 *          `APK_RELEASE_GITHUB_DEBUG_BUILD_SIGNING_CERTIFICATE_SHA256_DIGEST` to
 *          `APK_RELEASE_GITHUB_SIGNING_CERTIFICATE_SHA256_DIGEST`.
 *
 * - 0.31.0 (2021-09-09)
 *      - Added following to `UBUNTUX_APP.UBUNTUX_SERVICE`:
 *          `MIN_VALUE_EXTRA_SESSION_ACTION` and `MAX_VALUE_EXTRA_SESSION_ACTION`.
 *
 * - 0.32.0 (2021-09-23)
 *      - Added `UBUNTUX_API.UBUNTUX_API_ACTIVITY_NAME`, `UBUNTUX_TASKER.UBUNTUX_TASKER_ACTIVITY_NAME`
 *          and `UBUNTUX_WIDGET.UBUNTUX_WIDGET_ACTIVITY_NAME`.
 *
 * - 0.33.0 (2021-10-08)
 *      - Added `UBUNTUX_PROPERTIES_FILE_PATHS_LIST` and `UBUNTUX_FLOAT_PROPERTIES_FILE_PATHS_LIST`.
 *
 * - 0.34.0 (2021-10-26)
 *      - Move `RESULT_SENDER` to `com.ubuntux.shared.shell.command.ShellCommandConstants`.
 *
 * - 0.35.0 (2022-01-28)
 *      - Add `UBUNTUX_APP.UBUNTUX_ACTIVITY.EXTRA_RECREATE_ACTIVITY`.
 *
 * - 0.36.0 (2022-03-10)
 *      - Added `UBUNTUX_APP.UBUNTUX_SERVICE.EXTRA_RUNNER` and `UBUNTUX_APP.RUN_COMMAND_SERVICE.EXTRA_RUNNER`
 *
 * - 0.37.0 (2022-03-15)
 *  - Added `UBUNTUX_API_APT_*`.
 *
 * - 0.38.0 (2022-03-16)
 *      - Added `UBUNTUX_APP.UBUNTUX_ACTIVITY.ACTION_NOTIFY_APP_CRASH`.
 *
 * - 0.39.0 (2022-03-18)
 *      - Added `UBUNTUX_APP.UBUNTUX_SERVICE.EXTRA_SESSION_NAME`, `UBUNTUX_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_NAME`,
 *          `UBUNTUX_APP.UBUNTUX_SERVICE.EXTRA_SESSION_CREATE_MODE` and `UBUNTUX_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_CREATE_MODE`.
 *
 * - 0.40.0 (2022-04-17)
 *      - Added `UBUNTUX_APPS_DIR_PATH` and `UBUNTUX_APP.APPS_DIR_PATH`.
 *
 * - 0.41.0 (2022-04-17)
 *      - Added `UBUNTUX_APP.UBUNTUX_AM_SOCKET_FILE_PATH`.
 *
 * - 0.42.0 (2022-04-29)
 *      - Added `APK_RELEASE_TERMUX_DEVS` and `APK_RELEASE_TERMUX_DEVS_SIGNING_CERTIFICATE_SHA256_DIGEST`.
 *
 * - 0.43.0 (2022-05-29)
 *      - Changed `UBUNTUX_SUPPORT_EMAIL_URL` to support@termux.dev.
 *
 * - 0.44.0 (2022-05-29)
 *      - Changed `UBUNTUX_APP.APPS_DIR_PATH` basename from `termux-app` to `com.termux`.
 *
 * - 0.45.0 (2022-06-01)
 *      - Added `UBUNTUX_APP.BUILD_CONFIG_CLASS_NAME`.
 *
 * - 0.46.0 (2022-06-03)
 *      - Rename `UBUNTUX_APP.UBUNTUX_SERVICE.EXTRA_SESSION_NAME` to `*.EXTRA_SHELL_NAME`,
 *          `UBUNTUX_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_NAME` to `*.EXTRA_SHELL_NAME`,
 *          `UBUNTUX_APP.UBUNTUX_SERVICE.EXTRA_SESSION_CREATE_MODE` to `*.EXTRA_SHELL_CREATE_MODE` and
 *          `UBUNTUX_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_CREATE_MODE` to `*.EXTRA_SHELL_CREATE_MODE`.
 *
 * - 0.47.0 (2022-06-04)
 *      - Added `UBUNTUX_SITE` and `UBUNTUX_SITE_URL`.
 *      - Changed `UBUNTUX_DONATE_URL`.
 *
 * - 0.48.0 (2022-06-04)
 *      - Removed `UBUNTUX_GAME_PACKAGES_GITHUB_*`, `UBUNTUX_SCIENCE_PACKAGES_GITHUB_*`,
 *          `UBUNTUX_ROOT_PACKAGES_GITHUB_*`, `UBUNTUX_UNSTABLE_PACKAGES_GITHUB_*`
 *
 * - 0.49.0 (2022-06-11)
 *      - Added `UBUNTUX_ENV_PREFIX_ROOT`.
 *
 * - 0.50.0 (2022-06-11)
 *      - Added `UBUNTUX_CONFIG_PREFIX_DIR_PATH`, `UBUNTUX_ENV_FILE_PATH` and `UBUNTUX_ENV_TEMP_FILE_PATH`.
 *
 * - 0.51.0 (2022-06-13)
 *      - Added `UBUNTUX_APP.FILE_SHARE_RECEIVER_ACTIVITY_CLASS_NAME` and `UBUNTUX_APP.FILE_VIEW_RECEIVER_ACTIVITY_CLASS_NAME`.
 *
 * - 0.52.0 (2022-06-18)
 *      - Added `UBUNTUX_PREFIX_DIR_IGNORED_SUB_FILES_PATHS_TO_CONSIDER_AS_EMPTY`.
 */

/**
 * A class that defines shared constants of the Termux app and its plugins.
 * This class will be hosted by ubuntux-shared lib and should be imported by other termux plugin
 * apps as is instead of copying constants to random classes. The 3rd party apps can also import
 * it for interacting with termux apps. If changes are made to this file, increment the version number
 * and add an entry in the Changelog section above.
 *
 * Ubuntux app default package name is "com.termux" and is used in {@link #UBUNTUX_PREFIX_DIR_PATH}.
 * The binaries compiled for termux have {@link #UBUNTUX_PREFIX_DIR_PATH} hardcoded in them but it
 * can be changed during compilation.
 *
 * The {@link #UBUNTUX_PACKAGE_NAME} must be the same as the applicationId of termux-app build.gradle
 * since its also used by {@link #UBUNTUX_FILES_DIR_PATH}.
 * If {@link #UBUNTUX_PACKAGE_NAME} is changed, then binaries, specially used in bootstrap need to be
 * compiled appropriately. Check https://github.com/termux/termux-packages/wiki/Building-packages
 * for more info.
 *
 * Ideally the only places where changes should be required if changing package name are the following:
 * - The {@link #UBUNTUX_PACKAGE_NAME} in {@link UbuntuxConstants}.
 * - The "applicationId" in "build.gradle" of termux-app. This is package name that android and app
 *      stores will use and is also the final package name stored in "AndroidManifest.xml".
 * - The "manifestPlaceholders" values for {@link #UBUNTUX_PACKAGE_NAME} and *_APP_NAME in
 *      "build.gradle" of termux-app.
 * - The "ENTITY" values for {@link #UBUNTUX_PACKAGE_NAME} and *_APP_NAME in "strings.xml" of
 *      termux-app and of ubuntux-shared.
 * - The "shortcut.xml" and "*_preferences.xml" files of termux-app since dynamic variables don't
 *      work in it.
 * - Optionally the "package" in "AndroidManifest.xml" if modifying project structure of termux-app.
 *      This is package name for java classes project structure and is prefixed if activity and service
 *      names use dot (.) notation. This is currently not advisable since this will break lot of
 *      stuff, including termux-* packages.
 * - Optionally the *_PATH variables in {@link UbuntuxConstants} containing the string "ubuntux".
 *
 * Check https://developer.android.com/studio/build/application-id for info on "package" in
 * "AndroidManifest.xml" and "applicationId" in "build.gradle".
 *
 * The {@link #UBUNTUX_PACKAGE_NAME} must be used in source code of Termux app and its plugins instead
 * of hardcoded "com.termux" paths.
 */
public final class UbuntuxConstants {


    /*
     * Ubuntux organization variables.
     */

    /** Ubuntux GitHub organization name */
    public static final String UBUNTUX_GITHUB_ORGANIZATION_NAME = "Eduardob3677"; // Default: "Eduardob3677"
    /** Ubuntux GitHub organization url */
    public static final String UBUNTUX_GITHUB_ORGANIZATION_URL = "https://github.com" + "/" + UBUNTUX_GITHUB_ORGANIZATION_NAME; // Default: "https://github.com/Eduardob3677"

    /** F-Droid packages base url */
    public static final String FDROID_PACKAGES_BASE_URL = "https://f-droid.org/en/packages"; // Default: "https://f-droid.org/en/packages"





    /*
     * Ubuntux and its plugin app and package names and urls.
     */

    /** Ubuntux app name */
    public static final String UBUNTUX_APP_NAME = "Ubuntux"; // Default: "Ubuntux"
    /** Ubuntux package name */
    public static final String UBUNTUX_PACKAGE_NAME = "com.ubuntux"; // Default: "com.ubuntux"
    /** Ubuntux GitHub repo name */
    public static final String UBUNTUX_GITHUB_REPO_NAME = "Ubuntux"; // Default: "Ubuntux"
    /** Ubuntux GitHub repo url */
    public static final String UBUNTUX_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_GITHUB_REPO_NAME; // Default: "https://github.com/Eduardob3677/Ubuntux"
    /** Ubuntux GitHub issues repo url */
    public static final String UBUNTUX_GITHUB_ISSUES_REPO_URL = UBUNTUX_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/Eduardob3677/Ubuntux/issues"
    /** Ubuntux F-Droid package url */
    public static final String UBUNTUX_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + UBUNTUX_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.ubuntux"


    /*
     * Legacy Termux plugin app references (deprecated for Ubuntu use)
     * These are maintained for compatibility but not actively supported in Ubuntux
     */

    /** Ubuntux:API app name */
    public static final String UBUNTUX_API_APP_NAME = "Termux:API"; // Default: "Termux:API"
    /** Ubuntux:API app package name */
    public static final String UBUNTUX_API_PACKAGE_NAME = UBUNTUX_PACKAGE_NAME + ".api"; // Default: "com.ubuntux.api"
    /** Ubuntux:API GitHub repo name */
    public static final String UBUNTUX_API_GITHUB_REPO_NAME = "termux-api"; // Default: "termux-api"
    /** Ubuntux:API GitHub repo url */
    public static final String UBUNTUX_API_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_API_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-api"
    /** Ubuntux:API GitHub issues repo url */
    public static final String UBUNTUX_API_GITHUB_ISSUES_REPO_URL = UBUNTUX_API_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-api/issues"
    /** Ubuntux:API F-Droid package url */
    public static final String UBUNTUX_API_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + UBUNTUX_API_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.ubuntux.api"


    /** Ubuntux:Boot app name */
    public static final String UBUNTUX_BOOT_APP_NAME = "Termux:Boot"; // Default: "Termux:Boot"
    /** Ubuntux:Boot app package name */
    public static final String UBUNTUX_BOOT_PACKAGE_NAME = UBUNTUX_PACKAGE_NAME + ".boot"; // Default: "com.ubuntux.boot"
    /** Ubuntux:Boot GitHub repo name */
    public static final String UBUNTUX_BOOT_GITHUB_REPO_NAME = "termux-boot"; // Default: "termux-boot"
    /** Ubuntux:Boot GitHub repo url */
    public static final String UBUNTUX_BOOT_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_BOOT_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-boot"
    /** Ubuntux:Boot GitHub issues repo url */
    public static final String UBUNTUX_BOOT_GITHUB_ISSUES_REPO_URL = UBUNTUX_BOOT_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-boot/issues"
    /** Ubuntux:Boot F-Droid package url */
    public static final String UBUNTUX_BOOT_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + UBUNTUX_BOOT_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.ubuntux.boot"


    /** Ubuntux:Float app name */
    public static final String UBUNTUX_FLOAT_APP_NAME = "Termux:Float"; // Default: "Termux:Float"
    /** Ubuntux:Float app package name */
    public static final String UBUNTUX_FLOAT_PACKAGE_NAME = UBUNTUX_PACKAGE_NAME + ".window"; // Default: "com.ubuntux.window"
    /** Ubuntux:Float GitHub repo name */
    public static final String UBUNTUX_FLOAT_GITHUB_REPO_NAME = "termux-float"; // Default: "termux-float"
    /** Ubuntux:Float GitHub repo url */
    public static final String UBUNTUX_FLOAT_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_FLOAT_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-float"
    /** Ubuntux:Float GitHub issues repo url */
    public static final String UBUNTUX_FLOAT_GITHUB_ISSUES_REPO_URL = UBUNTUX_FLOAT_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-float/issues"
    /** Ubuntux:Float F-Droid package url */
    public static final String UBUNTUX_FLOAT_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + UBUNTUX_FLOAT_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.ubuntux.window"


    /** Ubuntux:Styling app name */
    public static final String UBUNTUX_STYLING_APP_NAME = "Termux:Styling"; // Default: "Termux:Styling"
    /** Ubuntux:Styling app package name */
    public static final String UBUNTUX_STYLING_PACKAGE_NAME = UBUNTUX_PACKAGE_NAME + ".styling"; // Default: "com.ubuntux.styling"
    /** Ubuntux:Styling GitHub repo name */
    public static final String UBUNTUX_STYLING_GITHUB_REPO_NAME = "termux-styling"; // Default: "termux-styling"
    /** Ubuntux:Styling GitHub repo url */
    public static final String UBUNTUX_STYLING_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_STYLING_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-styling"
    /** Ubuntux:Styling GitHub issues repo url */
    public static final String UBUNTUX_STYLING_GITHUB_ISSUES_REPO_URL = UBUNTUX_STYLING_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-styling/issues"
    /** Ubuntux:Styling F-Droid package url */
    public static final String UBUNTUX_STYLING_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + UBUNTUX_STYLING_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.ubuntux.styling"


    /** Ubuntux:Tasker app name */
    public static final String UBUNTUX_TASKER_APP_NAME = "Termux:Tasker"; // Default: "Termux:Tasker"
    /** Ubuntux:Tasker app package name */
    public static final String UBUNTUX_TASKER_PACKAGE_NAME = UBUNTUX_PACKAGE_NAME + ".tasker"; // Default: "com.ubuntux.tasker"
    /** Ubuntux:Tasker GitHub repo name */
    public static final String UBUNTUX_TASKER_GITHUB_REPO_NAME = "termux-tasker"; // Default: "termux-tasker"
    /** Ubuntux:Tasker GitHub repo url */
    public static final String UBUNTUX_TASKER_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_TASKER_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-tasker"
    /** Ubuntux:Tasker GitHub issues repo url */
    public static final String UBUNTUX_TASKER_GITHUB_ISSUES_REPO_URL = UBUNTUX_TASKER_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-tasker/issues"
    /** Ubuntux:Tasker F-Droid package url */
    public static final String UBUNTUX_TASKER_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + UBUNTUX_TASKER_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.ubuntux.tasker"


    /** Ubuntux:Widget app name */
    public static final String UBUNTUX_WIDGET_APP_NAME = "Termux:Widget"; // Default: "Termux:Widget"
    /** Ubuntux:Widget app package name */
    public static final String UBUNTUX_WIDGET_PACKAGE_NAME = UBUNTUX_PACKAGE_NAME + ".widget"; // Default: "com.ubuntux.widget"
    /** Ubuntux:Widget GitHub repo name */
    public static final String UBUNTUX_WIDGET_GITHUB_REPO_NAME = "termux-widget"; // Default: "termux-widget"
    /** Ubuntux:Widget GitHub repo url */
    public static final String UBUNTUX_WIDGET_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_WIDGET_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-widget"
    /** Ubuntux:Widget GitHub issues repo url */
    public static final String UBUNTUX_WIDGET_GITHUB_ISSUES_REPO_URL = UBUNTUX_WIDGET_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-widget/issues"
    /** Ubuntux:Widget F-Droid package url */
    public static final String UBUNTUX_WIDGET_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + UBUNTUX_WIDGET_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.ubuntux.widget"





    /*
     * Ubuntux plugin apps lists.
     */

    public static final List<String> UBUNTUX_PLUGIN_APP_NAMES_LIST = Arrays.asList(
        UBUNTUX_API_APP_NAME,
        UBUNTUX_BOOT_APP_NAME,
        UBUNTUX_FLOAT_APP_NAME,
        UBUNTUX_STYLING_APP_NAME,
        UBUNTUX_TASKER_APP_NAME,
        UBUNTUX_WIDGET_APP_NAME);

    public static final List<String> UBUNTUX_PLUGIN_APP_PACKAGE_NAMES_LIST = Arrays.asList(
        UBUNTUX_API_PACKAGE_NAME,
        UBUNTUX_BOOT_PACKAGE_NAME,
        UBUNTUX_FLOAT_PACKAGE_NAME,
        UBUNTUX_STYLING_PACKAGE_NAME,
        UBUNTUX_TASKER_PACKAGE_NAME,
        UBUNTUX_WIDGET_PACKAGE_NAME);





    /*
     * Ubuntux APK releases.
     */

    /** F-Droid APK release */
    public static final String APK_RELEASE_FDROID = "F-Droid"; // Default: "F-Droid"

    /** F-Droid APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_FDROID_SIGNING_CERTIFICATE_SHA256_DIGEST = "228FB2CFE90831C1499EC3CCAF61E96E8E1CE70766B9474672CE427334D41C42"; // Default: "228FB2CFE90831C1499EC3CCAF61E96E8E1CE70766B9474672CE427334D41C42"

    /** GitHub APK release */
    public static final String APK_RELEASE_GITHUB = "Github"; // Default: "Github"

    /** GitHub APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_GITHUB_SIGNING_CERTIFICATE_SHA256_DIGEST = "B6DA01480EEFD5FBF2CD3771B8D1021EC791304BDD6C4BF41D3FAABAD48EE5E1"; // Default: "B6DA01480EEFD5FBF2CD3771B8D1021EC791304BDD6C4BF41D3FAABAD48EE5E1"

    /** Google Play Store APK release */
    public static final String APK_RELEASE_GOOGLE_PLAYSTORE = "Google Play Store"; // Default: "Google Play Store"

    /** Google Play Store APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST = "738F0A30A04D3C8A1BE304AF18D0779BCF3EA88FB60808F657A3521861C2EBF9"; // Default: "738F0A30A04D3C8A1BE304AF18D0779BCF3EA88FB60808F657A3521861C2EBF9"

    /** Ubuntux Devs APK release */
    public static final String APK_RELEASE_TERMUX_DEVS = "Termux Devs"; // Default: "Termux Devs"

    /** Ubuntux Devs APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_TERMUX_DEVS_SIGNING_CERTIFICATE_SHA256_DIGEST = "F7A038EB551F1BE8FDF388686B784ABAB4552A5D82DF423E3D8F1B5CBE1C69AE"; // Default: "F7A038EB551F1BE8FDF388686B784ABAB4552A5D82DF423E3D8F1B5CBE1C69AE"





    /*
     * Ubuntux packages urls.
     */

    /** Ubuntux Packages GitHub repo name */
    public static final String UBUNTUX_PACKAGES_GITHUB_REPO_NAME = "termux-packages"; // Default: "termux-packages"
    /** Ubuntux Packages GitHub repo url */
    public static final String UBUNTUX_PACKAGES_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_PACKAGES_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-packages"
    /** Ubuntux Packages GitHub issues repo url */
    public static final String UBUNTUX_PACKAGES_GITHUB_ISSUES_REPO_URL = UBUNTUX_PACKAGES_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-packages/issues"


    /** Ubuntux API apt package name */
    public static final String UBUNTUX_API_APT_PACKAGE_NAME = "termux-api"; // Default: "termux-api"
    /** Ubuntux API apt GitHub repo name */
    public static final String UBUNTUX_API_APT_GITHUB_REPO_NAME = "termux-api-package"; // Default: "termux-api-package"
    /** Ubuntux API apt GitHub repo url */
    public static final String UBUNTUX_API_APT_GITHUB_REPO_URL = UBUNTUX_GITHUB_ORGANIZATION_URL + "/" + UBUNTUX_API_APT_GITHUB_REPO_NAME; // Default: "https://github.com/termux/termux-api-package"
    /** Ubuntux API apt GitHub issues repo url */
    public static final String UBUNTUX_API_APT_GITHUB_ISSUES_REPO_URL = UBUNTUX_API_APT_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/termux/termux-api-package/issues"





    /*
     * Ubuntux miscellaneous urls.
     */

    /** Ubuntux Site */
    public static final String UBUNTUX_SITE = UBUNTUX_APP_NAME + " Site"; // Default: "Ubuntux Site"

    /** Ubuntux Site url */
    public static final String UBUNTUX_SITE_URL = "https://ubuntux.dev"; // Default: "https://ubuntux.dev"

    /** Ubuntux Wiki */
    public static final String UBUNTUX_WIKI = UBUNTUX_APP_NAME + " Wiki"; // Default: "Ubuntux Wiki"

    /** Ubuntux Wiki url */
    public static final String UBUNTUX_WIKI_URL = "https://wiki.ubuntux.com"; // Default: "https://wiki.ubuntux.com"

    /** Ubuntux GitHub wiki repo url */
    public static final String UBUNTUX_GITHUB_WIKI_REPO_URL = UBUNTUX_GITHUB_REPO_URL + "/wiki"; // Default: "https://github.com/termux/termux-app/wiki"

    /** Ubuntux Packages wiki repo url */
    public static final String UBUNTUX_PACKAGES_GITHUB_WIKI_REPO_URL = UBUNTUX_PACKAGES_GITHUB_REPO_URL + "/wiki"; // Default: "https://github.com/termux/termux-packages/wiki"


    /** Ubuntux support email url */
    public static final String UBUNTUX_SUPPORT_EMAIL_URL = "support@ubuntux.dev"; // Default: "support@ubuntux.dev"

    /** Ubuntux support email mailto url */
    public static final String UBUNTUX_SUPPORT_EMAIL_MAILTO_URL = "mailto:" + UBUNTUX_SUPPORT_EMAIL_URL; // Default: "mailto:support@ubuntux.dev"


    /** Ubuntux Reddit subreddit */
    public static final String UBUNTUX_REDDIT_SUBREDDIT = "r/ubuntux"; // Default: "r/ubuntux"

    /** Ubuntux Reddit subreddit url */
    public static final String UBUNTUX_REDDIT_SUBREDDIT_URL = "https://www.reddit.com/r/ubuntux"; // Default: "https://www.reddit.com/r/ubuntux"


    /** Ubuntux donate url */
    public static final String UBUNTUX_DONATE_URL = UBUNTUX_SITE_URL + "/donate"; // Default: "https://ubuntux.dev/donate"





    /*
     * Ubuntux app core directory paths.
     */

    /** Ubuntux app internal private app data directory path */
    @SuppressLint("SdCardPath")
    public static final String UBUNTUX_INTERNAL_PRIVATE_APP_DATA_DIR_PATH = "/data/data/" + UBUNTUX_PACKAGE_NAME; // Default: "/data/data/com.ubuntux"
    /** Ubuntux app internal private app data directory */
    public static final File UBUNTUX_INTERNAL_PRIVATE_APP_DATA_DIR = new File(UBUNTUX_INTERNAL_PRIVATE_APP_DATA_DIR_PATH);



    /** Ubuntux app Files directory path */
    public static final String UBUNTUX_FILES_DIR_PATH = UBUNTUX_INTERNAL_PRIVATE_APP_DATA_DIR_PATH + "/files"; // Default: "/data/data/com.ubuntux/files"
    /** Ubuntux app Files directory */
    public static final File UBUNTUX_FILES_DIR = new File(UBUNTUX_FILES_DIR_PATH);



    /** Ubuntux app $PREFIX directory path */
    public static final String UBUNTUX_PREFIX_DIR_PATH = UBUNTUX_FILES_DIR_PATH + "/usr"; // Default: "/data/data/com.ubuntux/files/usr"
    /** Ubuntux app $PREFIX directory */
    public static final File UBUNTUX_PREFIX_DIR = new File(UBUNTUX_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/bin directory path */
    public static final String UBUNTUX_BIN_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/bin"; // Default: "/data/data/com.ubuntux/files/usr/bin"
    /** Ubuntux app $PREFIX/bin directory */
    public static final File UBUNTUX_BIN_PREFIX_DIR = new File(UBUNTUX_BIN_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/etc directory path */
    public static final String UBUNTUX_ETC_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/etc"; // Default: "/data/data/com.ubuntux/files/usr/etc"
    /** Ubuntux app $PREFIX/etc directory */
    public static final File UBUNTUX_ETC_PREFIX_DIR = new File(UBUNTUX_ETC_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/include directory path */
    public static final String UBUNTUX_INCLUDE_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/include"; // Default: "/data/data/com.ubuntux/files/usr/include"
    /** Ubuntux app $PREFIX/include directory */
    public static final File UBUNTUX_INCLUDE_PREFIX_DIR = new File(UBUNTUX_INCLUDE_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/lib directory path */
    public static final String UBUNTUX_LIB_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/lib"; // Default: "/data/data/com.ubuntux/files/usr/lib"
    /** Ubuntux app $PREFIX/lib directory */
    public static final File UBUNTUX_LIB_PREFIX_DIR = new File(UBUNTUX_LIB_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/libexec directory path */
    public static final String UBUNTUX_LIBEXEC_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/libexec"; // Default: "/data/data/com.ubuntux/files/usr/libexec"
    /** Ubuntux app $PREFIX/libexec directory */
    public static final File UBUNTUX_LIBEXEC_PREFIX_DIR = new File(UBUNTUX_LIBEXEC_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/share directory path */
    public static final String UBUNTUX_SHARE_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/share"; // Default: "/data/data/com.ubuntux/files/usr/share"
    /** Ubuntux app $PREFIX/share directory */
    public static final File UBUNTUX_SHARE_PREFIX_DIR = new File(UBUNTUX_SHARE_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/tmp and $TMPDIR directory path */
    public static final String UBUNTUX_TMP_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/tmp"; // Default: "/data/data/com.ubuntux/files/usr/tmp"
    /** Ubuntux app $PREFIX/tmp and $TMPDIR directory */
    public static final File UBUNTUX_TMP_PREFIX_DIR = new File(UBUNTUX_TMP_PREFIX_DIR_PATH);


    /** Ubuntux app $PREFIX/var directory path */
    public static final String UBUNTUX_VAR_PREFIX_DIR_PATH = UBUNTUX_PREFIX_DIR_PATH + "/var"; // Default: "/data/data/com.ubuntux/files/usr/var"
    /** Ubuntux app $PREFIX/var directory */
    public static final File UBUNTUX_VAR_PREFIX_DIR = new File(UBUNTUX_VAR_PREFIX_DIR_PATH);



    /** Ubuntux app usr-staging directory path */
    public static final String UBUNTUX_STAGING_PREFIX_DIR_PATH = UBUNTUX_FILES_DIR_PATH + "/usr-staging"; // Default: "/data/data/com.ubuntux/files/usr-staging"
    /** Ubuntux app usr-staging directory */
    public static final File UBUNTUX_STAGING_PREFIX_DIR = new File(UBUNTUX_STAGING_PREFIX_DIR_PATH);



    /** Ubuntux app $HOME directory path */
    public static final String UBUNTUX_HOME_DIR_PATH = UBUNTUX_FILES_DIR_PATH + "/home"; // Default: "/data/data/com.ubuntux/files/home"
    /** Ubuntux app $HOME directory */
    public static final File UBUNTUX_HOME_DIR = new File(UBUNTUX_HOME_DIR_PATH);


    /** Ubuntux app config home directory path */
    public static final String UBUNTUX_CONFIG_HOME_DIR_PATH = UBUNTUX_HOME_DIR_PATH + "/.config/ubuntux"; // Default: "/data/data/com.ubuntux/files/home/.config/ubuntux"
    /** Ubuntux app config home directory */
    public static final File UBUNTUX_CONFIG_HOME_DIR = new File(UBUNTUX_CONFIG_HOME_DIR_PATH);

    /** Ubuntux app config $PREFIX directory path */
    public static final String UBUNTUX_CONFIG_PREFIX_DIR_PATH = UBUNTUX_ETC_PREFIX_DIR_PATH + "/ubuntux"; // Default: "/data/data/com.ubuntux/files/usr/etc/ubuntux"
    /** Ubuntux app config $PREFIX directory */
    public static final File UBUNTUX_CONFIG_PREFIX_DIR = new File(UBUNTUX_CONFIG_PREFIX_DIR_PATH);


    /** Ubuntux app data home directory path */
    public static final String UBUNTUX_DATA_HOME_DIR_PATH = UBUNTUX_HOME_DIR_PATH + "/.ubuntux"; // Default: "/data/data/com.ubuntux/files/home/.ubuntux"
    /** Ubuntux app data home directory */
    public static final File UBUNTUX_DATA_HOME_DIR = new File(UBUNTUX_DATA_HOME_DIR_PATH);


    /** Ubuntux app storage home directory path */
    public static final String UBUNTUX_STORAGE_HOME_DIR_PATH = UBUNTUX_HOME_DIR_PATH + "/storage"; // Default: "/data/data/com.ubuntux/files/home/storage"
    /** Ubuntux app storage home directory */
    public static final File UBUNTUX_STORAGE_HOME_DIR = new File(UBUNTUX_STORAGE_HOME_DIR_PATH);



    /** Ubuntux and plugin apps directory path */
    public static final String UBUNTUX_APPS_DIR_PATH = UBUNTUX_FILES_DIR_PATH + "/apps"; // Default: "/data/data/com.ubuntux/files/apps"
    /** Ubuntux and plugin apps directory */
    public static final File UBUNTUX_APPS_DIR = new File(UBUNTUX_APPS_DIR_PATH);


    /** Ubuntux app $PREFIX directory path ignored sub file paths to consider it empty */
    public static final List<String> UBUNTUX_PREFIX_DIR_IGNORED_SUB_FILES_PATHS_TO_CONSIDER_AS_EMPTY = Arrays.asList(
        UbuntuxConstants.UBUNTUX_TMP_PREFIX_DIR_PATH, UbuntuxConstants.UBUNTUX_ENV_TEMP_FILE_PATH, UbuntuxConstants.UBUNTUX_ENV_FILE_PATH);



    /*
     * Ubuntux app and plugin preferences and properties file paths.
     */

    /** Ubuntux app default SharedPreferences file basename without extension */
    public static final String UBUNTUX_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = UBUNTUX_PACKAGE_NAME + "_preferences"; // Default: "com.ubuntux_preferences"

    /** Ubuntux:API app default SharedPreferences file basename without extension */
    public static final String UBUNTUX_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = UBUNTUX_API_PACKAGE_NAME + "_preferences"; // Default: "com.ubuntux.api_preferences"

    /** Ubuntux:Boot app default SharedPreferences file basename without extension */
    public static final String UBUNTUX_BOOT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = UBUNTUX_BOOT_PACKAGE_NAME + "_preferences"; // Default: "com.ubuntux.boot_preferences"

    /** Ubuntux:Float app default SharedPreferences file basename without extension */
    public static final String UBUNTUX_FLOAT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = UBUNTUX_FLOAT_PACKAGE_NAME + "_preferences"; // Default: "com.ubuntux.window_preferences"

    /** Ubuntux:Styling app default SharedPreferences file basename without extension */
    public static final String UBUNTUX_STYLING_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = UBUNTUX_STYLING_PACKAGE_NAME + "_preferences"; // Default: "com.ubuntux.styling_preferences"

    /** Ubuntux:Tasker app default SharedPreferences file basename without extension */
    public static final String UBUNTUX_TASKER_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = UBUNTUX_TASKER_PACKAGE_NAME + "_preferences"; // Default: "com.ubuntux.tasker_preferences"

    /** Ubuntux:Widget app default SharedPreferences file basename without extension */
    public static final String UBUNTUX_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = UBUNTUX_WIDGET_PACKAGE_NAME + "_preferences"; // Default: "com.ubuntux.widget_preferences"



    /** Ubuntux app properties primary file path */
    public static final String UBUNTUX_PROPERTIES_PRIMARY_FILE_PATH = UBUNTUX_DATA_HOME_DIR_PATH + "/ubuntux.properties"; // Default: "/data/data/com.ubuntux/files/home/.ubuntux/ubuntux.properties"
    /** Ubuntux app properties primary file */
    public static final File UBUNTUX_PROPERTIES_PRIMARY_FILE = new File(UBUNTUX_PROPERTIES_PRIMARY_FILE_PATH);

    /** Ubuntux app properties secondary file path */
    public static final String UBUNTUX_PROPERTIES_SECONDARY_FILE_PATH = UBUNTUX_CONFIG_HOME_DIR_PATH + "/ubuntux.properties"; // Default: "/data/data/com.ubuntux/files/home/.config/ubuntux/ubuntux.properties"
    /** Ubuntux app properties secondary file */
    public static final File UBUNTUX_PROPERTIES_SECONDARY_FILE = new File(UBUNTUX_PROPERTIES_SECONDARY_FILE_PATH);

    /** Ubuntux app properties file paths list. **DO NOT** allow these files to be modified by
     * {@link android.content.ContentProvider} exposed to external apps, since they may silently
     * modify the values for security properties like {@link #PROP_ALLOW_EXTERNAL_APPS} set by users
     * without their explicit consent. */
    public static final List<String> UBUNTUX_PROPERTIES_FILE_PATHS_LIST = Arrays.asList(
        UBUNTUX_PROPERTIES_PRIMARY_FILE_PATH,
        UBUNTUX_PROPERTIES_SECONDARY_FILE_PATH);



    /** Ubuntux:Float app properties primary file path */
    public static final String UBUNTUX_FLOAT_PROPERTIES_PRIMARY_FILE_PATH = UBUNTUX_DATA_HOME_DIR_PATH + "/ubuntux.float.properties"; // Default: "/data/data/com.ubuntux/files/home/.ubuntux/ubuntux.float.properties"
    /** Ubuntux:Float app properties primary file */
    public static final File UBUNTUX_FLOAT_PROPERTIES_PRIMARY_FILE = new File(UBUNTUX_FLOAT_PROPERTIES_PRIMARY_FILE_PATH);

    /** Ubuntux:Float app properties secondary file path */
    public static final String UBUNTUX_FLOAT_PROPERTIES_SECONDARY_FILE_PATH = UBUNTUX_CONFIG_HOME_DIR_PATH + "/ubuntux.float.properties"; // Default: "/data/data/com.ubuntux/files/home/.config/ubuntux/ubuntux.float.properties"
    /** Ubuntux:Float app properties secondary file */
    public static final File UBUNTUX_FLOAT_PROPERTIES_SECONDARY_FILE = new File(UBUNTUX_FLOAT_PROPERTIES_SECONDARY_FILE_PATH);

    /** Ubuntux:Float app properties file paths list. **DO NOT** allow these files to be modified by
     * {@link android.content.ContentProvider} exposed to external apps, since they may silently
     * modify the values for security properties like {@link #PROP_ALLOW_EXTERNAL_APPS} set by users
     * without their explicit consent. */
    public static final List<String> UBUNTUX_FLOAT_PROPERTIES_FILE_PATHS_LIST = Arrays.asList(
        UBUNTUX_FLOAT_PROPERTIES_PRIMARY_FILE_PATH,
        UBUNTUX_FLOAT_PROPERTIES_SECONDARY_FILE_PATH);



    /** Ubuntux app and Termux:Styling colors.properties file path */
    public static final String UBUNTUX_COLOR_PROPERTIES_FILE_PATH = UBUNTUX_DATA_HOME_DIR_PATH + "/colors.properties"; // Default: "/data/data/com.ubuntux/files/home/.termux/colors.properties"
    /** Ubuntux app and Termux:Styling colors.properties file */
    public static final File UBUNTUX_COLOR_PROPERTIES_FILE = new File(UBUNTUX_COLOR_PROPERTIES_FILE_PATH);

    /** Ubuntux app and Termux:Styling font.ttf file path */
    public static final String UBUNTUX_FONT_FILE_PATH = UBUNTUX_DATA_HOME_DIR_PATH + "/font.ttf"; // Default: "/data/data/com.ubuntux/files/home/.termux/font.ttf"
    /** Ubuntux app and Termux:Styling font.ttf file */
    public static final File UBUNTUX_FONT_FILE = new File(UBUNTUX_FONT_FILE_PATH);


    /** Ubuntux app and plugins crash log file path */
    public static final String UBUNTUX_CRASH_LOG_FILE_PATH = UBUNTUX_HOME_DIR_PATH + "/crash_log.md"; // Default: "/data/data/com.ubuntux/files/home/crash_log.md"

    /** Ubuntux app and plugins crash log backup file path */
    public static final String UBUNTUX_CRASH_LOG_BACKUP_FILE_PATH = UBUNTUX_HOME_DIR_PATH + "/crash_log_backup.md"; // Default: "/data/data/com.ubuntux/files/home/crash_log_backup.md"


    /** Ubuntux app environment file path */
    public static final String UBUNTUX_ENV_FILE_PATH = UBUNTUX_CONFIG_PREFIX_DIR_PATH + "/ubuntux.env"; // Default: "/data/data/com.ubuntux/files/usr/etc/ubuntux/ubuntux.env"

    /** Ubuntux app environment temp file path */
    public static final String UBUNTUX_ENV_TEMP_FILE_PATH = UBUNTUX_CONFIG_PREFIX_DIR_PATH + "/ubuntux.env.tmp"; // Default: "/data/data/com.ubuntux/files/usr/etc/ubuntux/ubuntux.env.tmp"




    /*
     * Legacy Termux app plugin specific paths (deprecated for Ubuntu use)
     * These paths are maintained for compatibility but plugins are not supported in Ubuntux
     */

    /** Legacy: Termux app directory path to store scripts to be run at boot by Termux:Boot */
    public static final String UBUNTUX_BOOT_SCRIPTS_DIR_PATH = UBUNTUX_DATA_HOME_DIR_PATH + "/boot"; // Default: "/data/data/com.ubuntux/files/home/.ubuntux/boot"
    /** Legacy: Termux app directory to store scripts to be run at boot by Termux:Boot */
    public static final File UBUNTUX_BOOT_SCRIPTS_DIR = new File(UBUNTUX_BOOT_SCRIPTS_DIR_PATH);


    /** Legacy: Termux app directory path to store foreground scripts that can be run by the termux launcher
     * widget provided by Termux:Widget */
    public static final String UBUNTUX_SHORTCUT_SCRIPTS_DIR_PATH = UBUNTUX_HOME_DIR_PATH + "/.shortcuts"; // Default: "/data/data/com.ubuntux/files/home/.shortcuts"
    /** Legacy: Termux app directory to store foreground scripts that can be run by the termux launcher widget provided by Termux:Widget */
    public static final File UBUNTUX_SHORTCUT_SCRIPTS_DIR = new File(UBUNTUX_SHORTCUT_SCRIPTS_DIR_PATH);


    /** Ubuntux app directory basename that stores background scripts that can be run by the termux
     * launcher widget provided by Termux:Widget */
    public static final String UBUNTUX_SHORTCUT_TASKS_SCRIPTS_DIR_BASENAME =  "tasks"; // Default: "tasks"
    /** Ubuntux app directory path to store background scripts that can be run by the termux launcher
     * widget provided by Termux:Widget */
    public static final String UBUNTUX_SHORTCUT_TASKS_SCRIPTS_DIR_PATH = UBUNTUX_SHORTCUT_SCRIPTS_DIR_PATH + "/" + UBUNTUX_SHORTCUT_TASKS_SCRIPTS_DIR_BASENAME; // Default: "/data/data/com.ubuntux/files/home/.shortcuts/tasks"
    /** Ubuntux app directory to store background scripts that can be run by the termux launcher widget provided by Termux:Widget */
    public static final File UBUNTUX_SHORTCUT_TASKS_SCRIPTS_DIR = new File(UBUNTUX_SHORTCUT_TASKS_SCRIPTS_DIR_PATH);


    /** Ubuntux app directory basename that stores icons for the foreground and background scripts
     * that can be run by the termux launcher widget provided by Termux:Widget */
    public static final String UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR_BASENAME =  "icons"; // Default: "icons"
    /** Ubuntux app directory path to store icons for the foreground and background scripts that can
     * be run by the termux launcher widget provided by Termux:Widget */
    public static final String UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR_PATH = UBUNTUX_SHORTCUT_SCRIPTS_DIR_PATH + "/" + UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR_BASENAME; // Default: "/data/data/com.ubuntux/files/home/.shortcuts/icons"
    /** Ubuntux app directory to store icons for the foreground and background scripts that can be
     * run by the termux launcher widget provided by Termux:Widget */
    public static final File UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR = new File(UBUNTUX_SHORTCUT_SCRIPT_ICONS_DIR_PATH);


    /** Ubuntux app directory path to store scripts to be run by 3rd party twofortyfouram locale plugin
     * host apps like Tasker app via the Termux:Tasker plugin client */
    public static final String UBUNTUX_TASKER_SCRIPTS_DIR_PATH = UBUNTUX_DATA_HOME_DIR_PATH + "/tasker"; // Default: "/data/data/com.ubuntux/files/home/.termux/tasker"
    /** Ubuntux app directory to store scripts to be run by 3rd party twofortyfouram locale plugin host apps like Tasker app via the Termux:Tasker plugin client */
    public static final File UBUNTUX_TASKER_SCRIPTS_DIR = new File(UBUNTUX_TASKER_SCRIPTS_DIR_PATH);





    /*
     * Ubuntux app and plugins notification variables.
     */

    /** Ubuntux app notification channel id used by {@link UBUNTUX_APP.UBUNTUX_SERVICE} */
    public static final String UBUNTUX_APP_NOTIFICATION_CHANNEL_ID = "ubuntux_notification_channel";
    /** Ubuntux app notification channel name used by {@link UBUNTUX_APP.UBUNTUX_SERVICE} */
    public static final String UBUNTUX_APP_NOTIFICATION_CHANNEL_NAME = UbuntuxConstants.UBUNTUX_APP_NAME + " App";
    /** Ubuntux app unique notification id used by {@link UBUNTUX_APP.UBUNTUX_SERVICE} */
    public static final int UBUNTUX_APP_NOTIFICATION_ID = 1337;

    /** Ubuntux app notification channel id used by {@link UBUNTUX_APP.RUN_COMMAND_SERVICE} */
    public static final String UBUNTUX_RUN_COMMAND_NOTIFICATION_CHANNEL_ID = "ubuntux_run_command_notification_channel";
    /** Ubuntux app notification channel name used by {@link UBUNTUX_APP.RUN_COMMAND_SERVICE} */
    public static final String UBUNTUX_RUN_COMMAND_NOTIFICATION_CHANNEL_NAME = UbuntuxConstants.UBUNTUX_APP_NAME + " RunCommandService";
    /** Ubuntux app unique notification id used by {@link UBUNTUX_APP.RUN_COMMAND_SERVICE} */
    public static final int UBUNTUX_RUN_COMMAND_NOTIFICATION_ID = 1338;

    /** Ubuntux app notification channel id used for plugin command errors */
    public static final String UBUNTUX_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID = "ubuntux_plugin_command_errors_notification_channel";
    /** Ubuntux app notification channel name used for plugin command errors */
    public static final String UBUNTUX_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME = UbuntuxConstants.UBUNTUX_APP_NAME + " Plugin Commands Errors";

    /** Ubuntux app notification channel id used for crash reports */
    public static final String UBUNTUX_CRASH_REPORTS_NOTIFICATION_CHANNEL_ID = "ubuntux_crash_reports_notification_channel";
    /** Ubuntux app notification channel name used for crash reports */
    public static final String UBUNTUX_CRASH_REPORTS_NOTIFICATION_CHANNEL_NAME = UbuntuxConstants.UBUNTUX_APP_NAME + " Crash Reports";


    /** Ubuntux app notification channel id used by {@link UBUNTUX_FLOAT_APP.UBUNTUX_FLOAT_SERVICE} */
    public static final String UBUNTUX_FLOAT_APP_NOTIFICATION_CHANNEL_ID = "ubuntux_float_notification_channel";
    /** Ubuntux app notification channel name used by {@link UBUNTUX_FLOAT_APP.UBUNTUX_FLOAT_SERVICE} */
    public static final String UBUNTUX_FLOAT_APP_NOTIFICATION_CHANNEL_NAME = UbuntuxConstants.UBUNTUX_FLOAT_APP_NAME + " App";
    /** Ubuntux app unique notification id used by {@link UBUNTUX_APP.UBUNTUX_SERVICE} */
    public static final int UBUNTUX_FLOAT_APP_NOTIFICATION_ID = 1339;





    /*
     * Ubuntux app and plugins miscellaneous variables.
     */

    /** Android OS permission declared by Ubuntux app in AndroidManifest.xml which can be requested by
     * 3rd party apps to run various commands in Ubuntux app context */
    public static final String PERMISSION_RUN_COMMAND = UBUNTUX_PACKAGE_NAME + ".permission.RUN_COMMAND"; // Default: "com.ubuntux.permission.RUN_COMMAND"

    /** Ubuntux property defined in ubuntux.properties file as a secondary check to PERMISSION_RUN_COMMAND
     * to allow 3rd party apps to run various commands in Ubuntux app context */
    public static final String PROP_ALLOW_EXTERNAL_APPS = "allow-external-apps"; // Default: "allow-external-apps"
    /** Default value for {@link #PROP_ALLOW_EXTERNAL_APPS} */
    public static final String PROP_DEFAULT_VALUE_ALLOW_EXTERNAL_APPS = "false"; // Default: "false"

    /** The broadcast action sent when Ubuntux App opens */
    public static final String BROADCAST_TERMUX_OPENED = UBUNTUX_PACKAGE_NAME + ".app.OPENED";

    /** The Uri authority for Ubuntux app file shares */
    public static final String UBUNTUX_FILE_SHARE_URI_AUTHORITY = UBUNTUX_PACKAGE_NAME + ".files"; // Default: "com.ubuntux.files"

    /** The normal comma character (U+002C, &comma;, &#44;, comma) */
    public static final String COMMA_NORMAL = ","; // Default: ","

    /** The alternate comma character (U+201A, &sbquo;, &#8218;, single low-9 quotation mark) that
     * may be used instead of {@link #COMMA_NORMAL} */
    public static final String COMMA_ALTERNATIVE = "‚"; // Default: "‚"

    /** Environment variable prefix root for the Ubuntux app. */
    public static final String UBUNTUX_ENV_PREFIX_ROOT = "UBUNTUX";






    /**
     * Ubuntux app constants.
     */
    public static final class UBUNTUX_APP {

        /** Ubuntux apps directory path */
        public static final String APPS_DIR_PATH = UBUNTUX_APPS_DIR_PATH + "/" + UBUNTUX_PACKAGE_NAME; // Default: "/data/data/com.ubuntux/files/apps/com.ubuntux"

        /** ubuntux-am socket file path */
        public static final String UBUNTUX_AM_SOCKET_FILE_PATH = APPS_DIR_PATH + "/ubuntux-am/am.sock"; // Default: "/data/data/com.ubuntux/files/apps/com.ubuntux/ubuntux-am/am.sock"


        /** Ubuntux app BuildConfig class name */
        public static final String BUILD_CONFIG_CLASS_NAME = UBUNTUX_PACKAGE_NAME + ".BuildConfig"; // Default: "com.ubuntux.BuildConfig"

        /** Ubuntux app FileShareReceiverActivity class name */
        public static final String FILE_SHARE_RECEIVER_ACTIVITY_CLASS_NAME = UBUNTUX_PACKAGE_NAME + ".app.api.file.FileShareReceiverActivity"; // Default: "com.ubuntux.app.api.file.FileShareReceiverActivity"

        /** Ubuntux app FileViewReceiverActivity class name */
        public static final String FILE_VIEW_RECEIVER_ACTIVITY_CLASS_NAME = UBUNTUX_PACKAGE_NAME + ".app.api.file.FileViewReceiverActivity"; // Default: "com.ubuntux.app.api.file.FileViewReceiverActivity"


        /** Ubuntux app core activity name. */
        public static final String UBUNTUX_ACTIVITY_NAME = UBUNTUX_PACKAGE_NAME + ".app.UbuntuxActivity"; // Default: "com.ubuntux.app.UbuntuxActivity"

        /**
         * Ubuntux app core activity.
         */
        public static final class UBUNTUX_ACTIVITY {

            /** Intent extra for if termux failsafe session needs to be started and is used by {@link UBUNTUX_ACTIVITY} and {@link UBUNTUX_SERVICE#ACTION_STOP_SERVICE} */
            public static final String EXTRA_FAILSAFE_SESSION = UbuntuxConstants.UBUNTUX_PACKAGE_NAME + ".app.failsafe_session"; // Default: "com.ubuntux.app.failsafe_session"


            /** Intent action to make termux app notify user that a crash happened. */
            public static final String ACTION_NOTIFY_APP_CRASH = UbuntuxConstants.UBUNTUX_PACKAGE_NAME + ".app.notify_app_crash"; // Default: "com.ubuntux.app.notify_app_crash"


            /** Intent action to make termux reload its termux session styling */
            public static final String ACTION_RELOAD_STYLE = UbuntuxConstants.UBUNTUX_PACKAGE_NAME + ".app.reload_style"; // Default: "com.ubuntux.app.reload_style"
            /** Intent {@code String} extra for what to reload for the UBUNTUX_ACTIVITY.ACTION_RELOAD_STYLE intent. This has been deperecated. */
            @Deprecated
            public static final String EXTRA_RELOAD_STYLE = UbuntuxConstants.UBUNTUX_PACKAGE_NAME + ".app.reload_style"; // Default: "com.ubuntux.app.reload_style"

            /**  Intent {@code boolean} extra for whether to recreate activity for the UBUNTUX_ACTIVITY.ACTION_RELOAD_STYLE intent. */
            public static final String EXTRA_RECREATE_ACTIVITY = UBUNTUX_APP.UBUNTUX_ACTIVITY_NAME + ".EXTRA_RECREATE_ACTIVITY"; // Default: "com.ubuntux.app.UbuntuxActivity.EXTRA_RECREATE_ACTIVITY"


            /** Intent action to make termux request storage permissions */
            public static final String ACTION_REQUEST_PERMISSIONS = UbuntuxConstants.UBUNTUX_PACKAGE_NAME + ".app.request_storage_permissions"; // Default: "com.ubuntux.app.request_storage_permissions"
        }





        /** Ubuntux app settings activity name. */
        public static final String UBUNTUX_SETTINGS_ACTIVITY_NAME = UBUNTUX_PACKAGE_NAME + ".app.activities.SettingsActivity"; // Default: "com.ubuntux.app.activities.SettingsActivity"





        /** Ubuntux app core service name. */
        public static final String UBUNTUX_SERVICE_NAME = UBUNTUX_PACKAGE_NAME + ".app.UbuntuxService"; // Default: "com.ubuntux.app.UbuntuxService"

        /**
         * Ubuntux app core service.
         */
        public static final class UBUNTUX_SERVICE {

            /** Intent action to stop UBUNTUX_SERVICE */
            public static final String ACTION_STOP_SERVICE = UBUNTUX_PACKAGE_NAME + ".service_stop"; // Default: "com.ubuntux.service_stop"


            /** Intent action to make UBUNTUX_SERVICE acquire a wakelock */
            public static final String ACTION_WAKE_LOCK = UBUNTUX_PACKAGE_NAME + ".service_wake_lock"; // Default: "com.ubuntux.service_wake_lock"


            /** Intent action to make UBUNTUX_SERVICE release wakelock */
            public static final String ACTION_WAKE_UNLOCK = UBUNTUX_PACKAGE_NAME + ".service_wake_unlock"; // Default: "com.ubuntux.service_wake_unlock"


            /** Intent action to execute command with UBUNTUX_SERVICE */
            public static final String ACTION_SERVICE_EXECUTE = UBUNTUX_PACKAGE_NAME + ".service_execute"; // Default: "com.ubuntux.service_execute"

            /** Uri scheme for paths sent via intent to UBUNTUX_SERVICE */
            public static final String URI_SCHEME_SERVICE_EXECUTE = UBUNTUX_PACKAGE_NAME + ".file"; // Default: "com.ubuntux.file"
            /** Intent {@code String[]} extra for arguments to the executable of the command for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_ARGUMENTS = UBUNTUX_PACKAGE_NAME + ".execute.arguments"; // Default: "com.ubuntux.execute.arguments"
            /** Intent {@code String} extra for stdin of the command for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_STDIN = UBUNTUX_PACKAGE_NAME + ".execute.stdin"; // Default: "com.ubuntux.execute.stdin"
            /** Intent {@code String} extra for command current working directory for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_WORKDIR = UBUNTUX_PACKAGE_NAME + ".execute.cwd"; // Default: "com.ubuntux.execute.cwd"
            /** Intent {@code boolean} extra for whether to run command in background {@link Runner#APP_SHELL} or foreground {@link Runner#TERMINAL_SESSION} for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            @Deprecated
            public static final String EXTRA_BACKGROUND = UBUNTUX_PACKAGE_NAME + ".execute.background"; // Default: "com.ubuntux.execute.background"
            /** Intent {@code String} extra for command the {@link Runner} for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RUNNER = UBUNTUX_PACKAGE_NAME + ".execute.runner"; // Default: "com.ubuntux.execute.runner"
            /** Intent {@code String} extra for custom log level for background commands defined by {@link com.ubuntux.shared.logger.Logger} for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL = UBUNTUX_PACKAGE_NAME + ".execute.background_custom_log_level"; // Default: "com.ubuntux.execute.background_custom_log_level"
            /** Intent {@code String} extra for session action for {@link Runner#TERMINAL_SESSION} commands for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_SESSION_ACTION = UBUNTUX_PACKAGE_NAME + ".execute.session_action"; // Default: "com.ubuntux.execute.session_action"
            /** Intent {@code String} extra for shell name for commands for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_SHELL_NAME = UBUNTUX_PACKAGE_NAME + ".execute.shell_name"; // Default: "com.ubuntux.execute.shell_name"
            /** Intent {@code String} extra for the {@link ExecutionCommand.ShellCreateMode}  for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent. */
            public static final String EXTRA_SHELL_CREATE_MODE = UBUNTUX_PACKAGE_NAME + ".execute.shell_create_mode"; // Default: "com.ubuntux.execute.shell_create_mode"
            /** Intent {@code String} extra for label of the command for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_COMMAND_LABEL = UBUNTUX_PACKAGE_NAME + ".execute.command_label"; // Default: "com.ubuntux.execute.command_label"
            /** Intent markdown {@code String} extra for description of the command for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_COMMAND_DESCRIPTION = UBUNTUX_PACKAGE_NAME + ".execute.command_description"; // Default: "com.ubuntux.execute.command_description"
            /** Intent markdown {@code String} extra for help of the command for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_COMMAND_HELP = UBUNTUX_PACKAGE_NAME + ".execute.command_help"; // Default: "com.ubuntux.execute.command_help"
            /** Intent markdown {@code String} extra for help of the plugin API for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent (Internal Use Only) */
            public static final String EXTRA_PLUGIN_API_HELP = UBUNTUX_PACKAGE_NAME + ".execute.plugin_api_help"; // Default: "com.ubuntux.execute.plugin_help"
            /** Intent {@code Parcelable} extra for the pending intent that should be sent with the
             * result of the execution command to the execute command caller for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_PENDING_INTENT = "pendingIntent"; // Default: "pendingIntent"
            /** Intent {@code String} extra for the directory path in which to write the result of the
             * execution command for the execute command caller for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_DIRECTORY = UBUNTUX_PACKAGE_NAME + ".execute.result_directory"; // Default: "com.ubuntux.execute.result_directory"
            /** Intent {@code boolean} extra for whether the result should be written to a single file
             * or multiple files (err, errmsg, stdout, stderr, exit_code) in
             * {@link #EXTRA_RESULT_DIRECTORY} for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_SINGLE_FILE = UBUNTUX_PACKAGE_NAME + ".execute.result_single_file"; // Default: "com.ubuntux.execute.result_single_file"
            /** Intent {@code String} extra for the basename of the result file that should be created
             * in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is {@code true}
             * for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILE_BASENAME = UBUNTUX_PACKAGE_NAME + ".execute.result_file_basename"; // Default: "com.ubuntux.execute.result_file_basename"
            /** Intent {@code String} extra for the output {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILE_OUTPUT_FORMAT = UBUNTUX_PACKAGE_NAME + ".execute.result_file_output_format"; // Default: "com.ubuntux.execute.result_file_output_format"
            /** Intent {@code String} extra for the error {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILE_ERROR_FORMAT = UBUNTUX_PACKAGE_NAME + ".execute.result_file_error_format"; // Default: "com.ubuntux.execute.result_file_error_format"
            /** Intent {@code String} extra for the optional suffix of the result files that should
             * be created in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is
             * {@code false} for the UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILES_SUFFIX = UBUNTUX_PACKAGE_NAME + ".execute.result_files_suffix"; // Default: "com.ubuntux.execute.result_files_suffix"



            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will set the new session as
             * the current session and will start {@link UBUNTUX_ACTIVITY} if its not running to bring
             * the new session to foreground.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY = 0;

            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will keep any existing session
             * as the current session and will start {@link UBUNTUX_ACTIVITY} if its not running to
             * bring the existing session to foreground. The new session will be added to the left
             * sidebar in the sessions list.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY = 1;

            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will set the new session as
             * the current session but will not start {@link UBUNTUX_ACTIVITY} if its not running
             * and session(s) will be seen in Termux notification and can be clicked to bring new
             * session to foreground. If the {@link UBUNTUX_ACTIVITY} is already running, then this
             * will behave like {@link #VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY}.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_DONT_OPEN_ACTIVITY = 2;

            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will keep any existing session
             * as the current session but will not start {@link UBUNTUX_ACTIVITY} if its not running
             * and session(s) will be seen in Termux notification and can be clicked to bring
             * existing session to foreground. If the {@link UBUNTUX_ACTIVITY} is already running,
             * then this will behave like {@link #VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY}.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY = 3;

            /** The minimum allowed value for {@link #EXTRA_SESSION_ACTION}. */
            public static final int MIN_VALUE_EXTRA_SESSION_ACTION = VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY;

            /** The maximum allowed value for {@link #EXTRA_SESSION_ACTION}. */
            public static final int MAX_VALUE_EXTRA_SESSION_ACTION = VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY;


            /** Intent {@code Bundle} extra to store result of execute command that is sent back for the
             * UBUNTUX_SERVICE.ACTION_SERVICE_EXECUTE intent if the {@link #EXTRA_PENDING_INTENT} is not
             * {@code null} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE = "result"; // Default: "result"
            /** Intent {@code String} extra for stdout value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT = "stdout"; // Default: "stdout"
            /** Intent {@code String} extra for original length of stdout value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH = "stdout_original_length"; // Default: "stdout_original_length"
            /** Intent {@code String} extra for stderr value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDERR = "stderr"; // Default: "stderr"
            /** Intent {@code String} extra for original length of stderr value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH = "stderr_original_length"; // Default: "stderr_original_length"
            /** Intent {@code int} extra for exit code value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_EXIT_CODE = "exitCode"; // Default: "exitCode"
            /** Intent {@code int} extra for err value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_ERR = "err"; // Default: "err"
            /** Intent {@code String} extra for errmsg value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_ERRMSG = "errmsg"; // Default: "errmsg"

        }





        /** Ubuntux app run command service name. */
        public static final String RUN_COMMAND_SERVICE_NAME = UBUNTUX_PACKAGE_NAME + ".app.RunCommandService"; // Termux app service to receive commands from 3rd party apps "com.ubuntux.app.RunCommandService"

        /**
         * Ubuntux app run command service to receive commands sent by 3rd party apps.
         */
        public static final class RUN_COMMAND_SERVICE {

            /** Ubuntux RUN_COMMAND Intent help url */
            public static final String RUN_COMMAND_API_HELP_URL = UBUNTUX_GITHUB_WIKI_REPO_URL + "/RUN_COMMAND-Intent"; // Default: "https://github.com/termux/termux-app/wiki/RUN_COMMAND-Intent"


            /** Intent action to execute command with RUN_COMMAND_SERVICE */
            public static final String ACTION_RUN_COMMAND = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND"; // Default: "com.ubuntux.RUN_COMMAND"

            /** Intent {@code String} extra for absolute path of command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_PATH = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_PATH"; // Default: "com.ubuntux.RUN_COMMAND_PATH"
            /** Intent {@code String[]} extra for arguments to the executable of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_ARGUMENTS = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_ARGUMENTS"; // Default: "com.ubuntux.RUN_COMMAND_ARGUMENTS"
            /** Intent {@code boolean} extra for whether to replace comma alternative characters in arguments with comma characters for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"; // Default: "com.ubuntux.RUN_COMMAND_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"
            /** Intent {@code String} extra for the comma alternative characters in arguments that should be replaced instead of the default {@link #COMMA_ALTERNATIVE} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"; // Default: "com.ubuntux.RUN_COMMAND_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"

            /** Intent {@code String} extra for stdin of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_STDIN = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_STDIN"; // Default: "com.ubuntux.RUN_COMMAND_STDIN"
            /** Intent {@code String} extra for current working directory of command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_WORKDIR = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_WORKDIR"; // Default: "com.ubuntux.RUN_COMMAND_WORKDIR"
            /** Intent {@code boolean} extra for whether to run command in background {@link Runner#APP_SHELL} or foreground {@link Runner#TERMINAL_SESSION} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            @Deprecated
            public static final String EXTRA_BACKGROUND = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_BACKGROUND"; // Default: "com.ubuntux.RUN_COMMAND_BACKGROUND"
            /** Intent {@code String} extra for command the {@link Runner} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RUNNER = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_RUNNER"; // Default: "com.ubuntux.RUN_COMMAND_RUNNER"
            /** Intent {@code String} extra for custom log level for background commands defined by {@link com.ubuntux.shared.logger.Logger} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_BACKGROUND_CUSTOM_LOG_LEVEL"; // Default: "com.ubuntux.RUN_COMMAND_BACKGROUND_CUSTOM_LOG_LEVEL"
            /** Intent {@code String} extra for session action of {@link Runner#TERMINAL_SESSION} commands for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_SESSION_ACTION = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_SESSION_ACTION"; // Default: "com.ubuntux.RUN_COMMAND_SESSION_ACTION"
            /** Intent {@code String} extra for shell name of commands for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_SHELL_NAME = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_SHELL_NAME"; // Default: "com.ubuntux.RUN_COMMAND_SHELL_NAME"
            /** Intent {@code String} extra for the {@link ExecutionCommand.ShellCreateMode}  for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent. */
            public static final String EXTRA_SHELL_CREATE_MODE = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_SHELL_CREATE_MODE"; // Default: "com.ubuntux.RUN_COMMAND_SHELL_CREATE_MODE"
            /** Intent {@code String} extra for label of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_LABEL = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_COMMAND_LABEL"; // Default: "com.ubuntux.RUN_COMMAND_COMMAND_LABEL"
            /** Intent markdown {@code String} extra for description of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_DESCRIPTION = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_COMMAND_DESCRIPTION"; // Default: "com.ubuntux.RUN_COMMAND_COMMAND_DESCRIPTION"
            /** Intent markdown {@code String} extra for help of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_HELP = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_COMMAND_HELP"; // Default: "com.ubuntux.RUN_COMMAND_COMMAND_HELP"
            /** Intent {@code Parcelable} extra for the pending intent that should be sent with the result of the execution command to the execute command caller for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_PENDING_INTENT = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_PENDING_INTENT"; // Default: "com.ubuntux.RUN_COMMAND_PENDING_INTENT"
            /** Intent {@code String} extra for the directory path in which to write the result of
             * the execution command for the execute command caller for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_DIRECTORY = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_RESULT_DIRECTORY"; // Default: "com.ubuntux.RUN_COMMAND_RESULT_DIRECTORY"
            /** Intent {@code boolean} extra for whether the result should be written to a single file
             * or multiple files (err, errmsg, stdout, stderr, exit_code) in
             * {@link #EXTRA_RESULT_DIRECTORY} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_SINGLE_FILE = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_RESULT_SINGLE_FILE"; // Default: "com.ubuntux.RUN_COMMAND_RESULT_SINGLE_FILE"
            /** Intent {@code String} extra for the basename of the result file that should be created
             * in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is {@code true}
             * for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILE_BASENAME = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILE_BASENAME"; // Default: "com.ubuntux.RUN_COMMAND_RESULT_FILE_BASENAME"
            /** Intent {@code String} extra for the output {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILE_OUTPUT_FORMAT = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILE_OUTPUT_FORMAT"; // Default: "com.ubuntux.RUN_COMMAND_RESULT_FILE_OUTPUT_FORMAT"
            /** Intent {@code String} extra for the error {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILE_ERROR_FORMAT = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILE_ERROR_FORMAT"; // Default: "com.ubuntux.RUN_COMMAND_RESULT_FILE_ERROR_FORMAT"
            /** Intent {@code String} extra for the optional suffix of the result files that should be
             * created in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is
             * {@code false} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILES_SUFFIX = UBUNTUX_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILES_SUFFIX"; // Default: "com.ubuntux.RUN_COMMAND_RESULT_FILES_SUFFIX"

        }
    }


    /**
     * Ubuntux:API app constants.
     */
    public static final class UBUNTUX_API {

        /** Ubuntux:API app core activity name. */
        public static final String UBUNTUX_API_ACTIVITY_NAME = UBUNTUX_API_PACKAGE_NAME + ".activities.TermuxAPIActivity"; // Default: "com.ubuntux.tasker.activities.TermuxAPIActivity"

    }





    /**
     * Ubuntux:Float app constants.
     */
    public static final class UBUNTUX_FLOAT_APP {

        /** Ubuntux:Float app core service name. */
        public static final String UBUNTUX_FLOAT_SERVICE_NAME = UBUNTUX_FLOAT_PACKAGE_NAME + ".TermuxFloatService"; // Default: "com.ubuntux.window.TermuxFloatService"

        /**
         * Ubuntux:Float app core service.
         */
        public static final class UBUNTUX_FLOAT_SERVICE {

            /** Intent action to stop UBUNTUX_FLOAT_SERVICE. */
            public static final String ACTION_STOP_SERVICE = UBUNTUX_FLOAT_PACKAGE_NAME + ".ACTION_STOP_SERVICE"; // Default: "com.ubuntux.float.ACTION_STOP_SERVICE"

            /** Intent action to show float window. */
            public static final String ACTION_SHOW = UBUNTUX_FLOAT_PACKAGE_NAME + ".ACTION_SHOW"; // Default: "com.ubuntux.float.ACTION_SHOW"

            /** Intent action to hide float window. */
            public static final String ACTION_HIDE = UBUNTUX_FLOAT_PACKAGE_NAME + ".ACTION_HIDE"; // Default: "com.ubuntux.float.ACTION_HIDE"

        }

    }





    /**
     * Ubuntux:Styling app constants.
     */
    public static final class UBUNTUX_STYLING {

        /** Ubuntux:Styling app core activity name. */
        public static final String UBUNTUX_STYLING_ACTIVITY_NAME = UBUNTUX_STYLING_PACKAGE_NAME + ".TermuxStyleActivity"; // Default: "com.ubuntux.styling.TermuxStyleActivity"

    }





    /**
     * Ubuntux:Tasker app constants.
     */
    public static final class UBUNTUX_TASKER {

        /** Ubuntux:Tasker app core activity name. */
        public static final String UBUNTUX_TASKER_ACTIVITY_NAME = UBUNTUX_TASKER_PACKAGE_NAME + ".activities.TermuxTaskerActivity"; // Default: "com.ubuntux.tasker.activities.TermuxTaskerActivity"

    }





    /**
     * Ubuntux:Widget app constants.
     */
    public static final class UBUNTUX_WIDGET {

        /** Ubuntux:Widget app core activity name. */
        public static final String UBUNTUX_WIDGET_ACTIVITY_NAME = UBUNTUX_WIDGET_PACKAGE_NAME + ".activities.TermuxWidgetActivity"; // Default: "com.ubuntux.widget.activities.TermuxWidgetActivity"


        /**  Intent {@code String} extra for the token of the Termux:Widget app shortcuts. */
        public static final String EXTRA_TOKEN_NAME = UBUNTUX_PACKAGE_NAME + ".shortcut.token"; // Default: "com.ubuntux.shortcut.token"

        /**
         * Ubuntux:Widget app {@link android.appwidget.AppWidgetProvider} class.
         */
        public static final class UBUNTUX_WIDGET_PROVIDER {

            /** Intent action for if an item is clicked in the widget. */
            public static final String ACTION_WIDGET_ITEM_CLICKED = UBUNTUX_WIDGET_PACKAGE_NAME + ".ACTION_WIDGET_ITEM_CLICKED"; // Default: "com.ubuntux.widget.ACTION_WIDGET_ITEM_CLICKED"


            /** Intent action to refresh files in the widget. */
            public static final String ACTION_REFRESH_WIDGET = UBUNTUX_WIDGET_PACKAGE_NAME + ".ACTION_REFRESH_WIDGET"; // Default: "com.ubuntux.widget.ACTION_REFRESH_WIDGET"


            /**  Intent {@code String} extra for the file clicked for the UBUNTUX_WIDGET_PROVIDER.ACTION_WIDGET_ITEM_CLICKED intent. */
            public static final String EXTRA_FILE_CLICKED = UBUNTUX_WIDGET_PACKAGE_NAME + ".EXTRA_FILE_CLICKED"; // Default: "com.ubuntux.widget.EXTRA_FILE_CLICKED"

        }

    }

}
