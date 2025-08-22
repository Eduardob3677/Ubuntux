package com.ubuntux.shared.ubuntux.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ubuntux.shared.shell.command.ExecutionCommand;
import com.ubuntux.shared.shell.command.environment.ShellCommandShellEnvironment;
import com.ubuntux.shared.shell.command.environment.ShellEnvironmentUtils;
import com.ubuntux.shared.ubuntux.settings.preferences.UbuntuxAppSharedPreferences;
import com.ubuntux.shared.ubuntux.shell.UbuntuxShellManager;

import java.util.HashMap;

/**
 * Environment for Termux {@link ExecutionCommand}.
 */
public class UbuntuxShellCommandShellEnvironment extends ShellCommandShellEnvironment {

    /** Get shell environment containing info for Termux {@link ExecutionCommand}. */
    @NonNull
    @Override
    public HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext,
                                                  @NonNull ExecutionCommand executionCommand) {
        HashMap<String, String> environment = super.getEnvironment(currentPackageContext, executionCommand);

        UbuntuxAppSharedPreferences preferences = UbuntuxAppSharedPreferences.build(currentPackageContext);
        if (preferences == null) return environment;

        if (ExecutionCommand.Runner.APP_SHELL.equalsRunner(executionCommand.runner)) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__APP_SHELL_NUMBER_SINCE_BOOT,
                String.valueOf(preferences.getAndIncrementAppShellNumberSinceBoot()));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__APP_SHELL_NUMBER_SINCE_APP_START,
                String.valueOf(UbuntuxShellManager.getAndIncrementAppShellNumberSinceAppStart()));

        } else if (ExecutionCommand.Runner.TERMINAL_SESSION.equalsRunner(executionCommand.runner)) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__TERMINAL_SESSION_NUMBER_SINCE_BOOT,
                String.valueOf(preferences.getAndIncrementTerminalSessionNumberSinceBoot()));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__TERMINAL_SESSION_NUMBER_SINCE_APP_START,
                String.valueOf(UbuntuxShellManager.getAndIncrementTerminalSessionNumberSinceAppStart()));
        } else {
            return environment;
        }

        return environment;
    }

}
