//
// Created by chao.ma on 6/29/21.
//

#ifndef AVPROJECT_JAVACALLHELPER_H
#define AVPROJECT_JAVACALLHELPER_H

#include <jni.h>

class JavaCallHelper {

public:
    JavaCallHelper(JavaVM *javaVm, JNIEnv *env, jobject &_jobj);

    void encodeH264(char *data, int length, int type);

    void callStatus(int code);


    ~JavaCallHelper();

public:
    JavaVM *javaVM;
    JNIEnv *env;
    jobject  jobj;
    jmethodID  encodeCallbackId;
    jmethodID  statusCallbackId;

};


#endif //AVPROJECT_JAVACALLHELPER_H
