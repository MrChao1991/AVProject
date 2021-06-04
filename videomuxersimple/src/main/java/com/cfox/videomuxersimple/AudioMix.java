package com.cfox.videomuxersimple;

public class AudioMix {


    public static byte[] mix(byte[] buffer1, int vol1, byte[] buffer2, int vol2) {

        if (buffer1.length !=buffer2.length) {
            throw new RuntimeException("");
        }
        short temp2, temp1;//   两个short变量相加 会大于short   声音
        int  temp;
        byte[] mixBuffer = new byte[buffer1.length];
        for (int i = 0; i < buffer2.length; i += 2) {
//                    或运算
            // 一个声音 2 个byte 16 位， 高 8 位和 低 8 位
            temp1 = (short) ((buffer1[i] & 0xff) | (buffer1[i + 1] & 0xff) << 8);// 低8位 或上 高8位
            temp2 = (short) ((buffer2[i] & 0xff) | (buffer2[i + 1] & 0xff) << 8);
            temp = (int) (temp1 * vol1 + temp2 * vol2);//音乐和 视频声音 各占一半
            if (temp > 32767) { // 不能大于声音采样位数的最大值和小于最小值
                temp = 32767;
            }else if (temp < -32768) {
                temp = -32768;
            }
            // 取合成后的低8位 ， 高8位
            mixBuffer[i] = (byte) (temp & 0xFF);
            mixBuffer[i + 1] = (byte) ((temp >>> 8) & 0xFF);
        }

        return mixBuffer;
    }
}
