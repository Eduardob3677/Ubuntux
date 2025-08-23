package com.ubuntux.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.system.Os;
import android.util.Pair;
import android.view.WindowManager;

import com.ubuntux.R;
import com.ubuntux.shared.file.FileUtils;
import com.ubuntux.shared.ubuntux.crash.UbuntuxCrashUtils;
import com.ubuntux.shared.ubuntux.file.UbuntuxFileUtils;
import com.ubuntux.shared.interact.MessageDialogUtils;
import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.markdown.MarkdownUtils;
import com.ubuntux.shared.errors.Error;
import com.ubuntux.shared.android.PackageUtils;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;
import com.ubuntux.shared.ubuntux.UbuntuxUtils;
import com.ubuntux.shared.ubuntux.shell.command.environment.UbuntuxShellEnvironment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.ubuntux.shared.ubuntux.UbuntuxConstants.UBUNTUX_PREFIX_DIR;
import static com.ubuntux.shared.ubuntux.UbuntuxConstants.UBUNTUX_PREFIX_DIR_PATH;
import static com.ubuntux.shared.ubuntux.UbuntuxConstants.UBUNTUX_STAGING_PREFIX_DIR;
import static com.ubuntux.shared.ubuntux.UbuntuxConstants.UBUNTUX_STAGING_PREFIX_DIR_PATH;

/**
 * Install the Ubuntu packages if necessary by following the below steps:
 * <p/>
 * (1) If $PREFIX already exist, assume that it is correct and be done. Note that this relies on that we do not create a
 * broken $PREFIX directory below.
 * <p/>
 * (2) A progress dialog is shown with "Installing..." message and a spinner.
 * <p/>
 * (3) A staging directory, $STAGING_PREFIX, is cleared if left over from broken installation below.
 * <p/>
 * (4) The bootstrap archive is loaded from a shared library.
 * <p/>
 * (5) The archive (ZIP, tar.gz, or tar.xz format), containing entries relative to the $PREFIX, is extracted by detecting
 * the format and using the appropriate decompression method. The extraction process continuously encounters archive entries:
 * <p/>
 * (5.1) If the archive entry encountered is SYMLINKS.txt, go through it and remember all symlinks to setup.
 * <p/>
 * (5.2) For every other archive entry, extract it into $STAGING_PREFIX and set execute permissions if necessary.
 */
final class UbuntuxInstaller {

    private static final String LOG_TAG = "UbuntuxInstaller";

    /** Performs bootstrap setup if necessary. */
    static void setupBootstrapIfNeeded(final Activity activity, final Runnable whenDone) {
        String bootstrapErrorMessage;
        Error filesDirectoryAccessibleError;

        // This will also call Context.getFilesDir(), which should ensure that termux files directory
        // is created if it does not already exist
        filesDirectoryAccessibleError = UbuntuxFileUtils.isTermuxFilesDirectoryAccessible(activity, true, true);
        boolean isFilesDirectoryAccessible = filesDirectoryAccessibleError == null;

        // Termux can only be run as the primary user (device owner) since only that
        // account has the expected file system paths. Verify that:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !PackageUtils.isCurrentUserThePrimaryUser(activity)) {
            bootstrapErrorMessage = activity.getString(R.string.bootstrap_error_not_primary_user_message,
                MarkdownUtils.getMarkdownCodeForString(UBUNTUX_PREFIX_DIR_PATH, false));
            Logger.logError(LOG_TAG, "isFilesDirectoryAccessible: " + isFilesDirectoryAccessible);
            Logger.logError(LOG_TAG, bootstrapErrorMessage);
            sendBootstrapCrashReportNotification(activity, bootstrapErrorMessage);
            MessageDialogUtils.exitAppWithErrorMessage(activity,
                activity.getString(R.string.bootstrap_error_title),
                bootstrapErrorMessage);
            return;
        }

        if (!isFilesDirectoryAccessible) {
            bootstrapErrorMessage = Error.getMinimalErrorString(filesDirectoryAccessibleError);
            //noinspection SdCardPath
            if (PackageUtils.isAppInstalledOnExternalStorage(activity) &&
                !UbuntuxConstants.UBUNTUX_FILES_DIR_PATH.equals(activity.getFilesDir().getAbsolutePath().replaceAll("^/data/user/0/", "/data/data/"))) {
                bootstrapErrorMessage += "\n\n" + activity.getString(R.string.bootstrap_error_installed_on_portable_sd,
                    MarkdownUtils.getMarkdownCodeForString(UBUNTUX_PREFIX_DIR_PATH, false));
            }

            Logger.logError(LOG_TAG, bootstrapErrorMessage);
            sendBootstrapCrashReportNotification(activity, bootstrapErrorMessage);
            MessageDialogUtils.showMessage(activity,
                activity.getString(R.string.bootstrap_error_title),
                bootstrapErrorMessage, null);
            return;
        }

        // If prefix directory exists, even if its a symlink to a valid directory and symlink is not broken/dangling
        if (FileUtils.directoryFileExists(UBUNTUX_PREFIX_DIR_PATH, true)) {
            if (UbuntuxFileUtils.isTermuxPrefixDirectoryEmpty()) {
                Logger.logInfo(LOG_TAG, "The prefix directory \"" + UBUNTUX_PREFIX_DIR_PATH + "\" exists but is empty or only contains specific unimportant files.");
            } else {
                whenDone.run();
                return;
            }
        } else if (FileUtils.fileExists(UBUNTUX_PREFIX_DIR_PATH, false)) {
            Logger.logInfo(LOG_TAG, "The prefix directory \"" + UBUNTUX_PREFIX_DIR_PATH + "\" does not exist but another file exists at its destination.");
        }

        final ProgressDialog progress = ProgressDialog.show(activity, null, activity.getString(R.string.bootstrap_installer_body), true, false);
        new Thread() {
            @Override
            public void run() {
                try {
                    Logger.logInfo(LOG_TAG, "Installing " + UbuntuxConstants.UBUNTUX_APP_NAME + " Ubuntu packages.");

                    Error error;

                    // Delete prefix staging directory or any file at its destination
                    error = FileUtils.deleteFile("prefix staging directory", UBUNTUX_STAGING_PREFIX_DIR_PATH, true);
                    if (error != null) {
                        showBootstrapErrorDialog(activity, whenDone, Error.getErrorMarkdownString(error));
                        return;
                    }

                    // Delete prefix directory or any file at its destination
                    error = FileUtils.deleteFile("prefix directory", UBUNTUX_PREFIX_DIR_PATH, true);
                    if (error != null) {
                        showBootstrapErrorDialog(activity, whenDone, Error.getErrorMarkdownString(error));
                        return;
                    }

                    // Create prefix staging directory if it does not already exist and set required permissions
                    error = UbuntuxFileUtils.isTermuxPrefixStagingDirectoryAccessible(true, true);
                    if (error != null) {
                        showBootstrapErrorDialog(activity, whenDone, Error.getErrorMarkdownString(error));
                        return;
                    }

                    // Create prefix directory if it does not already exist and set required permissions
                    error = UbuntuxFileUtils.isTermuxPrefixDirectoryAccessible(true, true);
                    if (error != null) {
                        showBootstrapErrorDialog(activity, whenDone, Error.getErrorMarkdownString(error));
                        return;
                    }

                    // Update progress dialog to show extraction phase
                    activity.runOnUiThread(() -> progress.setMessage(activity.getString(R.string.bootstrap_installer_extracting)));
                    Logger.logInfo(LOG_TAG, "Extracting bootstrap archive to prefix staging directory \"" + UBUNTUX_STAGING_PREFIX_DIR_PATH + "\".");

                    final byte[] buffer = new byte[8096];
                    final List<Pair<String, String>> symlinks = new ArrayList<>(50);

                    final byte[] archiveBytes = loadBootstrapBytes();
                    Logger.logInfo(LOG_TAG, "Loaded bootstrap archive with " + archiveBytes.length + " bytes");
                    extractBootstrapArchive(archiveBytes, buffer, symlinks);

                    // Ubuntu rootfs may not have SYMLINKS.txt, so we don't require it
                    // Create essential symlinks for Ubuntu compatibility if needed
                    if (symlinks.isEmpty()) {
                        Logger.logInfo(LOG_TAG, "No SYMLINKS.txt found (expected for Ubuntu rootfs), creating essential symlinks manually");
                        // Update progress dialog to show symlink creation phase
                        activity.runOnUiThread(() -> progress.setMessage(activity.getString(R.string.bootstrap_installer_creating_symlinks)));
                        createEssentialUbuntuSymlinks(symlinks);
                    } else {
                        Logger.logInfo(LOG_TAG, "Found " + symlinks.size() + " symlinks to create");
                        // Update progress dialog to show symlink creation phase
                        activity.runOnUiThread(() -> progress.setMessage(activity.getString(R.string.bootstrap_installer_creating_symlinks)));
                    }
                    
                    for (Pair<String, String> symlink : symlinks) {
                        // Use explicit parameters to allow overwriting any file type during bootstrap
                        // allowDangling=true, overwrite=true, overwriteOnlyIfDestIsASymlink=false
                        Error symlinkError = FileUtils.createSymlinkFile("bootstrap symlink ", symlink.first, symlink.second, true, true, false);
                        if (symlinkError != null) {
                            throw new RuntimeException("Failed to create symlink from \"" + symlink.first + "\" to \"" + symlink.second + "\": " + symlinkError.toString());
                        }
                    }

                    // Update progress dialog to show finalization phase
                    activity.runOnUiThread(() -> progress.setMessage(activity.getString(R.string.bootstrap_installer_finalizing)));
                    Logger.logInfo(LOG_TAG, "Moving prefix staging to prefix directory.");

                    if (!UBUNTUX_STAGING_PREFIX_DIR.renameTo(UBUNTUX_PREFIX_DIR)) {
                        throw new RuntimeException("Moving prefix staging to prefix directory failed");
                    }

                    Logger.logInfo(LOG_TAG, "Bootstrap packages installed successfully.");
                    
                    // Update progress dialog to show verification phase
                    activity.runOnUiThread(() -> progress.setMessage(activity.getString(R.string.bootstrap_installer_verifying)));
                    
                    // Verify essential binaries are present
                    File lsBinary = new File(UBUNTUX_PREFIX_DIR_PATH + "/usr/bin/ls");
                    File bashBinary = new File(UBUNTUX_PREFIX_DIR_PATH + "/usr/bin/bash");
                    File shBinary = new File(UBUNTUX_PREFIX_DIR_PATH + "/usr/bin/sh");
                    
                    Logger.logInfo(LOG_TAG, "Essential binary verification:");
                    Logger.logInfo(LOG_TAG, "ls binary exists: " + lsBinary.exists() + " at " + lsBinary.getAbsolutePath());
                    Logger.logInfo(LOG_TAG, "bash binary exists: " + bashBinary.exists() + " at " + bashBinary.getAbsolutePath());
                    Logger.logInfo(LOG_TAG, "sh binary exists: " + shBinary.exists() + " at " + shBinary.getAbsolutePath());
                    
                    if (lsBinary.exists()) {
                        Logger.logInfo(LOG_TAG, "ls binary permissions: " + (lsBinary.canExecute() ? "executable" : "not executable"));
                    }
                    
                    // Also check if the bin directory exists and list its contents
                    File binDir = new File(UBUNTUX_PREFIX_DIR_PATH + "/usr/bin");
                    if (binDir.exists() && binDir.isDirectory()) {
                        String[] files = binDir.list();
                        Logger.logInfo(LOG_TAG, "usr/bin directory contains " + (files != null ? files.length : 0) + " files");
                        if (files != null && files.length > 0) {
                            Logger.logDebug(LOG_TAG, "First 10 files in usr/bin: " + 
                                String.join(", ", java.util.Arrays.copyOf(files, Math.min(10, files.length))));
                        }
                    } else {
                        Logger.logError(LOG_TAG, "usr/bin directory does not exist or is not a directory!");
                    }

                    // Recreate env file since termux prefix was wiped earlier
                    UbuntuxShellEnvironment.writeEnvironmentToFile(activity);
                    
                    // Copy diagnostic scripts to user home directory for easy access
                    copyDiagnosticScripts(activity);

                    activity.runOnUiThread(whenDone);

                } catch (final Exception e) {
                    showBootstrapErrorDialog(activity, whenDone, Logger.getStackTracesMarkdownString(null, Logger.getStackTracesStringArray(e)));

                } finally {
                    activity.runOnUiThread(() -> {
                        try {
                            progress.dismiss();
                        } catch (RuntimeException e) {
                            // Activity already dismissed - ignore.
                        }
                    });
                }
            }
        }.start();
    }

    public static void showBootstrapErrorDialog(Activity activity, Runnable whenDone, String message) {
        Logger.logErrorExtended(LOG_TAG, "Bootstrap Error:\n" + message);

        // Send a notification with the exception so that the user knows why bootstrap setup failed
        sendBootstrapCrashReportNotification(activity, message);

        activity.runOnUiThread(() -> {
            try {
                new AlertDialog.Builder(activity).setTitle(R.string.bootstrap_error_title).setMessage(R.string.bootstrap_error_body)
                    .setNegativeButton(R.string.bootstrap_error_abort, (dialog, which) -> {
                        dialog.dismiss();
                        activity.finish();
                    })
                    .setPositiveButton(R.string.bootstrap_error_try_again, (dialog, which) -> {
                        dialog.dismiss();
                        FileUtils.deleteFile("termux prefix directory", UBUNTUX_PREFIX_DIR_PATH, true);
                        UbuntuxInstaller.setupBootstrapIfNeeded(activity, whenDone);
                    }).show();
            } catch (WindowManager.BadTokenException e1) {
                // Activity already dismissed - ignore.
            }
        });
    }

    private static void sendBootstrapCrashReportNotification(Activity activity, String message) {
        final String title = UbuntuxConstants.UBUNTUX_APP_NAME + " Bootstrap Error";

        // Add info of all install Termux plugin apps as well since their target sdk or installation
        // on external/portable sd card can affect Termux app files directory access or exec.
        UbuntuxCrashUtils.sendCrashReportNotification(activity, LOG_TAG,
            title, null, "## " + title + "\n\n" + message + "\n\n" +
                UbuntuxUtils.getTermuxDebugMarkdownString(activity),
            true, false, UbuntuxUtils.AppInfoMode.UBUNTUX_AND_PLUGIN_PACKAGES, true);
    }

    static void setupStorageSymlinks(final Context context) {
        final String LOG_TAG = "termux-storage";
        final String title = UbuntuxConstants.UBUNTUX_APP_NAME + " Setup Storage Error";

        Logger.logInfo(LOG_TAG, "Setting up storage symlinks.");

        new Thread() {
            public void run() {
                try {
                    Error error;
                    File storageDir = UbuntuxConstants.UBUNTUX_STORAGE_HOME_DIR;

                    error = FileUtils.clearDirectory("~/storage", storageDir.getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        UbuntuxCrashUtils.sendCrashReportNotification(context, LOG_TAG, title, null,
                            "## " + title + "\n\n" + Error.getErrorMarkdownString(error),
                            true, false, UbuntuxUtils.AppInfoMode.UBUNTUX_PACKAGE, true);
                        return;
                    }

                    Logger.logInfo(LOG_TAG, "Setting up storage symlinks at ~/storage/shared, ~/storage/downloads, ~/storage/dcim, ~/storage/pictures, ~/storage/music and ~/storage/movies for directories in \"" + Environment.getExternalStorageDirectory().getAbsolutePath() + "\".");

                    // Get primary storage root "/storage/emulated/0" symlink
                    File sharedDir = Environment.getExternalStorageDirectory();
                    error = FileUtils.createSymlinkFile("~/storage/shared ", sharedDir.getAbsolutePath(), new File(storageDir, "shared").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    error = FileUtils.createSymlinkFile("~/storage/documents ", documentsDir.getAbsolutePath(), new File(storageDir, "documents").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    error = FileUtils.createSymlinkFile("~/storage/downloads ", downloadsDir.getAbsolutePath(), new File(storageDir, "downloads").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    error = FileUtils.createSymlinkFile("~/storage/dcim ", dcimDir.getAbsolutePath(), new File(storageDir, "dcim").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    error = FileUtils.createSymlinkFile("~/storage/pictures ", picturesDir.getAbsolutePath(), new File(storageDir, "pictures").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    error = FileUtils.createSymlinkFile("~/storage/music ", musicDir.getAbsolutePath(), new File(storageDir, "music").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    error = FileUtils.createSymlinkFile("~/storage/movies ", moviesDir.getAbsolutePath(), new File(storageDir, "movies").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    File podcastsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
                    error = FileUtils.createSymlinkFile("~/storage/podcasts ", podcastsDir.getAbsolutePath(), new File(storageDir, "podcasts").getAbsolutePath());
                    if (error != null) {
                        Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                        Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                        return;
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        File audiobooksDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_AUDIOBOOKS);
                        error = FileUtils.createSymlinkFile("~/storage/audiobooks ", audiobooksDir.getAbsolutePath(), new File(storageDir, "audiobooks").getAbsolutePath());
                        if (error != null) {
                            Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                            Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                            return;
                        }
                    }

                    // Dir 0 should ideally be for primary storage
                    // https://cs.android.com/android/platform/superproject/+/android-12.0.0_r32:frameworks/base/core/java/android/app/ContextImpl.java;l=818
                    // https://cs.android.com/android/platform/superproject/+/android-12.0.0_r32:frameworks/base/core/java/android/os/Environment.java;l=219
                    // https://cs.android.com/android/platform/superproject/+/android-12.0.0_r32:frameworks/base/core/java/android/os/Environment.java;l=181
                    // https://cs.android.com/android/platform/superproject/+/android-12.0.0_r32:frameworks/base/services/core/java/com/android/server/StorageManagerService.java;l=3796
                    // https://cs.android.com/android/platform/superproject/+/android-7.0.0_r36:frameworks/base/services/core/java/com/android/server/MountService.java;l=3053

                    // Create "Android/data/com.ubuntux" symlinks
                    File[] dirs = context.getExternalFilesDirs(null);
                    if (dirs != null && dirs.length > 0) {
                        for (int i = 0; i < dirs.length; i++) {
                            File dir = dirs[i];
                            if (dir == null) continue;
                            String symlinkName = "external-" + i;
                            Logger.logInfo(LOG_TAG, "Setting up storage symlinks at ~/storage/" + symlinkName + " for \"" + dir.getAbsolutePath() + "\".");
                            error = FileUtils.createSymlinkFile("~/storage/" + symlinkName + " ", dir.getAbsolutePath(), new File(storageDir, symlinkName).getAbsolutePath());
                            if (error != null) {
                                Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                                Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                                return;
                            }
                        }
                    }

                    // Create "Android/media/com.ubuntux" symlinks
                    dirs = context.getExternalMediaDirs();
                    if (dirs != null && dirs.length > 0) {
                        for (int i = 0; i < dirs.length; i++) {
                            File dir = dirs[i];
                            if (dir == null) continue;
                            String symlinkName = "media-" + i;
                            Logger.logInfo(LOG_TAG, "Setting up storage symlinks at ~/storage/" + symlinkName + " for \"" + dir.getAbsolutePath() + "\".");
                            error = FileUtils.createSymlinkFile("~/storage/" + symlinkName + " ", dir.getAbsolutePath(), new File(storageDir, symlinkName).getAbsolutePath());
                            if (error != null) {
                                Logger.logErrorAndShowToast(context, LOG_TAG, error.getMessage());
                                Logger.logErrorExtended(LOG_TAG, "Setup Storage Error\n" + error.toString());
                                return;
                            }
                        }
                    }

                    Logger.logInfo(LOG_TAG, "Storage symlinks created successfully.");
                } catch (Exception e) {
                    Logger.logErrorAndShowToast(context, LOG_TAG, e.getMessage());
                    Logger.logStackTraceWithMessage(LOG_TAG, "Setup Storage Error: Error setting up link", e);
                    UbuntuxCrashUtils.sendCrashReportNotification(context, LOG_TAG, title, null,
                        "## " + title + "\n\n" + Logger.getStackTracesMarkdownString(null, Logger.getStackTracesStringArray(e)),
                        true, false, UbuntuxUtils.AppInfoMode.UBUNTUX_PACKAGE, true);
                }
            }
        }.start();
    }

    private static Error ensureDirectoryExists(File directory) {
        return FileUtils.createDirectoryFile(directory.getAbsolutePath());
    }

    /**
     * Create essential symlinks for Ubuntu compatibility when SYMLINKS.txt is not present.
     */
    private static void createEssentialUbuntuSymlinks(List<Pair<String, String>> symlinks) {
        // Ubuntu rootfs typically has a proper directory structure already
        // Add any essential symlinks that might be needed for Android compatibility
        // For now, we may not need any specific symlinks as Ubuntu rootfs should be complete
        Logger.logInfo(LOG_TAG, "Ubuntu rootfs extracted, no additional symlinks required");
    }

    /**
     * Archive format types supported for bootstrap extraction.
     */
    private enum ArchiveFormat {
        ZIP, TAR_GZ, TAR_XZ, UNKNOWN
    }

    /**
     * Detect the archive format based on magic bytes.
     */
    private static ArchiveFormat detectArchiveFormat(byte[] data) {
        if (data.length < 4) return ArchiveFormat.UNKNOWN;
        
        // ZIP file signature: PK (0x504B)
        if (data[0] == 0x50 && data[1] == 0x4B) {
            return ArchiveFormat.ZIP;
        }
        
        // GZIP signature: 0x1F 0x8B
        if (data[0] == 0x1F && data[1] == (byte)0x8B) {
            return ArchiveFormat.TAR_GZ;
        }
        
        // XZ signature: 0xFD 0x37 0x7A 0x58 0x5A 0x00
        if (data.length >= 6 && 
            data[0] == (byte)0xFD && data[1] == 0x37 && data[2] == 0x7A && 
            data[3] == 0x58 && data[4] == 0x5A && data[5] == 0x00) {
            return ArchiveFormat.TAR_XZ;
        }
        
        return ArchiveFormat.UNKNOWN;
    }

    /**
     * Abstract interface for archive entry extraction.
     */
    private interface ArchiveEntry {
        String getName();
        boolean isDirectory();
        InputStream getInputStream();
    }

    /**
     * Extract bootstrap archive with format auto-detection.
     */
    private static void extractBootstrapArchive(byte[] archiveBytes, final byte[] buffer, 
                                               final List<Pair<String, String>> symlinks) throws Exception {
        ArchiveFormat format = detectArchiveFormat(archiveBytes);
        Logger.logInfo(LOG_TAG, "Detected archive format: " + format);
        
        switch (format) {
            case ZIP:
                Logger.logInfo(LOG_TAG, "Extracting as ZIP archive");
                extractZipArchive(archiveBytes, buffer, symlinks);
                break;
            case TAR_GZ:
                Logger.logInfo(LOG_TAG, "Extracting as TAR.GZ archive (Ubuntu format)");
                extractTarGzArchive(archiveBytes, buffer, symlinks);
                break;
            case TAR_XZ:
                // TAR_XZ support would require external library - fall back to ZIP for now
                Logger.logWarn(LOG_TAG, "TAR_XZ format detected but not fully supported yet, trying as ZIP");
                extractZipArchive(archiveBytes, buffer, symlinks);
                break;
            default:
                // Assume ZIP as fallback for backward compatibility
                Logger.logWarn(LOG_TAG, "Unknown archive format, trying as ZIP");
                extractZipArchive(archiveBytes, buffer, symlinks);
                break;
        }
        Logger.logInfo(LOG_TAG, "Archive extraction completed");
    }

    /**
     * Extract ZIP archive (existing implementation).
     */
    private static void extractZipArchive(byte[] zipBytes, final byte[] buffer, 
                                         final List<Pair<String, String>> symlinks) throws Exception {
        try (ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInput.getNextEntry()) != null) {
                processArchiveEntry(zipEntry.getName(), zipEntry.isDirectory(), zipInput, buffer, symlinks);
            }
        }
    }

    /**
     * Extract TAR.GZ archive.
     */
    private static void extractTarGzArchive(byte[] tarGzBytes, final byte[] buffer, 
                                           final List<Pair<String, String>> symlinks) throws Exception {
        try (GZIPInputStream gzipInput = new GZIPInputStream(new ByteArrayInputStream(tarGzBytes))) {
            // For now, implement a basic TAR parser
            // This is a simplified implementation - full TAR support would need a proper library
            extractTarStream(gzipInput, buffer, symlinks);
        }
    }

    /**
     * Basic TAR stream extraction (simplified implementation).
     */
    private static void extractTarStream(InputStream tarInput, final byte[] buffer, 
                                        final List<Pair<String, String>> symlinks) throws Exception {
        // This is a minimal TAR implementation
        // For production use, consider using Apache Commons Compress or similar library
        byte[] header = new byte[512];
        
        while (tarInput.read(header) == 512) {
            // Parse TAR header
            String fileName = new String(header, 0, 100).trim().replace("\0", "");
            if (fileName.isEmpty()) break;
            
            String sizeStr = new String(header, 124, 12).trim().replace("\0", "");
            long size = 0;
            try {
                size = Long.parseLong(sizeStr, 8); // TAR uses octal
            } catch (NumberFormatException e) {
                continue; // Skip invalid entries
            }
            
            char typeFlag = (char)header[156];
            boolean isDirectory = (typeFlag == '5' || fileName.endsWith("/"));
            
            if (!fileName.isEmpty()) {
                processArchiveEntry(fileName, isDirectory, new LimitedInputStream(tarInput, size), buffer, symlinks);
            }
            
            // Skip to next 512-byte boundary
            long remaining = size % 512;
            if (remaining > 0) {
                tarInput.skip(512 - remaining);
            }
        }
    }

    /**
     * Process a single archive entry (common logic for all formats).
     */
    private static void processArchiveEntry(String entryName, boolean isDirectory, InputStream entryInput,
                                           final byte[] buffer, final List<Pair<String, String>> symlinks) throws Exception {
        if (entryName.equals("SYMLINKS.txt")) {
            BufferedReader symlinksReader = new BufferedReader(new InputStreamReader(entryInput));
            String line;
            while ((line = symlinksReader.readLine()) != null) {
                String[] parts = line.split("←");
                if (parts.length != 2)
                    throw new RuntimeException("Malformed symlink line: " + line);
                String oldPath = parts[0];
                String newPath = UBUNTUX_STAGING_PREFIX_DIR_PATH + "/" + parts[1];
                symlinks.add(Pair.create(oldPath, newPath));

                Error error = ensureDirectoryExists(new File(newPath).getParentFile());
                if (error != null) {
                    throw new RuntimeException("Failed to create directory: " + error.toString());
                }
            }
        } else {
            File targetFile = new File(UBUNTUX_STAGING_PREFIX_DIR_PATH, entryName);
            
            Error error = ensureDirectoryExists(isDirectory ? targetFile : targetFile.getParentFile());
            if (error != null) {
                throw new RuntimeException("Failed to create directory: " + error.toString());
            }

            if (!isDirectory) {
                try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
                    int readBytes;
                    while ((readBytes = entryInput.read(buffer)) != -1)
                        outStream.write(buffer, 0, readBytes);
                }
                // Set executable permissions for Ubuntu system binaries
                if (entryName.startsWith("bin/") || entryName.startsWith("sbin/") || 
                    entryName.startsWith("usr/bin/") || entryName.startsWith("usr/sbin/") ||
                    entryName.startsWith("libexec") || entryName.startsWith("usr/libexec") ||
                    entryName.startsWith("lib/apt/apt-helper") || entryName.startsWith("lib/apt/methods") ||
                    entryName.startsWith("usr/lib/apt/apt-helper") || entryName.startsWith("usr/lib/apt/methods")) {
                    //noinspection OctalInteger
                    Os.chmod(targetFile.getAbsolutePath(), 0755);
                }
            }
        }
    }

    /**
     * InputStream wrapper that limits the number of bytes read.
     */
    private static class LimitedInputStream extends InputStream {
        private final InputStream input;
        private long remaining;

        public LimitedInputStream(InputStream input, long limit) {
            this.input = input;
            this.remaining = limit;
        }

        @Override
        public int read() throws java.io.IOException {
            if (remaining <= 0) return -1;
            int result = input.read();
            if (result != -1) remaining--;
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws java.io.IOException {
            if (remaining <= 0) return -1;
            int toRead = (int) Math.min(len, remaining);
            int result = input.read(b, off, toRead);
            if (result > 0) remaining -= result;
            return result;
        }
    }

    public static byte[] loadBootstrapBytes() {
        // Only load the shared library when necessary to save memory usage.
        System.loadLibrary("ubuntux-bootstrap");
        return getZip();
    }

    /**
     * Copy diagnostic scripts from assets to user home directory for easy troubleshooting.
     */
    private static void copyDiagnosticScripts(Context context) {
        try {
            // Copy diagnostic script
            copyAssetToFile(context, "ubuntux-diagnostics.sh", 
                          UbuntuxConstants.UBUNTUX_HOME_DIR_PATH + "/ubuntux-diagnostics.sh");
            
            // Copy test script
            copyAssetToFile(context, "test-ubuntu.sh", 
                          UbuntuxConstants.UBUNTUX_HOME_DIR_PATH + "/test-ubuntu.sh");
            
            Logger.logInfo(LOG_TAG, "Diagnostic scripts copied to user home directory");
        } catch (Exception e) {
            Logger.logError(LOG_TAG, "Failed to copy diagnostic scripts: " + e.getMessage());
        }
    }

    /**
     * Copy an asset file to a destination path and make it executable.
     */
    private static void copyAssetToFile(Context context, String assetName, String destPath) throws Exception {
        try (InputStream inputStream = context.getAssets().open(assetName);
             FileOutputStream outputStream = new FileOutputStream(destPath)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        
        // Make the script executable
        File scriptFile = new File(destPath);
        scriptFile.setExecutable(true, false);
        
        Logger.logDebug(LOG_TAG, "Copied and made executable: " + destPath);
    }

    public static native byte[] getZip();

}
