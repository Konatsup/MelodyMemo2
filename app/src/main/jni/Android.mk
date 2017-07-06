LOCAL_PATH := $(call my-dir)
LOCAL_SHARED_LIBRARIES := libutils libcutils

LOCAL_LDLIBS := -llog \
                -landroid

include $(CLEAR_VARS)
LOCAL_MODULE  := calc
LOCAL_MODULE  := testC


LOCAL_SRC_FILES := calc.c
LOCAL_SRC_FILES := testC.c


include $(BUILD_SHARED_LIBRARY)