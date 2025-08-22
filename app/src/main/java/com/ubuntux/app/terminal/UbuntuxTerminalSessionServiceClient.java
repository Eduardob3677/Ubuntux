package com.ubuntux.app.terminal;

import android.app.Service;

import androidx.annotation.NonNull;

import com.ubuntux.app.UbuntuxService;
import com.ubuntux.shared.ubuntux.shell.command.runner.terminal.UbuntuxSession;
import com.ubuntux.shared.ubuntux.terminal.UbuntuxTerminalSessionClientBase;
import com.ubuntux.terminal.TerminalSession;
import com.ubuntux.terminal.TerminalSessionClient;

/** The {@link TerminalSessionClient} implementation that may require a {@link Service} for its interface methods. */
public class UbuntuxTerminalSessionServiceClient extends UbuntuxTerminalSessionClientBase {

    private static final String LOG_TAG = "UbuntuxTerminalSessionServiceClient";

    private final UbuntuxService mService;

    public UbuntuxTerminalSessionServiceClient(UbuntuxService service) {
        this.mService = service;
    }

    @Override
    public void setTerminalShellPid(@NonNull TerminalSession terminalSession, int pid) {
        UbuntuxSession termuxSession = mService.getTermuxSessionForTerminalSession(terminalSession);
        if (termuxSession != null)
            termuxSession.getExecutionCommand().mPid = pid;
    }

}
