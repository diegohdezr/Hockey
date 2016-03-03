package com.prefixa.android.hockey;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class AirHockeyActivity extends AppCompatActivity {

    private GLSurfaceView myGLSurfaceView;
    private boolean renderSet = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myGLSurfaceView = new GLSurfaceView(this);

        //check for openGL compatibility
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationinfo = activityManager.getDeviceConfigurationInfo();

        final boolean suportsEs2 = configurationinfo.reqGlEsVersion >= 0x20000||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));

        if (suportsEs2){
            myGLSurfaceView.setEGLContextClientVersion(2);

            myGLSurfaceView.setRenderer(new AirHockeyRenderer(this));
            renderSet = false;
        }else{
            Toast.makeText(this,"OpenGL not suported",Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(myGLSurfaceView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (renderSet){
            myGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (renderSet){
            myGLSurfaceView.onResume();
        }
    }


}
