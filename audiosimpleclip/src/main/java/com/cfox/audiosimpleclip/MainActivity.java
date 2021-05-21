package com.cfox.audiosimpleclip;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.cfox.espermission.EsPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        new EsPermissions(this).isGranted(permissions);

    }


    private void copyAssets(String assetsName, String path) throws IOException {
        AssetFileDescriptor assetFileDescriptor = getAssets().openFd(assetsName);
        FileChannel from = new FileInputStream(assetFileDescriptor.getFileDescriptor()).getChannel();
        FileChannel to = new FileOutputStream(path).getChannel();
        from.transferTo(assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength(), to);
    }

    public void onStartClip(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String aacPath = new File(Environment.getExternalStorageDirectory(), "music.mp3").getAbsolutePath();
                final String outPath = new File(Environment.getExternalStorageDirectory(), "out111.mp3").getAbsolutePath();
                try {
                    copyAssets("music.mp3", aacPath);
                    Log.d(TAG, "run: file copy end ===>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    new AudioClipTools().clip(aacPath,outPath,10*1000*1000,15*1000*1000);
                    Log.d(TAG, "run: clip end ===?");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}