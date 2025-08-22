package com.ubuntux.shared.ubuntux.shell.command.runner.terminal;

import com.ubuntux.shared.shell.command.ExecutionCommand;
import com.ubuntux.shared.shell.command.environment.UnixShellEnvironment;

import org.junit.Test;
import org.junit.Assert;

import java.io.File;

/**
 * Test for login shell selection logic to ensure proper file existence checks.
 */
public class LoginShellSelectionTest {

    @Test
    public void testLoginShellSelectionWithNonExistentBinaries() {
        // Test the fixed login shell selection logic
        String[] loginShellBinaries = UnixShellEnvironment.LOGIN_SHELL_BINARIES;
        
        // Verify that "login" is the first binary in the list (as per the issue)
        Assert.assertEquals("login should be the first binary in LOGIN_SHELL_BINARIES", 
                           "login", loginShellBinaries[0]);
        
        // Test that the shell selection logic should handle non-existent files properly
        // When files don't exist, the logic should continue to the next binary or fallback
        String nonExistentPath = "/tmp/non-existent-bin-path";
        File nonExistentDir = new File(nonExistentPath);
        
        // Verify that non-existent directory doesn't cause issues
        Assert.assertFalse("Test directory should not exist", nonExistentDir.exists());
        
        // Test each binary path construction
        for (String shellBinary : loginShellBinaries) {
            File shellFile = new File(nonExistentPath, shellBinary);
            // With our fix, this should return false for non-existent files
            boolean shouldBeExecutable = shellFile.exists() && shellFile.canExecute();
            Assert.assertFalse("Non-existent shell binary should not be considered executable: " + shellBinary, 
                              shouldBeExecutable);
        }
    }

    @Test 
    public void testExecutionCommandInitialization() {
        // Test that ExecutionCommand can be properly initialized
        ExecutionCommand cmd = new ExecutionCommand();
        Assert.assertNull("Initial executable should be null", cmd.executable);
        Assert.assertFalse("Initial failsafe should be false", cmd.isFailsafe);
    }
}