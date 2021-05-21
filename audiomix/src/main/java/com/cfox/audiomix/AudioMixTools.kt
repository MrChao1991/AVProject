package com.cfox.audiomix

import com.cfox.audiomix.wavfile.WavFileReader

class AudioMixTools {



    fun mixAudio(audioPath1: String , audioPath2 : String, outPath: String, audioVolume1: Int, audioVolume2: Int) {

        val audio1 = WavFileReader()
        audio1.openFile(audioPath1)

        val audio2 = WavFileReader()
        audio2.openFile(audioPath1)



    }



}