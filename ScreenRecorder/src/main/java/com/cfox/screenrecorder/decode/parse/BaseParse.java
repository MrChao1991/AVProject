package com.cfox.screenrecorder.decode.parse;

public interface BaseParse {

    int getForbiddenZeroBit();
    int getNalRefIdc();
    int getNalUnitType();
    byte[] getBytes();
}
