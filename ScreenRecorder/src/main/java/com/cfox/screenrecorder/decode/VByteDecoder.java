package com.cfox.screenrecorder.decode;

import com.cfox.screenrecorder.decode.frame.Frame;
import com.cfox.screenrecorder.decode.parse.BaserFrameParse;
import com.cfox.screenrecorder.decode.parse.FrameParse;
import com.cfox.screenrecorder.decode.parse.PPSParse;
import com.cfox.screenrecorder.decode.parse.SPSParse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VByteDecoder {

    private final HashMap<Integer, FrameParse> mNalUnitTypeParse = new HashMap<>();
    private final BaserFrameParse baseParse = new BaserFrameParse();

    public VByteDecoder() {
        mNalUnitTypeParse.put(7, new SPSParse(baseParse));
        mNalUnitTypeParse.put(8, new PPSParse(baseParse));
    }
    public List<Frame> pressFrame(byte[] bytes) {
        List<Frame> frames = new ArrayList<>();
        baseParse.parseBytes(bytes);
        int forbiddenZeroBit = baseParse.getForbiddenZeroBit();
        int nalUnitType = baseParse.getNalUnitType();

        if (forbiddenZeroBit == 0) {
            Frame frame = mNalUnitTypeParse.get(nalUnitType).parseFrame();
            frames.add(frame);

        }
        return frames;
    }
}
