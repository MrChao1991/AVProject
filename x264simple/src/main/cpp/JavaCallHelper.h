//
// Created by chao.ma on 6/29/21.
//

#ifndef AVPROJECT_JAVACALLHELPER_H
#define AVPROJECT_JAVACALLHELPER_H
#define THREAD_MAIN 1
#define THREAD_CHILD 2

#include <jni.h>

class JavaCallHelper {

public:
    JavaCallHelper(JavaVM *javaVm, JNIEnv *env, jobject &_jobj);

    void postH264(char *data, int length, int thread = THREAD_MAIN);


    ~JavaCallHelper();

public:
    JavaVM *javaVM;
    JNIEnv *env;
    jobject  jobj;
    jmethodID  jmid_postData;

};


#endif //AVPROJECT_JAVACALLHELPER_H
