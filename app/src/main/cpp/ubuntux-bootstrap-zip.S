     .global blob
     .global blob_size
     .section .rodata
 blob:
 #if defined __x86_64__
     .incbin "bootstrap-x86_64.tar.gz"
 #elif defined __aarch64__
     .incbin "bootstrap-aarch64.tar.gz"
 #elif defined __arm__
     .incbin "bootstrap-arm.tar.gz"
 #else
 # error Unsupported arch (Ubuntu 22.04 rootfs only supports amd64, arm64, armhf)
 #endif
 1:
 blob_size:
     .int 1b - blob
