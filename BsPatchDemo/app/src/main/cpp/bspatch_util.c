//
// Created by 11320 on 2021/10/31.
//

#include "com_lwh_test_util_BsPatchUtil.h"

JNIEXPORT jint JNICALL Java_com_lwh_test_util_BsPatchUtil_patch
        (JNIEnv *env, jclass clazz, jstring old, jstring new, jstring patch){
    int args=4;
    char *argv[args];
    argv[0] = "bspatch";
    argv[1] = (char*)((*env)->GetStringUTFChars(env, old, 0));
    argv[2] = (char*)((*env)->GetStringUTFChars(env, new, 0));
    argv[3] = (char*)((*env)->GetStringUTFChars(env, patch, 0));

    //此处executePatch()就是上面我们修改出的
    int result = executePatch(args, argv);

    (*env)->ReleaseStringUTFChars(env, old, argv[1]);
    (*env)->ReleaseStringUTFChars(env, new, argv[2]);
    (*env)->ReleaseStringUTFChars(env, patch, argv[3]);

    return result;
}
