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
Java_com_cfox_x264simple_x264_x264Lib_native_1init(JNIEnv *env, jobject thiz) {
    helper = new JavaCallHelper(javaVM, env, thiz);
    videoEncoder = new VideoEncoder();
    videoEncoder->javaHelper = helper;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_x264simple_x264_x264Lib_native_1video_1encode_1info(JNIEnv *env, jobject thiz,
                                                                  jint width, jint height, jint fps,
                                                                  jint bitrate) {

    if (videoEncoder) {
        videoEncoder->setVideoInfo(width, height, fps, bitrate);
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_x264simple_x264_x264Lib_native_1push_1yuv_1data(JNIEnv *env, jobject thiz,jbyteArray data) {
    if (videoEncoder) {
        jbyteArray array = env->NewByteArray(100);
        // 转换java 层传过来的data
        jbyte  *videoData = env->GetByteArrayElements(data, NULL);
        videoEncoder->encodeData(videoData);
        // 释放
        env->ReleaseByteArrayElements(data, videoData, 0);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_x264simple_x264_x264Lib_native_1release(JNIEnv *env, jobject thiz) {
    if (videoEncoder) {
        delete videoEncoder;
        videoEncoder = nullptr;
    }

    if (helper) {
        delete helper;
        helper = nullptr;
    }
}