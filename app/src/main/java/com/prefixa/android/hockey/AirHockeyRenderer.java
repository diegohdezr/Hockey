package com.prefixa.android.hockey;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.prefixa.android.hockey.util.LoggerConfig;
import com.prefixa.android.hockey.util.ShaderHelper;
import com.prefixa.android.hockey.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Prefixa_01 on 01/03/2016.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPRONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private final Context context;

    private int program;                                    //OpenGL program ID to link the shaders
    private static final String U_COLOR = "u_Color";        //constant to refer to our color uniform
    private int uColorLocation;                             //variable to hold the uniform pos in
                                                            // the OpenGL program, Uniform locations
                                                            //don't get specified beforehand so we
                                                            //we need this variable to query its pos later
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;                          //same stuff, like in uniform we store the position
                                                            //of our attributes here

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

        int vertexShader = ShaderHelper.complieVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        //validate the program to know useful runtime data about the program
        if (LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }
        //now we use the program which has the shaders linked with our instance of OpenGL
        glUseProgram(program);

        //query and store the uniform location so that its value can be updated
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        //query and store the atribute location so it can be updated
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        //find the data for our attribute position
        vertexData.position(0);
        //passing incorrect arguments to this function can lead to strange results or even crash the app
        //so basically debug here if the error is not clear!
        glVertexAttribPointer(aPositionLocation,POSITION_COMPRONENT_COUNT, GL_FLOAT,false,0, vertexData);

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
