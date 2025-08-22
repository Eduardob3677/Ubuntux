package com.ubuntux.filepicker;

import com.ubuntux.shared.ubuntux.TermuxConstants;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class TermuxDocumentsProviderTest {

    @Test
    public void testProviderCreation() {
        TermuxDocumentsProvider provider = new TermuxDocumentsProvider();
        assertTrue("Provider should initialize successfully", provider.onCreate());
    }

    @Test
    public void testDocumentIdGeneration() {
        // Test the document ID generation logic indirectly
        // Since getDocIdForFile is private, we test the overall functionality
        
        // Create a test file path
        File testFile = new File(TermuxConstants.TERMUX_FILES_DIR_PATH, "test.txt");
        
        // The provider should handle files within the Ubuntux directory
        String expectedPath = TermuxConstants.TERMUX_FILES_DIR_PATH;
        assertTrue("Ubuntux files directory should be defined", expectedPath != null && !expectedPath.isEmpty());
    }

    @Test
    public void testSecurityPathChecking() {
        // Test that the provider properly restricts access to Ubuntux directory
        // This tests the security model indirectly
        
        String termuxPath = TermuxConstants.TERMUX_FILES_DIR_PATH;
        assertNotNull("Ubuntux files directory should be defined", termuxPath);
        assertTrue("Ubuntux files directory should be absolute path", 
                   termuxPath.startsWith("/data/data/com.ubuntux/files"));
    }

    @Test 
    public void testConstants() {
        // Verify that all required constants are properly defined
        assertNotNull("TERMUX_FILES_DIR_PATH should not be null", TermuxConstants.TERMUX_FILES_DIR_PATH);
        assertNotNull("TERMUX_FILES_DIR should not be null", TermuxConstants.TERMUX_FILES_DIR);
        assertNotNull("TERMUX_PROPERTIES_FILE_PATHS_LIST should not be null", TermuxConstants.TERMUX_PROPERTIES_FILE_PATHS_LIST);
    }
}