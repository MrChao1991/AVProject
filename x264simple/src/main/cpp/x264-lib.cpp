#include <jni.h>
#include <string>
#include "VideoEncoder.h"


VideoEncoder *videoEncoder = 0

extern "C"
JNIEXPORT void JNICALL
Java_com_cfox_x264simple_x264_x264Lib_native_1init(JNIEnv *env, jobject thiz) {
    videoEncoder = new VideoEncoder();

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
        videoEncoder = 0;
    }
}