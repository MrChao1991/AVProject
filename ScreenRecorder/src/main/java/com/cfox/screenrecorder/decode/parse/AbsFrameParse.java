package com.cfox.screenrecorder.decode.parse;

import com.cfox.screenrecorder.decode.frame.Frame;

abstract class AbsFrameParse<T extends Frame> implements FrameParse<T> {
    private BaseParse mBaseParse;
    private T t;
    public AbsFrameParse(BaseParse baseParse, T t) {
        this.mBaseParse = baseParse;
        this.t = t;
    }

    @Override
    public final T parseFrame() {
        this.t.forbidden_zero_bit = mBaseParse.getForbiddenZeroBit();
        this.t.nal_ref_idc = mBaseParse.getNalRefIdc();
        this.t.nal_unit_type = mBaseParse.getNalUnitType();
        onParseFrame(mBaseParse.getBytes(),t);
        return t;
    }

    abstract void onParseFrame(byte[] frameBytes, T t);

    int u(int bitIndex, byte[] frameByte) {
        return mBaseParse.u(bitIndex, frameByte);
    }

    int Ue(byte[] pBuff){
        return mBaseParse.Ue(pBuff);
    }
}
