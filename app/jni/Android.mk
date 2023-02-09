LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
	LOCAL_MODULE := libqspower-1.2.1
	LOCAL_SRC_FILES := armeabi-v7a/libqspower-1.2.1.so
	include $(PREBUILT_SHARED_LIBRARY)
endif

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
	LOCAL_MODULE := libqspower-1.2.1
	LOCAL_SRC_FILES := arm64-v8a/libqspower-1.2.1.so
	include $(PREBUILT_SHARED_LIBRARY)
endif

include $(CLEAR_VARS)

LOCAL_MODULE    := boostpower
LOCAL_SRC_FILES := main.cpp
LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := libqspower-1.2.1
LOCAL_SHORT_COMMANDS := true
include $(BUILD_SHARED_LIBRARY)
