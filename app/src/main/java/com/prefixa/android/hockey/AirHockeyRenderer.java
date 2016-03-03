package com.prefixa.android.hockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.prefixa.android.hockey.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Prefixa_01 on 01/03/2016.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPRONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private final Context context;

    public AirHockeyRenderer(Context context){
        this.context = context;
        float[] tableVertices = {
                //triangle 1
                0f,0f,
                9f, 14f,
                0f, 14f,
                //triangle2
                0f,0f,
                9f,0f,
                9f,14f
                ,
                //line 1
                0f, 7f,
                9f, 7f,
                //mallets
                4.5f, 2f,
                4.5f, 12f
        };
        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1.0f,1.0f,0.0f,0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_fragment_shader);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
    }
}
