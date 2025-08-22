package com.ubuntux.shared.ubuntux.shell.am;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.errors.Error;
import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.net.socket.local.LocalClientSocket;
import com.ubuntux.shared.net.socket.local.LocalServerSocket;
import com.ubuntux.shared.net.socket.local.LocalSocketManager;
import com.ubuntux.shared.net.socket.local.LocalSocketManagerClientBase;
import com.ubuntux.shared.net.socket.local.LocalSocketRunConfig;
import com.ubuntux.shared.shell.am.AmSocketServerRunConfig;
import com.ubuntux.shared.shell.am.AmSocketServer;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;
import com.ubuntux.shared.ubuntux.crash.UbuntuxCrashUtils;
import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.ubuntux.settings.properties.UbuntuxAppSharedProperties;
import com.ubuntux.shared.ubuntux.settings.properties.UbuntuxPropertyConstants;
import com.ubuntux.shared.ubuntux.shell.command.environment.UbuntuxAppShellEnvironment;

/**
 * A wrapper for {@link AmSocketServer} for termux-app usage.
 *
 * The static {@link #termuxAmSocketServer} variable stores the {@link LocalSocketManager} for the
 * {@link AmSocketServer}.
 *
 * The {@link TermuxAmSocketServerClient} extends the {@link AmSocketServer.AmSocketServerClient}
 * class to also show plugin error notifications for errors and disallowed client connections in
 * addition to logging the messages to logcat, which are only logged by {@link LocalSocketManagerClientBase}
 * if log level is debug or higher for privacy issues.
 *
 * It uses a filesystem socket server with the socket file at
 * {@link UbuntuxConstants.UBUNTUX_APP#UBUNTUX_AM_SOCKET_FILE_PATH}. It would normally only allow
 * processes belonging to the termux user and root user to connect to it. If commands are sent by the
 * root user, then the am commands executed will be run as the termux user and its permissions,
 * capabilities and selinux context instead of root.
 *
 * The `$PREFIX/bin/termux-am` client connects to the server via `$PREFIX/bin/termux-am-socket` to
 * run the am commands. It provides similar functionality to "$PREFIX/bin/am"
 * (and "/system/bin/am"), but should be faster since it does not require starting a dalvik vm for
 * every command as done by "am" via termux/TermuxAm.
 *
 * The server is started by termux-app Application class but is not started if
 * {@link UbuntuxPropertyConstants#KEY_RUN_TERMUX_AM_SOCKET_SERVER} is `false` which can be done by
 * adding the prop with value "false" to the "~/.termux/termux.properties" file. Changes
 * require termux-app to be force stopped and restarted.
 *
 * The current state of the server can be checked with the
 * {@link UbuntuxAppShellEnvironment#ENV_TERMUX_APP__AM_SOCKET_SERVER_ENABLED} env variable, which is exported
 * for all shell sessions and tasks.
 *
 * https://github.com/termux/termux-am-socket
 * https://github.com/termux/TermuxAm
 */
public class UbuntuxAmSocketServer {

    public static final String LOG_TAG = "UbuntuxAmSocketServer";

    public static final String TITLE = "TermuxAm";

    /** The static instance for the {@link UbuntuxAmSocketServer} {@link LocalSocketManager}. */
    private static LocalSocketManager termuxAmSocketServer;

    /** Whether {@link UbuntuxAmSocketServer} is enabled and running or not. */
    @Keep
    protected static Boolean UBUNTUX_APP_AM_SOCKET_SERVER_ENABLED;

    /**
     * Setup the {@link AmSocketServer} {@link LocalServerSocket} and start listening for
     * new {@link LocalClientSocket} if enabled.
     *
     * @param context The {@link Context} for {@link LocalSocketManager}.
     */
    public static void setupTermuxAmSocketServer(@NonNull Context context) {
        // Start termux-am-socket server if enabled by user
        boolean enabled = false;
        if (UbuntuxAppSharedProperties.getProperties().shouldRunTermuxAmSocketServer()) {
            Logger.logDebug(LOG_TAG, "Starting " + TITLE + " socket server since its enabled");
            start(context);
            if (termuxAmSocketServer != null && termuxAmSocketServer.isRunning()) {
                enabled = true;
                Logger.logDebug(LOG_TAG, TITLE + " socket server successfully started");
            }
        } else {
            Logger.logDebug(LOG_TAG, "Not starting " + TITLE + " socket server since its not enabled");
        }

        // Once termux-app has started, the server state must not be changed since the variable is
        // exported in shell sessions and tasks and if state is changed, then env of older shells will
        // retain invalid value. User should force stop the app to update state after changing prop.
        UBUNTUX_APP_AM_SOCKET_SERVER_ENABLED = enabled;
        UbuntuxAppShellEnvironment.updateTermuxAppAMSocketServerEnabled(context);
    }

    /**
     * Create the {@link AmSocketServer} {@link LocalServerSocket} and start listening for new {@link LocalClientSocket}.
     */
    public static synchronized void start(@NonNull Context context) {
        stop();

        AmSocketServerRunConfig amSocketServerRunConfig = new AmSocketServerRunConfig(TITLE,
            UbuntuxConstants.UBUNTUX_APP.UBUNTUX_AM_SOCKET_FILE_PATH, new TermuxAmSocketServerClient());

        termuxAmSocketServer = AmSocketServer.start(context, amSocketServerRunConfig);
    }

    /**
     * Stop the {@link AmSocketServer} {@link LocalServerSocket} and stop listening for new {@link LocalClientSocket}.
     */
    public static synchronized void stop() {
        if (termuxAmSocketServer != null) {
            Error error = termuxAmSocketServer.stop();
            if (error != null) {
                termuxAmSocketServer.onError(error);
            }
            termuxAmSocketServer = null;
        }
    }
    
    /**
     * Update the state of the {@link AmSocketServer} {@link LocalServerSocket} depending on current
     * value of {@link UbuntuxPropertyConstants#KEY_RUN_TERMUX_AM_SOCKET_SERVER}.
     */
    public static synchronized void updateState(@NonNull Context context) {
        UbuntuxAppSharedProperties properties = UbuntuxAppSharedProperties.getProperties();
        if (properties.shouldRunTermuxAmSocketServer()) {
            if (termuxAmSocketServer == null) {
                Logger.logDebug(LOG_TAG, "updateState: Starting " + TITLE + " socket server");
                start(context);
            }
        } else {
            if (termuxAmSocketServer != null) {
                Logger.logDebug(LOG_TAG, "updateState: Disabling " + TITLE + " socket server");
                stop();
            }
        }
    }
    
    /**
     * Get {@link #termuxAmSocketServer}.
     */
    public static synchronized LocalSocketManager getTermuxAmSocketServer() {
        return termuxAmSocketServer;
    }

    /**
     * Log an error instead of showing notification (Ubuntu doesn't use plugin notifications).
     *
     * @param context The {@link Context} to send the notification with.
     * @param error The {@link Error} generated.
     * @param localSocketRunConfig The {@link LocalSocketRunConfig} for {@link LocalSocketManager}.
     * @param clientSocket The optional {@link LocalClientSocket} for which the error was generated.
     */
    public static synchronized void showErrorNotification(@NonNull Context context, @NonNull Error error,
                                                          @NonNull LocalSocketRunConfig localSocketRunConfig,
                                                          @Nullable LocalClientSocket clientSocket) {
        // For Ubuntu, we just log the error instead of showing a plugin notification
        Logger.logErrorExtended(LOG_TAG, localSocketRunConfig.getTitle() + " Socket Server Error: " + 
            error.getMinimalErrorString());
    }



    public static Boolean getTermuxAppAMSocketServerEnabled(@NonNull Context currentPackageContext) {
        boolean isTermuxApp = UbuntuxConstants.UBUNTUX_PACKAGE_NAME.equals(currentPackageContext.getPackageName());
        if (isTermuxApp) {
            return UBUNTUX_APP_AM_SOCKET_SERVER_ENABLED;
        } else {
            // Currently, unsupported since plugin app processes don't know that value is set in termux
            // app process UbuntuxAmSocketServer class. A binder API or a way to check if server is actually
            // running needs to be used. Long checks would also not be possible on main application thread
            return null;
        }

    }





    /** Enhanced implementation for {@link AmSocketServer.AmSocketServerClient} for {@link UbuntuxAmSocketServer}. */
    public static class TermuxAmSocketServerClient extends AmSocketServer.AmSocketServerClient {

        public static final String LOG_TAG = "TermuxAmSocketServerClient";

        @Nullable
        @Override
        public Thread.UncaughtExceptionHandler getLocalSocketManagerClientThreadUEH(
            @NonNull LocalSocketManager localSocketManager) {
            // Use termux crash handler for socket listener thread just like used for main app process thread.
            return UbuntuxCrashUtils.getCrashHandler(localSocketManager.getContext());
        }

        @Override
        public void onError(@NonNull LocalSocketManager localSocketManager,
                            @Nullable LocalClientSocket clientSocket, @NonNull Error error) {
            // Don't show notification if server is not running since errors may be triggered
            // when server is stopped and server and client sockets are closed.
            if (localSocketManager.isRunning()) {
                UbuntuxAmSocketServer.showErrorNotification(localSocketManager.getContext(), error,
                    localSocketManager.getLocalSocketRunConfig(), clientSocket);
            }

            // But log the exception
            super.onError(localSocketManager, clientSocket, error);
        }

        @Override
        public void onDisallowedClientConnected(@NonNull LocalSocketManager localSocketManager,
                                                @NonNull LocalClientSocket clientSocket, @NonNull Error error) {
            // Always show notification and log error regardless of if server is running or not
            UbuntuxAmSocketServer.showErrorNotification(localSocketManager.getContext(), error,
                localSocketManager.getLocalSocketRunConfig(), clientSocket);
            super.onDisallowedClientConnected(localSocketManager, clientSocket, error);
        }



        @Override
        protected String getLogTag() {
            return LOG_TAG;
        }

    }

}
