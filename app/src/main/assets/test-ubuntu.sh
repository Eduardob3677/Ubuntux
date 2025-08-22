#!/bin/bash
# Simple Ubuntu binary test script
echo "Testing Ubuntu binaries..."

echo "Testing 'ls' command:"
ls --version 2>/dev/null && echo "✓ ls is working" || echo "✗ ls is not working"

echo "Testing 'bash' command:"
bash --version 2>/dev/null | head -1 && echo "✓ bash is working" || echo "✗ bash is not working"

echo "Testing 'cat' command:"
echo "test" | cat >/dev/null 2>&1 && echo "✓ cat is working" || echo "✗ cat is not working"

echo "PATH contains:"
echo "$PATH" | tr ':' '\n' | nl

echo "Current directory contents:"
ls -la 2>/dev/null || echo "ls command failed"

echo "Test complete."