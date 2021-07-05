//
// Created by chao.ma on 6/28/21.
//
#include <cstring>
#include "VideoEncoder.h"
#include "../nlog.h"

VideoEncoder::VideoEncoder() {

}

VideoEncoder::~VideoEncoder() {
    x264_picture_clean(pic_in);
}

void VideoEncoder::encodeData(int8_t *data) {

    // copy y 数据
    memcpy(pic_in->img.plane[0], data, mYSize);
    for (int i = 0; i < mUVSize; i ++) {
        // u 数据
        *(pic_in->img.plane[1] + i) = *(data + mYSize + i * 2 + 1);
        // v 数据
        *(pic_in->img.plane[2] + i) = *(data + mYSize + i * 2);
    }

    //编码出了几个 nalu （暂时理解为帧）  1   pi_nal  1  永远是1
    int pi_nal;

    // 编码出了几帧
    //编码出的数据 H264
    // pp_nal[]
    //编码出的参数  BufferInfo
    x264_nal_t *pp_nals;

    x264_picture_t pic_out;

    x264_encoder_encode(videoCodec, &pp_nals, &pi_nal, pic_in, &pic_out);

    if (pi_nal > 0) {
        for (int i = 0; i < pi_nal; ++i) {
            LOGE("输出索引:  %d  输出长度 %d type:%d",i,pi_nal, pp_nals[i].i_type);
            if (pp_nals[i].i_type == NAL_SPS) {
                LOGE("video frame type : psp========>");
            }  else if (pp_nals[i].i_type == NAL_PPS) {
                LOGE("video frame type : pps========>");
            }

//            pp_nals[i].i_payload; 编码数据长度
//            pp_nals[i].p_payload; 编码数据
            javaHelper->encodeH264(reinterpret_cast<char *>(pp_nals[i].p_payload), pp_nals[i].i_payload, pp_nals[i].i_type);
        }
    }

}

void VideoEncoder::initEncoder(int width, int height, int fps, int bitrate, int bFrame,
                               int iFrameInterval, int level_idc, const char *profileIdc) {


    if (videoCodec) {
        x264_encoder_close(videoCodec);
        videoCodec = nullptr;
    }

    mWidth = width;
    mHeight = height;
    mFps = fps;
    mBitrate = bitrate;

    mYSize = width * height;
    mUVSize = mYSize / 4;

    // 设置编码速度
    x264_param_default_preset(&param, "ultrafast", "zerolatency");
    // 编码等级
    param.i_level_idc = level_idc;
    // 选择显示格式
    param.i_csp = X264_CSP_I420;
    param.i_width = width;
    param.i_height = height;

    // 设置B frame
    param.i_bframe = bFrame;
    // 设置cpu 编码方式
    param.rc.i_rc_method = X264_RC_ABR;
    // k 为单位
    param.rc.i_bitrate = bitrate / 1024;

    /**
     * 帧率
     * 在x264 中帧率是通过分数进行计算的
     */
    param.i_fps_num = fps;
    param.i_fps_den = 1;

    param.i_frame_total = 0;
    param.b_open_gop = 0;
    param.i_bframe_pyramid = 0;
    param.rc.i_qp_constant=0;
    param.rc.i_qp_max=0;
    param.rc.i_qp_min=0;
    param.i_bframe_adaptive = X264_B_ADAPT_TRELLIS;

    // 分母
    param.i_timebase_den = param.i_fps_num;
    // 分子
    param.i_timebase_num = param.i_fps_num;

    // I 帧间隔  2s 一个I帧
//    param.i_keyint_max = fps * 2;
    param.i_keyint_min = fps * iFrameInterval;

    // 是否复sps和pps放在每个关键帧的前面 该参数设置是让每个关键帧(I帧)都附带sps/pps。
    param.b_repeat_headers = 1;

    // 设置线程
    param.i_threads = X264_SYNC_LOOKAHEAD_AUTO;

    x264_param_apply_profile(&param, profileIdc);

    // 容器
    pic_in = new x264_picture_t;
    // 设置初始化大小， 容器大小确定
    x264_picture_alloc(pic_in, X264_CSP_I420, width, height);

    LOGE("init x264 success .....");
    javaHelper->callStatus(1);
}

void VideoEncoder::start() {
    videoCodec = x264_encoder_open(&param);
    LOGE("start encoder success .......");
    javaHelper->callStatus(2);
}
