package com.cfox.audiomix.wavfile;



import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavFileReader {
    private static final String TAG = "WavFileReader";
    private RandomAccessFile mDataStream;
    private WavFileHeader mWavFileHeader;

    public boolean openFile(String filepath) throws IOException {
        if (mDataStream != null) {
            closeFile();
        }
        mDataStream = new RandomAccessFile(new File(filepath), "rw");
        return readHeader();
    }

    public void closeFile() throws IOException {
        if (mDataStream != null) {
            mDataStream.close();
            mDataStream = null;
        }
    }

    public WavFileHeader getWavFileHeader() {
        return mWavFileHeader;
    }

    public void forward(long length) throws IOException {
        if (mDataStream != null) {
            long point = mDataStream.getFilePointer();
            mDataStream.seek(point + length);
        }
    }

    public void back(long length) throws IOException {
        if (mDataStream != null) {
            long point = mDataStream.getFilePointer();
            mDataStream.seek(point - length);
        }
    }

    public void seek(long seekSize) throws IOException {
        if (mDataStream != null) {
            mDataStream.seek(seekSize);
        }
    }

    public long getFilePointer() throws IOException {
        if (mDataStream != null) {
            return mDataStream.getFilePointer();
        }
        return 0;
    }

    public int readData(byte[] buffer, int offset, int count) {
        if (mDataStream == null || mWavFileHeader == null) {
            return -1;
        }

        try {
            return mDataStream.read(buffer, offset, count);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private boolean readHeader() {
        if (mDataStream == null) {
            return false;
        }

        WavFileHeader header = new WavFileHeader();

        byte[] intValue = new byte[4];
        byte[] shortValue = new byte[2];

        try {
            header.mChunkID = "" + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte();
            Log.d(TAG, "Read file chunkID:" + header.mChunkID);

            mDataStream.read(intValue);
            header.mChunkSize = byteArrayToInt(intValue);
            Log.d(TAG, "Read file chunkSize:" + header.mChunkSize);

            header.mFormat = "" + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte();
            Log.d(TAG, "Read file format:" + header.mFormat);

            header.mSubChunk1ID = "" + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte();
            Log.d(TAG, "Read fmt chunkID:" + header.mSubChunk1ID);

            mDataStream.read(intValue);
            header.mSubChunk1Size = byteArrayToInt(intValue);
            Log.d(TAG, "Read fmt chunkSize:" + header.mSubChunk1Size);

            mDataStream.read(shortValue);
            header.mAudioFormat = byteArrayToShort(shortValue);
            Log.d(TAG, "Read audioFormat:" + header.mAudioFormat);

            mDataStream.read(shortValue);
            header.mNumChannel = byteArrayToShort(shortValue);
            Log.d(TAG, "Read channel number:" + header.mNumChannel);

            mDataStream.read(intValue);
            header.mSampleRate = byteArrayToInt(intValue);
            Log.d(TAG, "Read samplerate:" + header.mSampleRate);

            mDataStream.read(intValue);
            header.mByteRate = byteArrayToInt(intValue);
            Log.d(TAG, "Read byterate:" + header.mByteRate);

            mDataStream.read(shortValue);
            header.mBlockAlign = byteArrayToShort(shortValue);
            Log.d(TAG, "Read blockalign:" + header.mBlockAlign);

            mDataStream.read(shortValue);
            header.mBitsPerSample = byteArrayToShort(shortValue);
            Log.d(TAG, "Read bitspersample:" + header.mBitsPerSample);

            header.mSubChunk2ID = "" + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte() + (char) mDataStream.readByte();
            Log.d(TAG, "Read data chunkID:" + header.mSubChunk2ID);

            mDataStream.read(intValue);
            header.mSubChunk2Size = byteArrayToInt(intValue);
            Log.d(TAG, "Read data chunkSize:" + header.mSubChunk2Size);

            Log.d(TAG, "Read wav file success !");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        mWavFileHeader = header;

        return true;
    }

    private static short byteArrayToShort(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public void release() {
        if (mDataStream == null) {
            return;
        }
        try {
            mDataStream.close();
            mDataStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
