#include "JavaCallHelper.h"

JavaCallHelper::JavaCallHelper(JavaVM *javaVm, JNIEnv *_env, jobject &_jobj) : javaVM(javaVm), env(_env) {
    jobj = env->NewGlobalRef(_jobj);
    jclass jclazz = env->GetObjectClass(jobj);
    jmid_postData = env->GetMethodID(jclazz, "postData", "([B)V");
}

void JavaCallHelper::postH264(char *data, int length, int thread) {

    // NewByteArray 一定要在JavaVM 初始化在同一线程
    jbyteArray array = env->NewByteArray(length);
    env->SetByteArrayRegion(array, 0, length, reinterpret_cast<const jbyte *>(data));

    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            return;
        }

        jniEnv->CallVoidMethod(jobj, jmid_postData,array);
        javaVM->DetachCurrentThread();
    } else {
        env->CallVoidMethod(jobj, jmid_postData,array);
    }
}

JavaCallHelper::~JavaCallHelper() {
    env->DeleteGlobalRef(jobj);
    jobj = nullptr;
}
