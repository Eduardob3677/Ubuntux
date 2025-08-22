LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:= libubuntux
LOCAL_SRC_FILES:= ubuntux.c
include $(BUILD_SHARED_LIBRARY)
