#include <string.h>
#include <jni.h>
jstring Java_com_smarthane_vehicle_app_jni_JNISample_getSample( JNIEnv* env, jobject thiz )
{
    char* str = "hello JNI";
    return (*env)->NewStringUTF(env,str);
}
