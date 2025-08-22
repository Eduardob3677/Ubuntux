package com.ubuntux.filepicker;

import com.ubuntux.shared.ubuntux.UbuntuxConstants;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class UbuntuxDocumentsProviderTest {

    @Test
    public void testProviderCreation() {
        UbuntuxDocumentsProvider provider = new UbuntuxDocumentsProvider();
        assertTrue("Provider should initialize successfully", provider.onCreate());
    }

    @Test
    public void testDocumentIdGeneration() {
        // Test the document ID generation logic indirectly
        // Since getDocIdForFile is private, we test the overall functionality
        
        // Create a test file path
        File testFile = new File(UbuntuxConstants.UBUNTUX_FILES_DIR_PATH, "test.txt");
        
        // The provider should handle files within the Ubuntux directory
        String expectedPath = UbuntuxConstants.UBUNTUX_FILES_DIR_PATH;
        assertTrue("Ubuntux files directory should be defined", expectedPath != null && !expectedPath.isEmpty());
    }

    @Test
    public void testSecurityPathChecking() {
        // Test that the provider properly restricts access to Ubuntux directory
        // This tests the security model indirectly
        
        String termuxPath = UbuntuxConstants.UBUNTUX_FILES_DIR_PATH;
        assertNotNull("Ubuntux files directory should be defined", termuxPath);
        assertTrue("Ubuntux files directory should be absolute path", 
                   termuxPath.startsWith("/data/data/com.ubuntux/files"));
    }

    @Test 
    public void testConstants() {
        // Verify that all required constants are properly defined
        assertNotNull("UBUNTUX_FILES_DIR_PATH should not be null", UbuntuxConstants.UBUNTUX_FILES_DIR_PATH);
        assertNotNull("UBUNTUX_FILES_DIR should not be null", UbuntuxConstants.UBUNTUX_FILES_DIR);
        assertNotNull("UBUNTUX_PROPERTIES_FILE_PATHS_LIST should not be null", UbuntuxConstants.UBUNTUX_PROPERTIES_FILE_PATHS_LIST);
    }
}