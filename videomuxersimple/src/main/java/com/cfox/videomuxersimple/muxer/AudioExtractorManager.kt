package com.cfox.videomuxersimple.muxer

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Environment
import android.util.Log
import com.cfox.videomuxersimple.AudioMix
import com.cfox.videomuxersimple.wavfile.WavFileWriter
import java.io.File
import java.nio.ByteBuffer

class AudioExtractorManager (val muxer: AVMuxer, baseAudioMuxerInfo: AudioMuxerInfo,  audioMuxerInfos : MutableList<AudioMuxerInfo> ) : Runnable {


    companion object {
        private const val TAG = "AudioExtractorManager"
    }

    private val extractorList = mutableListOf<AudioExtractor>()
    private var baseExtractor : AudioExtractor  = AudioExtractor(baseAudioMuxerInfo)
    private val trackIndex : Int

    private val encoder : MediaCodec
    private val format : MediaFormat
    private val maxBufferSize : Int
    private val buffer : ByteBuffer
    private val info : MediaCodec.BufferInfo
    private val wavFileWriter =  WavFileWriter()

    init {
        audioMuxerInfos.forEach {
            extractorList.add(AudioExtractor(it))
        }

        format = baseExtractor.getFormat()

        if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            maxBufferSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
        } else {
            maxBufferSize = 100 * 1000
        }
        trackIndex = muxer.addTrack(format)

        buffer = ByteBuffer.allocateDirect(maxBufferSize)
        info = MediaCodec.BufferInfo()
        val encoderFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 2)

        // 比特率
        encoderFormat.setInteger(MediaFormat.KEY_BIT_RATE, format.getInteger(MediaFormat.KEY_BIT_RATE))
        // 编码等级
        encoderFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        encoderFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize)

        encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
        encoder.configure(encoderFormat, null, null , MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    fun start() {
        Log.d(TAG, "start:  ---->>>")
        wavFileWriter.openFile(
                File(Environment.getExternalStorageDirectory(), "mix_music.mp3").absolutePath,
                44100,
                2,
                AudioFormat.ENCODING_PCM_16BIT)
        encoder.start()
        Thread(this).start()
    }

    override fun run() {
        while (true) {
            val audioData = baseExtractor.readAudioData()
            if (audioData is Data.AudioData) {
                var byteArray : ByteArray  = audioData.buffer
                extractorList.forEach {
                    val mixAudioData = it.readAudioDataBytes(audioData.info.presentationTimeUs, byteArray.size)
                    if (mixAudioData.isNotEmpty()) {
                        Log.d(TAG, "run: ===> size1:${byteArray.size}   size2:${mixAudioData.size}")
                        byteArray = AudioMix.mix(byteArray, 100,  mixAudioData ,100)
                    }
                }

                // 写入编码
                wavFileWriter.writeData(byteArray, 0 , byteArray.size)
                encoder(byteArray, audioData.info)
                Log.d(TAG, "run: encode ====》 end ")
            } else if (audioData is Data.EndData) {
                extractorList.forEach {
                    it.release()
                }
                baseExtractor.release()
                break
            }
        }
        wavFileWriter.closeFile()
        Log.d(TAG, "run: audio muxer release--->")
        muxer.release()
        Log.d(TAG, "run: auido manager run  end   ....")

    }


    private fun encoder(byteArray: ByteArray, info : MediaCodec.BufferInfo)  {

        while (true) {
            val inputBufferIndex = encoder.dequeueInputBuffer(100_000)
            if (inputBufferIndex >= 0) {
                val inputBuffer = encoder.getInputBuffer(inputBufferIndex)
                inputBuffer?.clear()
                inputBuffer?.put(byteArray)
                inputBuffer?.position(0)

                encoder.queueInputBuffer(inputBufferIndex, 0, info.size, info.presentationTimeUs, info.flags)

                break
            }
        }

        var outputIndex = encoder.dequeueOutputBuffer(info, 100_000)
        while (outputIndex >= 0) {
            val outputBuffer = encoder.getOutputBuffer(outputIndex)
            outputBuffer?.let {
                muxer.writeSimpleData(trackIndex, outputBuffer, info)
            }

            outputBuffer?.clear()
            encoder.releaseOutputBuffer(outputIndex, false)
            outputIndex = encoder.dequeueOutputBuffer(info, 100_000)
        }

    }

}