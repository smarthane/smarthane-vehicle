#include <stdlib.h>
#include <string.h>
#include <jni.h>

#include <android/log.h>
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

char* jstringTostring(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = (*env)->FindClass(env,"java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env,"utf-8");
    jmethodID mid = (*env)->GetMethodID(env,clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray)(*env)->CallObjectMethod(env,jstr, mid, strencode);
    jsize alen = (*env)->GetArrayLength(env,barr);
    jbyte* ba = (*env)->GetByteArrayElements(env,barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);// "\0"
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env,barr, ba, 0);
    return rtn;
}


jstring Java_com_smarthane_vehicle_app_jni_JNISample_getSample( JNIEnv* env, jobject thiz )
{
    char* str = "hello JNI";
    return (*env)->NewStringUTF(env,str);
}

JNIEXPORT jint JNICALL Java_com_smarthane_vehicle_app_jni_JNISample_intParams(JNIEnv * env, jobject thiz, jint x, jint y)
{
    return x + y;
}

JNIEXPORT jstring JNICALL Java_com_smarthane_vehicle_app_jni_JNISample_strParams(JNIEnv * env, jobject thiz, jstring str)
{
    char* cstr = jstringTostring(env, str);
    int len = strlen(cstr);
    if(len >0 )
    {
        int i=0;
        for(i;i<len;i++)
        {
            *(cstr+i)+=2;
        }
    }
    return (*env)->NewStringUTF(env, cstr);
}

JNIEXPORT jintArray JNICALL Java_com_smarthane_vehicle_app_jni_JNISample_arrParams(JNIEnv * env , jobject thiz, jintArray arr)
{
    int len = (*env)->GetArrayLength(env, arr);
    LOGD("length=%d",len);
    //jboolean isCopy = NULL;
    jint* p = (*env)->GetIntArrayElements(env,arr,NULL);
    int i;
    for (i=0; i < len; i++) {
        *(p+i)+=10;
    }
    jintArray array = (*env)-> NewIntArray(env, len);
    (*env)->SetIntArrayRegion(env,array, 0, len, p);
    return array;
}

