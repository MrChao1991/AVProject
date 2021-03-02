package com.cfox.screenrecorder.decode.parse;

import com.cfox.screenrecorder.decode.frame.PPSFrame;

public class PPSParse extends AbsFrameParse<PPSFrame> {

    public PPSParse(BaseParse baseParse) {
        super(baseParse, new PPSFrame());
    }

    @Override
    void onParseFrame(byte[] frameBytes, PPSFrame ppsFrame) {

    }
}
