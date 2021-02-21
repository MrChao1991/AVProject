package com.cfox.mediacodecdemo;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 解码播放一个h264 视频文件
 */
public class H264Player implements Runnable{

    private String mFilePath;

    private MediaCodec mMediaCodec;
    public H264Player(String path, Surface surface) {
        this.mFilePath = path;


        try {
            mMediaCodec = MediaCodec.createDecoderByType("video/avc");

            MediaFormat format = MediaFormat.createVideoFormat("video/avc", 368, 384);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mMediaCodec.configure(format, surface, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        mMediaCodec.start();
        new Thread(this).start();
    }

    @Override
    public void run() {

        decodeH264();

    }

    private void decodeH264() {
        byte[] bytes = null;
        try {
            bytes = getBytes(mFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int startIndex = 0;
        int totalSize = bytes.length;

        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();



        while (true) {
            if (totalSize == 0 || startIndex >= totalSize) {
                break;
            }

            // 找到下一帧的开始位置
            int nextFrameStart = findByFrame(bytes, startIndex + 2, totalSize);

            // 获取一个输入buffer
            int inIndex = mMediaCodec.dequeueInputBuffer(10_000);
            if (inIndex >= 0) {
                ByteBuffer byteBuffer = inputBuffers[inIndex];
                byteBuffer.clear();
                byteBuffer.put(bytes, startIndex, nextFrameStart - startIndex);

                mMediaCodec.queueInputBuffer(inIndex, 0 , nextFrameStart - startIndex, 0 , 0);
                startIndex = nextFrameStart;
            }  else {
                continue;
            }
            MediaCodec.BufferInfo outBufferInfo = new MediaCodec.BufferInfo();
            int outIndex = mMediaCodec.dequeueOutputBuffer(outBufferInfo, 10_1000);
            if (outIndex >= 0) {
                mMediaCodec.releaseOutputBuffer(outIndex, true);
            }
        }
    }

    // 每一帧图像数据的标识符是 0x00 00 00 01
    private int findByFrame( byte[] bytes, int start, int totalSize) {
        for (int i = start; i < totalSize-4; i++) {
            if (bytes[i] == 0x00 && bytes[i + 1] == 0x00 && bytes[i + 2] == 0x00 && bytes[i + 3] == 0x01) {
                return i;
            }
        }
        return -1;
    }

    public  byte[] getBytes(String path) throws IOException {
        InputStream is =   new DataInputStream(new FileInputStream(new File(path)));
        int len;
        int size = 1024;
        byte[] buf;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1)
            bos.write(buf, 0, len);
        buf = bos.toByteArray();
        return buf;
    }
}
