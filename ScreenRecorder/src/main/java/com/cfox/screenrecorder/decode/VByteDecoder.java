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

    public List<Frame> parseBytes(byte[] bytes) {
        List<Frame> frames = new ArrayList<>();

        int totalSize = bytes.length;
        int startIndex = 0;
        boolean endParse = true;
        while (totalSize > 0 && endParse) {
            int nextFrameStart = findByFrame(bytes, startIndex + 2 , totalSize);
            if (nextFrameStart > 0 && nextFrameStart < totalSize) {
                byte[] framesByte = spliteFramesByte(bytes, startIndex, nextFrameStart - startIndex);
                Frame frame = parseFrame(framesByte);
                if (frame != null) {
                    frames.add(frame);
                }
                startIndex = nextFrameStart;
            } else {
                endParse = false;
            }
        }

        return frames;
    }

    private byte[] spliteFramesByte(byte[] array,int start,int lenght) {
        byte[] newArray = new byte[lenght];
        for (int i = start; i < start + lenght; i++) {
            newArray[i - start] = array[i];
        }
        return newArray;
    }


    private int findByFrame( byte[] bytes, int start, int totalSize) {
        for (int i = start; i < totalSize; i++) {
            if ((bytes[i] == 0x00 && bytes[i + 1] == 0x00 && bytes[i + 2] == 0x00
                    && bytes[i + 3] == 0x01)||(bytes[i] == 0x00 && bytes[i + 1] == 0x00
                    && bytes[i + 2] == 0x01)) {
                return i;
            }
        }
        return -1;  // Not found
    }

    private Frame parseFrame(byte[] bytes) {
        baseParse.parseBytes(bytes);
        int forbiddenZeroBit = baseParse.getForbiddenZeroBit();
        int nalUnitType = baseParse.getNalUnitType();

        if (forbiddenZeroBit == 0) {
            return mNalUnitTypeParse.get(nalUnitType).parseFrame();
        }
        return null;
    }
}
