#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
#common_ui:= ../Widget
#src_dirs:= src $(common_ui)/src
#res_dirs:= res $(common_ui)/res
LOCAL_SRC_FILES:= $(call all-java-files-under, src)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
#LOCAL_RESOURCE_DIR:= $(addprefix $(LOCAL_PATH)/, $(res_dirs))

LOCAL_AAPT_FLAGS:= --auto-add-overlay

LOCAL_STATIC_JAVA_LIBRARIES := android-common android-support-v4 weather-gson weather-okhttp weather-retrofit weather-converter-gson weather-okio

#LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := CarWeather
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
#LOCAL_SDK_VERSION := current
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := weather-gson:libs/gson-2.8.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += weather-okhttp:libs/okhttp-3.5.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += weather-retrofit:libs/retrofit-2.1.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += weather-converter-gson:libs/converter-gson-2.1.0.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += weather-okio:libs/okio-1.11.0.jar

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
