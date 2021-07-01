#include <jni.h>
#include <string>
#include "nlog.h"
#include "core/VideoEncoder.h"


VideoEncoder *videoEncoder = nullptr;
//虚拟机的引用
JavaVM *javaVM = nullptr;

JavaCallHelper *helper = nullptr;
//RTMPPacket释放
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    LOGE("保存虚拟机的引用");
    return JNI_VERSION_1_4;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_vsencoder_VSEncoderNative_native_1init(JNIEnv *env, jobject thiz) {
    helper = new JavaCallHelper(javaVM, env, thiz);
    videoEncoder = new VideoEncoder();
    videoEncoder->javaHelper = helper;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_vsencoder_VSEncoderNative_native_1init_1encode_1params(JNIEnv *env, jobject thiz,
                                                                     jint width, jint height,
                                                                     jint fps, jint bitrate,
                                                                     jint i_frame_interval,
                                                                     jint b_frame, jint level_idc,
                                                                     jstring profile_idc) {

    const char *profile_name = env->GetStringUTFChars(profile_idc, NULL);
    if (videoEncoder) {
        videoEncoder->initEncoder(width, height, fps, bitrate, b_frame, i_frame_interval, level_idc, profile_name);
    }
    env->ReleaseStringUTFChars(profile_idc, profile_name);


}

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_vsencoder_VSEncoderNative_native_1start(JNIEnv *env, jobject thiz) {

    if (videoEncoder) {
        videoEncoder->start();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_vsencoder_VSEncoderNative_native_1encode_1yuv_1data(JNIEnv *env, jobject thiz,
                                                                  jbyteArray data) {

    if (videoEncoder) {
        // 转换java 层传过来的data
        jbyte  *videoData = env->GetByteArrayElements(data, NULL);
        videoEncoder->encodeData(videoData);
        // 释放
        env->ReleaseByteArrayElements(data, videoData, 0);
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_vsencoder_VSEncoderNative_native_1release(JNIEnv *env, jobject thiz) {

    if (videoEncoder) {
        delete videoEncoder;
        videoEncoder = nullptr;
    }

    if (helper) {
        delete helper;
        helper = nullptr;
    }
}