package com.smarthane.vehicle.app.jni;

/**
 * Created by smarthane on 2017/12/26.
 */

public class JNISample {


    static {
        System.loadLibrary("hello-jni");//名字注意，需要跟你的build.gradle ndk节点       下面的名字一样
    }

    public static native String getSample();

    public static native int intParams(int x,int y);

    public static native String strParams(String str);

    public static native int[] arrParams(int[] arr);

}
