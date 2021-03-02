package com.cfox.screenrecorder.decode.parse;


import com.cfox.screenrecorder.decode.frame.SPSFrame;

public class SPSParse extends AbsFrameParse<SPSFrame> {

    public SPSParse(BaseParse baseParse) {
        super(baseParse, new SPSFrame());
    }

    @Override
    void onParseFrame(byte[] h264, SPSFrame spsFrame) {
        //编码等级   Baseline Main Extended High   High 10   High 4:2:2
        spsFrame.profile_idc = u(8, h264);
        //当constrained_set0_flag值为1的时候，就说明码流应该遵循基线profile(Baseline profile)的所有约束.constrained_set0_flag值为0时，说明码流不一定要遵循基线profile的所有约束。
        spsFrame.constraint_set0_flag = u(1, h264);//(h264[1] & 0x80)>>7;
        //当constrained_set1_flag值为1的时候，就说明码流应该遵循主profile(Main profile)的所有约束.constrained_set1_flag值为0时，说明码流不一定要遵
        spsFrame.constraint_set1_flag = u(1, h264);//(h264[1] & 0x40)>>6;
        //当constrained_set2_flag值为1的时候，就说明码流应该遵循扩展profile(Extended profile)的所有约束.constrained_set2_flag值为0时，说明码流不一定要遵循扩展profile的所有约束。
        spsFrame.constraint_set2_flag = u(1, h264);//(h264[1] & 0x20)>>5;
        //注意：当constraint_set0_flag,constraint_set1_flag或constraint_set2_flag中不只一个值为1的话，那么码流必须满足所有相应指明的profile约束。
        spsFrame.constraint_set3_flag = u(1, h264);//(h264[1] & 0x10)>>4;
        //4个零位
        spsFrame.reserved_zero_4bits = u(4, h264);
        //它指的是码流对应的level级
        spsFrame.level_idc = u(8, h264);
        //是否是哥伦布编码  0 是 1 不是
        spsFrame.seq_parameter_set_id = Ue(h264);
        if (spsFrame.profile_idc == 100) {
        //颜色位数
            spsFrame.chroma_format_idc=Ue(h264);
            spsFrame.bit_depth_luma_minus8   =Ue(h264);
            spsFrame.bit_depth_chroma_minus8  =Ue(h264);
            spsFrame.qpprime_y_zero_transform_bypass_flag=u(1, h264);
            spsFrame.seq_scaling_matrix_presFrent_flag     =u(1, h264);
        }
        spsFrame.log2_max_frame_num_minus4=Ue(h264);

        spsFrame.pic_order_cnt_type       =Ue(h264);

//        {
//            spsFrame.log2_max_pic_order_cnt_lsb_minus4=Ue(h264);
//        }
        spsFrame.num_ref_frames                      =Ue(h264);
        spsFrame.gaps_in_frame_num_value_allowed_flag=u(1,     h264);
        spsFrame.pic_width_in_mbs_minus1             =Ue(h264);
        spsFrame.pic_height_in_map_units_minus1      =Ue(h264);
        spsFrame.width=(spsFrame.pic_width_in_mbs_minus1       +1)*16;
        spsFrame.height=(spsFrame.pic_height_in_map_units_minus1+1)*16;

    }
}
