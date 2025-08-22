LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := libubuntux-bootstrap
LOCAL_SRC_FILES := ubuntux-bootstrap-zip.S ubuntux-bootstrap.c
include $(BUILD_SHARED_LIBRARY)
