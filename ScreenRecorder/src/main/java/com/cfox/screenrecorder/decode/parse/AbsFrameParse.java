package com.cfox.screenrecorder.decode.parse;

import com.cfox.screenrecorder.decode.frame.Frame;

abstract class AbsFrameParse<T extends Frame> implements FrameParse<T> {
    private BaseParse baseParse;

    public AbsFrameParse(BaseParse baseParse) {
        this.baseParse = baseParse;
    }

}
