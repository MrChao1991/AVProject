package com.cfox.screenrecorder.decode.frame;

public abstract class Frame {
    public int forbidden_zero_bit = -1;
    public int nal_ref_idc = -1;
    public int nal_unit_type = -1;
}
