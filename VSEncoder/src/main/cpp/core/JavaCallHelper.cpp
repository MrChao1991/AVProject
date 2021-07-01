#include "JavaCallHelper.h"

JavaCallHelper::JavaCallHelper(JavaVM *javaVm, JNIEnv *_env, jobject &_jobj) : javaVM(javaVm), env(_env) {
    jobj = env->NewGlobalRef(_jobj);
    jclass jclazz = env->GetObjectClass(jobj);
    encodeCallbackId = env->GetMethodID(jclazz, "nativeEncodeData", "([B)V");
    statusCallbackId = env->GetMethodID(jclazz, "nativeEncodeStatus", "(I)V");
    env->DeleteLocalRef(jclazz);
}

void JavaCallHelper::encodeH264(char *data, int length) {

    // NewByteArray 一定要在JavaVM 初始化在同一线程
    jbyteArray array = env->NewByteArray(length);
    env->SetByteArrayRegion(array, 0, length, reinterpret_cast<const jbyte *>(data));
    env->CallVoidMethod(jobj, encodeCallbackId,array);
    env->DeleteLocalRef(array);

}

JavaCallHelper::~JavaCallHelper() {
    env->DeleteGlobalRef(jobj);
    jobj = nullptr;
}

void JavaCallHelper::callStatus(int code) {
    env->CallVoidMethod(jobj, statusCallbackId, code);

}
