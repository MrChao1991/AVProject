package com.cfox.vsencoder

class VSFormat(val width: Int, val height: Int) {
    var fps = 30
    var bitrate = (width * height * 2.5).toInt()
    var level_idc : LEVEL_IDC = LEVEL_IDC.HD_720P_31
    var b_frame = 0
    var i_frame_interval = 1
    var profile_idc :PROFILE_IDC = PROFILE_IDC.BASELINE
}

sealed class LEVEL_IDC(val level_Idc: Int) {
    object QCIF_10 :LEVEL_IDC(10)
    object CIF_11 :LEVEL_IDC(11)
    object CIF_12 :LEVEL_IDC(12)
    object CIF_13 :LEVEL_IDC(13)
    object CIF_20 :LEVEL_IDC(20)
    object HHR_21 :LEVEL_IDC(21)
    object SD_4CIF_22 :LEVEL_IDC(22)
    object SD_4CIF_30 :LEVEL_IDC(30)
    object HD_720P_31 :LEVEL_IDC(31)
    object SXGA_32 :LEVEL_IDC(32)
    object K2_K1_40 :LEVEL_IDC(40)
    object K2_K1_41 :LEVEL_IDC(41)
    object K2_K1_42 :LEVEL_IDC(42)
    object PX_3672_1536_50 :LEVEL_IDC(50)
    object PX_4096_2304_51 :LEVEL_IDC(51)

}

sealed class PROFILE_IDC(val profile_idc : String) {
    object BASELINE : PROFILE_IDC("baseline")
    object MAIN : PROFILE_IDC("main")
    object HIGH : PROFILE_IDC("high")
    object HIGH_10 : PROFILE_IDC("high10")
    object HIGH_422 : PROFILE_IDC("high422")
    object HIGH_444 : PROFILE_IDC("high444")
}