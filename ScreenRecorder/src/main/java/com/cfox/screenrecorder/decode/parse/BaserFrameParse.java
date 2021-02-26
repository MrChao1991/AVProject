package com.cfox.screenrecorder.decode.parse;

public class BaserFrameParse implements BaseParse {

    private int nStartBit = 0;
    private byte[] bytes;

    private int mForbiddenZeroBit = -1;
    private int mNalRefIdc = -1;
    private int mNalUnitType = -1;

    public void parseBytes(byte[] bytes) {
        this.bytes = bytes;
        nStartBit = 4 * 8;
        mForbiddenZeroBit = u(1, bytes);
        mNalRefIdc = u(2, bytes);
        mNalUnitType = u(5, bytes);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public int getForbiddenZeroBit() {
        return mForbiddenZeroBit;
    }

    @Override
    public int getNalRefIdc() {
        return mNalRefIdc;
    }

    @Override
    public int getNalUnitType() {
        return mNalUnitType;
    }


    int u(int bitIndex, byte[] frameByte) {
        int dwRet = 0;
        for (int i = 0; i < bitIndex; i++) {
            dwRet <<= 1;
            if ((frameByte[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
                dwRet += 1;
            }
            nStartBit++;
        }
        return dwRet;
    }



    int Ue(byte[] pBuff) {
        int nZeroNum = 0;
        while (nStartBit < pBuff.length * 8) {
            if ((pBuff[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
                break;
            }
            nZeroNum++;
            nStartBit++;
        }
        nStartBit++;

        int dwRet = 0;
        for (int i = 0; i < nZeroNum; i++) {
            dwRet <<= 1;
            if ((pBuff[nStartBit / 8] & (0x80 >> (nStartBit % 8))) != 0) {
                dwRet += 1;
            }
            nStartBit++;
        }
        return (1 << nZeroNum) - 1 + dwRet;
    }


}
