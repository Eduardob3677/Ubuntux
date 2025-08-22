package com.ubuntux.view;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.ubuntux.terminal.TerminalSession;

/**
 * Interface for providing communication between a TerminalView and its client.
 */
public interface TerminalViewClient {

    float onScale(float scale);

    void onSingleTapUp(MotionEvent e);

    boolean isTerminalViewSelected();

    void copyModeChanged(boolean copyMode);

    boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session);

    boolean onKeyUp(int keyCode, KeyEvent e);

    boolean onLongPress(MotionEvent event);

    boolean readControlKey();

    boolean readAltKey();

    boolean readShiftKey();

    boolean readFnKey();

    boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session);

    void onEmulatorSet();

    // Logging methods
    void logError(String tag, String message);

    void logWarn(String tag, String message);

    void logInfo(String tag, String message);

    void logDebug(String tag, String message);

    void logVerbose(String tag, String message);

    void logStackTraceWithMessage(String tag, String message, Exception e);

    void logStackTrace(String tag, Exception e);

    // Additional methods that may be used by implementations
    default boolean shouldBackButtonBeMappedToEscape() {
        return false;
    }

    default boolean shouldEnforceCharBasedInput() {
        return false;
    }

    default boolean shouldUseCtrlSpaceWorkaround() {
        return false;
    }
}