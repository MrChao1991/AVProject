package com.cfox.screenrecorder.decode.parse;

public interface BaseParse {

    int getForbiddenZeroBit();
    int getNalRefIdc();
    int getNalUnitType();
    byte[] getBytes();
    int u(int bitIndex, byte[] frameByte);
    int Ue(byte[] pBuff);
}
