package com.ubuntux.filepicker;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Point;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubuntux.shared.logger.Logger;
import com.ubuntux.shared.termux.TermuxConstants;
import com.ubuntux.shared.termux.utils.UbuntuxErrorUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A DocumentsProvider that provides access to Ubuntux files for the Android
 * Documents UI (Storage Access Framework).
 */
public class TermuxDocumentsProvider extends DocumentsProvider {

    private static final String LOG_TAG = "TermuxDocumentsProvider";

    // Root document columns
    private static final String[] DEFAULT_ROOT_PROJECTION = new String[] {
        DocumentsContract.Root.COLUMN_ROOT_ID,
        DocumentsContract.Root.COLUMN_MIME_TYPES,
        DocumentsContract.Root.COLUMN_FLAGS,
        DocumentsContract.Root.COLUMN_ICON,
        DocumentsContract.Root.COLUMN_TITLE,
        DocumentsContract.Root.COLUMN_SUMMARY,
        DocumentsContract.Root.COLUMN_DOCUMENT_ID,
    };

    // Document columns
    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[] {
        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
        DocumentsContract.Document.COLUMN_MIME_TYPE,
        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        DocumentsContract.Document.COLUMN_FLAGS,
        DocumentsContract.Document.COLUMN_SIZE,
    };

    private static final String ROOT_ID = "ubuntux_root";
    private static final String ROOT_DOCUMENT_ID = "ubuntux_files";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        // Check if external apps policy allows access
        String errmsg = UbuntuxErrorUtils.checkIfAllowExternalAppsPolicyIsViolated(getContext(), LOG_TAG);
        if (errmsg != null) {
            throw new FileNotFoundException(errmsg);
        }

        final MatrixCursor result = new MatrixCursor(resolveRootProjection(projection));
        final MatrixCursor.RowBuilder row = result.newRow();
        
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT_ID);
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, ROOT_DOCUMENT_ID);
        row.add(DocumentsContract.Root.COLUMN_TITLE, "Ubuntux");
        row.add(DocumentsContract.Root.COLUMN_SUMMARY, "Access to Ubuntux files");
        row.add(DocumentsContract.Root.COLUMN_ICON, com.ubuntux.R.mipmap.ic_launcher);
        row.add(DocumentsContract.Root.COLUMN_FLAGS, 
                DocumentsContract.Root.FLAG_SUPPORTS_CREATE |
                DocumentsContract.Root.FLAG_SUPPORTS_SEARCH |
                DocumentsContract.Root.FLAG_SUPPORTS_IS_CHILD);
        row.add(DocumentsContract.Root.COLUMN_MIME_TYPES, "*/*");

        return result;
    }

    @Override
    public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
        // Check if external apps policy allows access
        String errmsg = UbuntuxErrorUtils.checkIfAllowExternalAppsPolicyIsViolated(getContext(), LOG_TAG);
        if (errmsg != null) {
            throw new FileNotFoundException(errmsg);
        }

        final MatrixCursor result = new MatrixCursor(resolveDocumentProjection(projection));
        includeFile(result, documentId, getFileForDocId(documentId));
        return result;
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) 
            throws FileNotFoundException {
        // Check if external apps policy allows access
        String errmsg = UbuntuxErrorUtils.checkIfAllowExternalAppsPolicyIsViolated(getContext(), LOG_TAG);
        if (errmsg != null) {
            throw new FileNotFoundException(errmsg);
        }

        final MatrixCursor result = new MatrixCursor(resolveDocumentProjection(projection));
        final File parent = getFileForDocId(parentDocumentId);
        
        if (!parent.exists() || !parent.isDirectory()) {
            Logger.logError(LOG_TAG, "Parent directory does not exist or is not a directory: " + parent.getPath());
            return result;
        }

        File[] children = parent.listFiles();
        if (children != null) {
            for (File file : children) {
                includeFile(result, getDocIdForFile(file), file);
            }
        }

        return result;
    }

    @Override
    public ParcelFileDescriptor openDocument(String documentId, String mode, @Nullable CancellationSignal signal) 
            throws FileNotFoundException {
        // Check if external apps policy allows access
        String errmsg = UbuntuxErrorUtils.checkIfAllowExternalAppsPolicyIsViolated(getContext(), LOG_TAG);
        if (errmsg != null) {
            throw new FileNotFoundException(errmsg);
        }

        final File file = getFileForDocId(documentId);
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file.getPath());
        }

        final boolean isWrite = (mode.indexOf('w') != -1);
        if (isWrite) {
            // Check if file is in the allowed termux properties files that should be read-only
            try {
                String canonicalPath = file.getCanonicalPath();
                if (TermuxConstants.TERMUX_PROPERTIES_FILE_PATHS_LIST.contains(canonicalPath) ||
                    TermuxConstants.TERMUX_FLOAT_PROPERTIES_FILE_PATHS_LIST.contains(canonicalPath)) {
                    throw new FileNotFoundException("Write access denied to protected file: " + canonicalPath);
                }
            } catch (IOException e) {
                throw new FileNotFoundException("Error resolving file path: " + e.getMessage());
            }
        }

        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.parseMode(mode));
    }

    private String[] resolveRootProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_ROOT_PROJECTION;
    }

    private String[] resolveDocumentProjection(String[] projection) {
        return projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION;
    }

    private String getDocIdForFile(File file) {
        String path = file.getAbsolutePath();
        
        // For the root Ubuntux files directory
        if (path.equals(TermuxConstants.TERMUX_FILES_DIR_PATH)) {
            return ROOT_DOCUMENT_ID;
        }
        
        // For files within the Ubuntux directory, use relative path
        if (path.startsWith(TermuxConstants.TERMUX_FILES_DIR_PATH)) {
            return path.substring(TermuxConstants.TERMUX_FILES_DIR_PATH.length());
        }
        
        return path;
    }

    private File getFileForDocId(String documentId) throws FileNotFoundException {
        File target;
        
        if (ROOT_DOCUMENT_ID.equals(documentId)) {
            target = TermuxConstants.TERMUX_FILES_DIR;
        } else {
            // documentId should be a path relative to TERMUX_FILES_DIR
            target = new File(TermuxConstants.TERMUX_FILES_DIR, documentId);
        }

        if (!target.exists()) {
            throw new FileNotFoundException("Missing file for " + documentId + " at " + target);
        }

        // Security check: ensure file is within Ubuntux directory
        try {
            String canonicalPath = target.getCanonicalPath();
            if (!canonicalPath.startsWith(TermuxConstants.TERMUX_FILES_DIR.getCanonicalPath())) {
                throw new FileNotFoundException("Path traversal attack detected for " + documentId);
            }
        } catch (IOException e) {
            throw new FileNotFoundException("Error resolving canonical path: " + e.getMessage());
        }

        return target;
    }

    private void includeFile(MatrixCursor result, String docId, File file) {
        if (docId == null) {
            docId = getDocIdForFile(file);
        }

        int flags = 0;
        if (file.canWrite()) {
            if (file.isDirectory()) {
                flags |= DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE;
            } else {
                flags |= DocumentsContract.Document.FLAG_SUPPORTS_WRITE;
            }
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_DELETE;
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_RENAME;
        }

        String mimeType;
        if (file.isDirectory()) {
            mimeType = DocumentsContract.Document.MIME_TYPE_DIR;
        } else {
            mimeType = getTypeForFile(file);
        }

        if (mimeType.startsWith("image/")) {
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL;
        }

        final MatrixCursor.RowBuilder row = result.newRow();
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, docId);
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, file.getName());
        row.add(DocumentsContract.Document.COLUMN_SIZE, file.length());
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, mimeType);
        row.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, file.lastModified());
        row.add(DocumentsContract.Document.COLUMN_FLAGS, flags);
    }

    private String getTypeForFile(File file) {
        final String name = file.getName();
        final int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) {
            final String extension = name.substring(lastDot + 1).toLowerCase();
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }
}