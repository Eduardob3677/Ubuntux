package com.ubuntux.shared.ubuntux.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.shell.command.ExecutionCommand;
import com.ubuntux.shared.ubuntux.settings.properties.UbuntuxAppSharedProperties;
import com.ubuntux.shared.errors.Errno;

/**
 * Utility class for handling execution command errors in Ubuntux without plugin dependencies.
 * This replaces UbuntuxPluginUtils for Ubuntu-specific functionality.
 */
public class UbuntuxErrorUtils {

    private static final String LOG_TAG = "UbuntuxErrorUtils";

    /**
     * Process execution command result.
     * Simplified version for Ubuntu environment without plugin support.
     * This mainly just logs the execution completion.
     *
     * @param context The context for operations.
     * @param logTag The log tag to use for logging.
     * @param executionCommand The execution command to process.
     */
    public static void processExecutionCommandResult(final Context context, String logTag, 
                                                   final ExecutionCommand executionCommand) {
        if (executionCommand == null) return;

        if (!executionCommand.hasExecuted()) {
            Logger.logWarn(logTag, executionCommand.getCommandIdAndLabelLogString() + 
                ": Ignoring call to processExecutionCommandResult() since the execution command state is not higher than the ExecutionState.EXECUTED");
            return;
        }

        // Log execution completion
        Logger.logDebugExtended(logTag, "Ubuntu execution command completed:\n" + 
            ExecutionCommand.getExecutionOutputLogString(executionCommand, true, true, true));
    }

    /**
     * Process execution command error by logging it.
     * Simplified version for Ubuntu environment without plugin support.
     *
     * @param context The context for operations.
     * @param logTag The log tag to use for logging.
     * @param executionCommand The execution command that failed.
     * @param forceNotification Currently ignored since Ubuntu doesn't use plugin notifications.
     */
    public static void processExecutionCommandError(final Context context, String logTag,
                                                  final ExecutionCommand executionCommand,
                                                  boolean forceNotification) {
        if (context == null || executionCommand == null) return;

        if (!executionCommand.isStateFailed()) {
            Logger.logWarn(logTag, executionCommand.getCommandIdAndLabelLogString() + 
                ": Ignoring call to processExecutionCommandError() since the execution command is not in failed state");
            return;
        }

        // Log the error details
        Logger.logErrorExtended(logTag, "Ubuntu execution command failed:\n" + 
            ExecutionCommand.getExecutionOutputLogString(executionCommand, true, true, true));
    }

    /**
     * Set execution command state to failed and process the error.
     *
     * @param context The context for operations.
     * @param logTag The log tag to use for logging.
     * @param executionCommand The execution command that failed.
     * @param forceNotification Currently ignored since Ubuntu doesn't use plugin notifications.
     * @param errmsg The error message.
     */
    public static void setAndProcessExecutionCommandError(final Context context, String logTag,
                                                        final ExecutionCommand executionCommand,
                                                        boolean forceNotification,
                                                        @NonNull String errmsg) {
        executionCommand.setStateFailed(Errno.ERRNO_FAILED.getCode(), errmsg);
        processExecutionCommandError(context, logTag, executionCommand, forceNotification);
    }

    /**
     * Check if external apps policy is violated.
     * For Ubuntu, we'll allow external apps by default but check the setting if available.
     *
     * @param context The context to get error string.
     * @param apiName The name of the API being called.
     * @return Returns error message if policy is violated, otherwise null.
     */
    public static String checkIfAllowExternalAppsPolicyIsViolated(final Context context, String apiName) {
        // For Ubuntu environment, we're more permissive by default
        // but still check the setting if available
        UbuntuxAppSharedProperties properties = UbuntuxAppSharedProperties.getProperties();
        if (properties != null && !properties.shouldAllowExternalApps()) {
            return "External app access is disabled in Ubuntu settings for: " + apiName;
        }
        return null;
    }
}