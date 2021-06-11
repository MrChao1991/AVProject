package com.cfox.videomuxersimple.muxer

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import java.nio.ByteBuffer


class AudioExtractor(val audioMuxerInfo : AudioMuxerInfo ) {

    companion object {
        private const val TAG = "AudioExtractor"
    }

    private val extractor : MediaExtractor = MediaExtractor()
    private val trackIndex : Int
    private val format : MediaFormat
    private val maxBufferSize : Int
    private val buffer : ByteBuffer

    private val decode : MediaCodec
    private val info : MediaCodec.BufferInfo
    init {
        extractor.setDataSource(audioMuxerInfo.audioPath)
        trackIndex = getTrackIndex()
        extractor.selectTrack(trackIndex)
        format = extractor.getTrackFormat(trackIndex)
        extractor.seekTo(audioMuxerInfo.startTime , MediaExtractor.SEEK_TO_CLOSEST_SYNC)
        if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            maxBufferSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        } else {
            maxBufferSize = 10 * 1000 * 100
        }

        buffer = ByteBuffer.allocateDirect(maxBufferSize)
        info = MediaCodec.BufferInfo()

        decode = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME)!!)
        decode.configure(format, null, null, 0)
        decode.start()

    }


    fun getFormat() : MediaFormat {
        return format
    }

    private var bufferTmp : ByteArray = ByteArray(0)

    fun readAudioDataBytes(pts : Long, bufferSize : Int): ByteArray {
        val buffer = ByteArray(bufferSize)
        while (true) {
            if (bufferTmp.size >= bufferSize) {
                val cacheBuffer   = ByteArray(bufferTmp.size - bufferSize)

                bufferTmp.forEachIndexed { index, byte ->
                    if (index < bufferSize) {
                        buffer[index] = byte
                    } else {
                        cacheBuffer.let {
                            it[index - bufferSize] = byte
                        }
                    }
                }
                bufferTmp = cacheBuffer
                return buffer

            } else {
                val status = resetBuffer(pts , bufferSize)
                if (status < 0) {
                    return ByteArray(0)
                }

                if (status == 0) {
                    val backBuffer = bufferTmp.copyOf()
                    bufferTmp = ByteArray(0)
                    return backBuffer
                }
            }
        }
    }

    private fun resetBuffer(pts : Long, bufferSize: Int) : Int {
        while (true) {

            if (bufferTmp.size >= bufferSize) {
                return 1
            }
            
            val data = readAudioData(pts)
            if (data is Data.AudioData) {
                val cacheBufferSize = bufferTmp.size + data.buffer.size
                val cacheBuffer = ByteArray(cacheBufferSize)
                bufferTmp.forEachIndexed { index, byte ->
                    cacheBuffer[index] = byte
                }

                data.buffer.forEachIndexed { index, byte ->
                    cacheBuffer[bufferTmp.size + index] = byte
                }

                bufferTmp = cacheBuffer

            } else if (data is Data.EndData) {
                if (bufferTmp.size <= bufferSize) {
                    return 0
                } else {
                    return -1
                }
            } else {
                return -1
            }
        }

    }

    fun readAudioData(pts : Long = -1) : Data {

        if (pts >= 0 && pts <  audioMuxerInfo.startTime) {
            return Data.EmptyData()
        }

        while (true) {
            val sampleTime = extractor.sampleTime
            if (sampleTime < 0) {
                Log.d(TAG, "readAudioData: endData sampleTime:$sampleTime")
                return Data.EndData()
            }

            if (sampleTime > audioMuxerInfo.endTime) {
                extractor.advance()
                return Data.EndData()
            }

            if (sampleTime < audioMuxerInfo.startTime ) {
                Log.d(TAG, "readAudioData: EmptyData=====>")
                extractor.advance()
                return Data.EmptyData()
            }
            val inputIndex = decode.dequeueInputBuffer(100_1000)

            if (inputIndex > 0) {


                Log.d(TAG, "readAudioData:pts===>:$sampleTime   endTime :${audioMuxerInfo.endTime}  xTime:${audioMuxerInfo.endTime - sampleTime}")

                info.presentationTimeUs = sampleTime
                info.flags = extractor.sampleFlags
                info.size = extractor.readSampleData(buffer, 0)
                val byteArray = ByteArray(buffer.remaining())
                buffer[byteArray] // 把数据copy 到content 中

                val inputBuffer = decode.getInputBuffer(inputIndex)
                inputBuffer?.put(byteArray)

                decode.queueInputBuffer(inputIndex, 0 , info.size, info.presentationTimeUs, info.flags)
                extractor.advance()
            }

            val outputIndex = decode.dequeueOutputBuffer(info, 100_1000)

            while (outputIndex >=0) {
                val outBuffer = decode.getOutputBuffer(outputIndex)
                outBuffer?.let {
                    val outByteArray = ByteArray(outBuffer.remaining())
                    outBuffer.get(outByteArray)
                    decode.releaseOutputBuffer(outputIndex, false)
                    return Data.AudioData(outByteArray, info, audioMuxerInfo.vol)
                }
            }
        }
    }


    fun release() {
        extractor.release()
        decode.stop()
        decode.release()
    }


    private fun getTrackIndex(): Int {
        val trackCount = extractor.trackCount
        for (i in 0 until  trackCount) {
            val format = extractor.getTrackFormat(i)
            val mine = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mine.startsWith("audio/")) {
                return i
            }
        }

        return  -1
    }

}

class AudioMuxerInfo(val audioPath: String, val vol : Int,  val startTime: Long, val endTime: Long) {

}