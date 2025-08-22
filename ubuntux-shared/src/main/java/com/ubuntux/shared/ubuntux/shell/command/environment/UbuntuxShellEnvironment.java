package com.ubuntux.shared.ubuntux.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ubuntux.shared.errors.Error;
import com.ubuntux.shared.file.FileUtils;
import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.shell.command.ExecutionCommand;
import com.ubuntux.shared.shell.command.environment.AndroidShellEnvironment;
import com.ubuntux.shared.shell.command.environment.ShellEnvironmentUtils;
import com.ubuntux.shared.shell.command.environment.ShellCommandShellEnvironment;
import com.ubuntux.shared.ubuntux.UbuntuxBootstrap;
import com.ubuntux.shared.ubuntux.UbuntuxConstants;
import com.ubuntux.shared.ubuntux.shell.UbuntuxShellUtils;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Environment for Termux.
 */
public class UbuntuxShellEnvironment extends AndroidShellEnvironment {

    private static final String LOG_TAG = "UbuntuxShellEnvironment";

    /** Environment variable for the termux {@link UbuntuxConstants#UBUNTUX_PREFIX_DIR_PATH}. */
    public static final String ENV_PREFIX = "PREFIX";

    public UbuntuxShellEnvironment() {
        super();
        shellCommandShellEnvironment = new UbuntuxShellCommandShellEnvironment();
    }


    /** Init {@link UbuntuxShellEnvironment} constants and caches. */
    public synchronized static void init(@NonNull Context currentPackageContext) {
        UbuntuxAppShellEnvironment.setTermuxAppEnvironment(currentPackageContext);
    }

    /** Init {@link UbuntuxShellEnvironment} constants and caches. */
    public synchronized static void writeEnvironmentToFile(@NonNull Context currentPackageContext) {
        HashMap<String, String> environmentMap = new UbuntuxShellEnvironment().getEnvironment(currentPackageContext, false);
        String environmentString = ShellEnvironmentUtils.convertEnvironmentToDotEnvFile(environmentMap);

        // Write environment string to temp file and then move to final location since otherwise
        // writing may happen while file is being sourced/read
        Error error = FileUtils.writeTextToFile("termux.env.tmp", UbuntuxConstants.UBUNTUX_ENV_TEMP_FILE_PATH,
            Charset.defaultCharset(), environmentString, false);
        if (error != null) {
            Logger.logErrorExtended(LOG_TAG, error.toString());
            return;
        }

        error = FileUtils.moveRegularFile("termux.env.tmp", UbuntuxConstants.UBUNTUX_ENV_TEMP_FILE_PATH, UbuntuxConstants.UBUNTUX_ENV_FILE_PATH, true);
        if (error != null) {
            Logger.logErrorExtended(LOG_TAG, error.toString());
        }
    }

    /** Get shell environment for Termux. */
    @NonNull
    @Override
    public HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext, boolean isFailSafe) {

        // Termux environment builds upon the Android environment
        HashMap<String, String> environment = super.getEnvironment(currentPackageContext, isFailSafe);

        HashMap<String, String> termuxAppEnvironment = UbuntuxAppShellEnvironment.getEnvironment(currentPackageContext);
        if (termuxAppEnvironment != null)
            environment.putAll(termuxAppEnvironment);

        HashMap<String, String> termuxApiAppEnvironment = UbuntuxAPIShellEnvironment.getEnvironment(currentPackageContext);
        if (termuxApiAppEnvironment != null)
            environment.putAll(termuxApiAppEnvironment);

        environment.put(ENV_HOME, UbuntuxConstants.UBUNTUX_HOME_DIR_PATH);
        environment.put(ENV_PREFIX, UbuntuxConstants.UBUNTUX_PREFIX_DIR_PATH);

        // If failsafe is not enabled, then we keep default PATH and TMPDIR so that system binaries can be used
        if (!isFailSafe) {
            environment.put(ENV_TMPDIR, UbuntuxConstants.UBUNTUX_TMP_PREFIX_DIR_PATH);
            if (UbuntuxBootstrap.isAppPackageManagerUbuntu()) {
                // Ubuntu binaries use standard paths, no need for applets directory
                environment.put(ENV_PATH, UbuntuxConstants.UBUNTUX_BIN_PREFIX_DIR_PATH);
                environment.put(ENV_LD_LIBRARY_PATH, UbuntuxConstants.UBUNTUX_LIB_PREFIX_DIR_PATH);
            } else {
                // Standard binaries rely on DT_RUNPATH, so LD_LIBRARY_PATH should be unset by default
                environment.put(ENV_PATH, UbuntuxConstants.UBUNTUX_BIN_PREFIX_DIR_PATH);
                environment.remove(ENV_LD_LIBRARY_PATH);
            }
        }

        return environment;
    }


    @NonNull
    @Override
    public String getDefaultWorkingDirectoryPath() {
        return UbuntuxConstants.UBUNTUX_HOME_DIR_PATH;
    }

    @NonNull
    @Override
    public String getDefaultBinPath() {
        return UbuntuxConstants.UBUNTUX_BIN_PREFIX_DIR_PATH;
    }

    @NonNull
    @Override
    public String[] setupShellCommandArguments(@NonNull String executable, String[] arguments) {
        return UbuntuxShellUtils.setupShellCommandArguments(executable, arguments);
    }

}
