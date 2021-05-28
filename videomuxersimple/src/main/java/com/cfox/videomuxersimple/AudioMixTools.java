package com.cfox.videomuxersimple;

import android.media.AudioFormat;

import com.cfox.videomuxersimple.wavfile.WavFileHeader;
import com.cfox.videomuxersimple.wavfile.WavFileReader;
import com.cfox.videomuxersimple.wavfile.WavFileWriter;

import java.io.IOException;

public class AudioMixTools {

    private static float normalizeVolume(int volume) {
        return volume / 100f * 1;
    }

    public void mixAudio(String audioPath1 , String audioPath2,String outPath,int audioVolume1,int audioVolume2) throws IOException {
        float vol1 = normalizeVolume(audioVolume1);
        float vol2 = normalizeVolume(audioVolume2);
//一次读取多一点 2k
        byte[] buffer1 = new byte[2048];
        byte[] buffer2 = new byte[2048];
//        待输出数据
        byte[] buffer3 = new byte[2048];

        WavFileReader audio1 = new WavFileReader();
        audio1.openFile(audioPath1);

        WavFileReader audio2 = new WavFileReader();
        audio2.openFile(audioPath2);

        WavFileHeader wavFileHeader = audio1.getWavFileHeader();
        WavFileWriter outFileWriter = new WavFileWriter();
        outFileWriter.openFile(outPath, wavFileHeader.mSampleRate, wavFileHeader.mNumChannel, AudioFormat.ENCODING_PCM_16BIT);


//输出PCM 的

        short temp2, temp1;//   两个short变量相加 会大于short   声音
        int  temp;
        boolean end1 = false, end2 = false;


        while (!end1 || !end2) {

            if (!end1) {
                end1 = audio1.readData(buffer1, 0, buffer1.length) == -1;
            }

            if (!end2) {
                end2 = audio2.readData(buffer2, 0 , buffer2.length) == -1;
            }

            if (!end1 && !end2) {
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
                    buffer3[i] = (byte) (temp & 0xFF);
                    buffer3[i + 1] = (byte) ((temp >>> 8) & 0xFF);
                }
                outFileWriter.writeData(buffer3, 0 , buffer2.length);

            }
        }

        audio1.closeFile();
        audio2.closeFile();
        outFileWriter.closeFile();

    }

}
