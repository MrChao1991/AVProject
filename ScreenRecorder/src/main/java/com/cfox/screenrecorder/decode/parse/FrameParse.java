package com.cfox.screenrecorder.decode.parse;

import com.cfox.screenrecorder.decode.frame.Frame;

public interface FrameParse<T extends Frame> {

    T parseFrame();
}
