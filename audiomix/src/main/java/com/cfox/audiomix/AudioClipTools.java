package com.cfox.audiomix;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;


import com.cfox.audiomix.wavfile.WavFileReader;
import com.cfox.audiomix.wavfile.WavFileWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioClipTools {
    private static final String TAG = "AudioClipServer";

    public void clip(String inPath , String outPath , int startTime , int endTime) throws IOException {
        if (startTime >= endTime) {
            return;
        }

        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(inPath);
        int audioTrack = selectAudioTrack(mediaExtractor); // 查找文件中的音频轨道
        Log.d(TAG, "clip: audio track :" + audioTrack);
        if (audioTrack == -1) {
            return;
        }
        mediaExtractor.selectTrack(audioTrack); //  设置操作的轨道
        mediaExtractor.seekTo(startTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC); // seek 到指定位置
        MediaFormat audioFormat = mediaExtractor.getTrackFormat(audioTrack); // 获取轨道中音频信息

        int maxBufferSize;
        if (audioFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            // 获取音频信息中最大输入size
            maxBufferSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        } else {
            maxBufferSize = 100 * 1000;
        }

        // 申请指定大小的buffer ，后面解码使用
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);

        // 创建 media codec 解码器
        MediaCodec mediaCodec = MediaCodec.createDecoderByType(audioFormat.getString(MediaFormat.KEY_MIME));
        mediaCodec.configure(audioFormat, null, null, 0);
        mediaCodec.start();

        WavFileWriter wavFileWriter = new WavFileWriter();
        wavFileWriter.openFile(
                outPath,
                audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE),
                audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT),
                AudioFormat.ENCODING_PCM_16BIT);

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();


        while (true) {
            int dequeueInputIndex = mediaCodec.dequeueInputBuffer(100_000);
            if (dequeueInputIndex >=0) {
                long sampleTimeUs = mediaExtractor.getSampleTime(); // 获取时间戳
                if (sampleTimeUs == -1) {
                    break;
                } else if (sampleTimeUs < startTime)  {
                    // 时间小于截取开始时间，丢弃
                    mediaExtractor.advance();
                    continue; // 跳出本次循环
                } else if (sampleTimeUs > endTime) {
                    break;
                }
                // 下面是需要截取的数据
                // 把需要解码数据读到buffer 中 ， 返回是读取的长度
                info.size = mediaExtractor.readSampleData(buffer, 0);
                info.presentationTimeUs = sampleTimeUs; // 设置pts
                info.flags = mediaExtractor.getSampleFlags();

                byte[] content = new byte[buffer.remaining()];
                buffer.get(content);// 把数据copy 到content 中
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(dequeueInputIndex);
                inputBuffer.put(content);
                // 解码
                mediaCodec.queueInputBuffer(dequeueInputIndex, 0, info.size, info.presentationTimeUs, info.flags);
                mediaExtractor.advance();

            }

            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(info, 100_000);
            while (outputBufferIndex >=0) {
                ByteBuffer decodeOutputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex);
                byte[] content = new byte[decodeOutputBuffer.remaining()];
                decodeOutputBuffer.get(content);// 把数据copy 到content 中
                wavFileWriter.writeData(content, 0, content.length);
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(info,100_000 );
            }
        }

        wavFileWriter.closeFile();
        mediaExtractor.release();
        mediaCodec.stop();
        mediaCodec.release();

        WavFileReader wavFileReader = new WavFileReader();
        wavFileReader.openFile(outPath);
        wavFileReader.release();

        Log.d(TAG, "clip: end ---->>");
    }

    private int selectAudioTrack(MediaExtractor extractor) {
        int trackCount = extractor.getTrackCount();
        for (int i = 0 ; i < trackCount; i ++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime =   format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                return i;
            }
        }

        return -1;
    }

    private void byteToHex(byte[] array) {
        char[] HEX_CHAR_TABLE = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(HEX_CHAR_TABLE[(b & 0xf0) >> 4]);
            sb.append(HEX_CHAR_TABLE[b & 0x0f]);
        }
        Log.i(TAG, "writeContent: " + sb.toString());
    }

}
