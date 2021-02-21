package com.cfox.mediacodecdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Environment;
import android.view.Surface;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 获取h264 中一帧图片，保存到sdcard
 */
public class GetH264Image implements Runnable{

    private String mFilePath;

    private MediaCodec mMediaCodec;
    public GetH264Image(String path, Surface surface) {
        this.mFilePath = path;


        try {
            mMediaCodec = MediaCodec.createDecoderByType("video/avc");

            MediaFormat format = MediaFormat.createVideoFormat("video/avc", 368, 384);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mMediaCodec.configure(format, null, null, 0);
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

    int frameCount = 0;

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
                // 获取解码数据buffer
                ByteBuffer outByteBuffer = mMediaCodec.getOutputBuffer(outIndex);
                // 获取到这一帧数据的偏移量，设置开始读取这一帧数据的位置
                outByteBuffer.position(outBufferInfo.offset);
                // 设置读取数据长度
                outByteBuffer.limit(outBufferInfo.offset + outBufferInfo.size);

                byte[] outByte = new byte[outByteBuffer.remaining()];
                // 将数据写入byte 数组
                outByteBuffer.get(outByte);

                if (frameCount % 5 == 0) {
                    saveImageToSDCard(outByte);

                }

                frameCount++;
                mMediaCodec.releaseOutputBuffer(outIndex, false);
            }
        }
    }

    private void saveImageToSDCard(byte[] outByte) {
        YuvImage yuvImage = new YuvImage(outByte, ImageFormat.NV21, 368, 384, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0 , 368, 384), 100, baos);

        byte[] imageData = baos.toByteArray(); // rgb
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0 , imageData.length);
        if (bitmap != null) {
            try {
                File myCaptureFile = new File(Environment.getExternalStorageDirectory(), "f_"+ frameCount +"_img.png");
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                bos.close();
            } catch ( Exception e) {
                e.printStackTrace();
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
