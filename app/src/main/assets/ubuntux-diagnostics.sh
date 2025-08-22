#!/bin/bash
# Ubuntux Diagnostic Script
# This script helps diagnose Ubuntu installation and environment issues

echo "=== Ubuntux Ubuntu Environment Diagnostics ==="
echo "Timestamp: $(date 2>/dev/null || echo 'date command not available')"
echo

echo "=== Environment Variables ==="
echo "HOME: $HOME"
echo "PREFIX: $PREFIX"
echo "PATH: $PATH"
echo "LD_LIBRARY_PATH: $LD_LIBRARY_PATH"
echo "SHELL: $SHELL"
echo "PWD: $PWD"
echo

echo "=== Directory Structure ==="
echo "Contents of /data/data/com.ubuntux/files/:"
ls -la /data/data/com.ubuntux/files/ 2>/dev/null || echo "Failed to list files directory"
echo

echo "Contents of /data/data/com.ubuntux/files/usr/:"
ls -la /data/data/com.ubuntux/files/usr/ 2>/dev/null || echo "Failed to list usr directory"
echo

echo "Contents of /data/data/com.ubuntux/files/usr/bin/ (first 20 files):"
ls -la /data/data/com.ubuntux/files/usr/bin/ 2>/dev/null | head -20 || echo "Failed to list usr/bin directory"
echo

echo "=== Essential Binary Check ==="
for binary in ls bash sh cat echo; do
    which_result=$(which $binary 2>/dev/null)
    if [ -n "$which_result" ]; then
        echo "$binary: $which_result ($(ls -la "$which_result" 2>/dev/null | awk '{print $1}'))"
    else
        echo "$binary: NOT FOUND in PATH"
    fi
done
echo

echo "=== Shell Information ==="
echo "Current shell: $0"
echo "Shell version:"
$SHELL --version 2>/dev/null | head -1 || echo "Shell version not available"
echo

echo "=== System Information ==="
echo "Architecture: $(uname -m 2>/dev/null || echo 'unknown')"
echo "Kernel: $(uname -r 2>/dev/null || echo 'unknown')"
echo

echo "=== Ubuntu Installation Verification ==="
if [ -f /etc/os-release ]; then
    echo "OS Release info:"
    cat /etc/os-release
else
    echo "No /etc/os-release found - Ubuntu may not be properly installed"
fi
echo

echo "=== Configuration Files ==="
echo "Ubuntux environment file:"
if [ -f /data/data/com.ubuntux/files/usr/etc/ubuntux/ubuntux.env ]; then
    echo "Environment file exists, first 10 lines:"
    head -10 /data/data/com.ubuntux/files/usr/etc/ubuntux/ubuntux.env
else
    echo "Environment file not found"
fi
echo

echo "=== Diagnostics Complete ==="
echo "If you're experiencing issues, please share this output with the developers."