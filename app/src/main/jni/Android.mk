# Copyright (C) 2009 The Android Open Source Project
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
#makefile作用就是向编译系统描述我要编译的文件在什么位置 要生成的文件叫什么名字是什么类型
LOCAL_PATH := $(call my-dir)
#清除上次的编译信息
include $(CLEAR_VARS)
#在这里指定最后生成的谁的的名字
LOCAL_MODULE    := hello-jni
LOCAL_SRC_FILES := hello-jni.c
#要编译的C的代码的文件名

#加载liblog.so
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
#生成的是一个动态链接库.so
