package com.cfox.screenrecorder.decode.frame;


public class SPSFrame extends Frame {

    //编码等级   Baseline Main Extended High   High 10   High 4:2:2
    public int profile_idc;
    //当constrained_set0_flag值为1的时候，就说明码流应该遵循基线profile(Baseline profile)的所有约束.constrained_set0_flag值为0时，说明码流不一定要遵循基线profile的所有约束。
    public int constraint_set0_flag;//(h264[1] & 0x80)>>7;
    //当constrained_set1_flag值为1的时候，就说明码流应该遵循主profile(Main profile)的所有约束.constrained_set1_flag值为0时，说明码流不一定要遵
    public int constraint_set1_flag;//(h264[1] & 0x40)>>6;
    //当constrained_set2_flag值为1的时候，就说明码流应该遵循扩展profile(Extended profile)的所有约束.constrained_set2_flag值为0时，说明码流不一定要遵循扩展profile的所有约束。
    public int constraint_set2_flag;//(h264[1] & 0x20)>>5;
    //注意：当constraint_set0_flag,constraint_set1_flag或constraint_set2_flag中不只一个值为1的话，那么码流必须满足所有相应指明的profile约束。
    public int constraint_set3_flag;//(h264[1] & 0x10)>>4;
    //4个零位
    public int reserved_zero_4bits;
    //它指的是码流对应的level级
    public int level_idc;
    //是否是哥伦布编码  0 是 1 不是
    public int seq_parameter_set_id;

    //颜色位数
    public int chroma_format_idc;
    public int bit_depth_luma_minus8;
    public int bit_depth_chroma_minus8;
    public int qpprime_y_zero_transform_bypass_flag;
    public int seq_scaling_matrix_presFrent_flag;


    public int log2_max_frame_num_minus4;

    public int pic_order_cnt_type;


    public int log2_max_pic_order_cnt_lsb_minus4;

    public int num_ref_frames;
    public int gaps_in_frame_num_value_allowed_flag;
    public int pic_width_in_mbs_minus1;
    public int pic_height_in_map_units_minus1;
    public int width;
    public int height;

    @Override
    public String toString() {
        return "SPSFrame{" +
                "base ===>, forbidden_zero_bit=" + forbidden_zero_bit +
                ", nal_ref_idc=" + nal_ref_idc +
                ", nal_unit_type=" + nal_unit_type +
                "SPS ===>,profile_idc=" + profile_idc +
                ", constraint_set0_flag=" + constraint_set0_flag +
                ", constraint_set1_flag=" + constraint_set1_flag +
                ", constraint_set2_flag=" + constraint_set2_flag +
                ", constraint_set3_flag=" + constraint_set3_flag +
                ", reserved_zero_4bits=" + reserved_zero_4bits +
                ", level_idc=" + level_idc +
                ", seq_parameter_set_id=" + seq_parameter_set_id +
                ", chroma_format_idc=" + chroma_format_idc +
                ", bit_depth_luma_minus8=" + bit_depth_luma_minus8 +
                ", bit_depth_chroma_minus8=" + bit_depth_chroma_minus8 +
                ", qpprime_y_zero_transform_bypass_flag=" + qpprime_y_zero_transform_bypass_flag +
                ", seq_scaling_matrix_presFrent_flag=" + seq_scaling_matrix_presFrent_flag +
                ", log2_max_frame_num_minus4=" + log2_max_frame_num_minus4 +
                ", pic_order_cnt_type=" + pic_order_cnt_type +
                ", log2_max_pic_order_cnt_lsb_minus4=" + log2_max_pic_order_cnt_lsb_minus4 +
                ", num_ref_frames=" + num_ref_frames +
                ", gaps_in_frame_num_value_allowed_flag=" + gaps_in_frame_num_value_allowed_flag +
                ", pic_width_in_mbs_minus1=" + pic_width_in_mbs_minus1 +
                ", pic_height_in_map_units_minus1=" + pic_height_in_map_units_minus1 +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
