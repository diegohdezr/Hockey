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
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
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

    public AirHockeyRenderer(){
        //this default constructor should not be called
        context = null;
        vertexData = null;
    }

    public AirHockeyRenderer(Context context){
        this.context = context;
       /* float[] tableVertices = {
                // Triangle 1
                -0.5f, -0.5f,
                0.5f,  0.5f,
                -0.5f,  0.5f,

                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f,  0.5f,

               // Line 1
                -0.5f, 0f,
                0.5f, 0f,

                // Mallets
                0f, -0.25f,
                0f,  0.25f,

                //Puck
                0f,0f,

                //marginleft
                -0.505f,+0.505f,
                -0.505f,-0.505f,
                //margin right
                0.505f,0.505f,
                0.505f,-0.505f,
                //margin top
                -0.505f,+0.505f,
                0.505f,+0.505f,
                //margin bottom
                -0.505f,-0.505f,
                0.505f,-0.505f
        };*/
        float[] tableVerticesWithTriangles = {

                //triangle fan, basically draw the poligon figure with triangles one next to the other just like in a circunscript figure
                // Order of coordinates: X, Y, R, G, B

                // Triangle Fan
                0f,    0f,   1f,   1f,   1f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets
                0f, -0.25f, 0f, 0f, 1f,
                0f,  0.25f, 1f, 0f, 0f

        };
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f,0.0f,0.0f,0.0f);
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
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        //draw the triangles
        /*glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);*/
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN,0,6);
        //draw the dividing line in the table
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);
        //draw the mallets
        glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
        glDrawArrays(GL_POINTS,8,1);
        glUniform4f(uColorLocation, 1.0f,0.0f,0.0f,1.0f);
        glDrawArrays(GL_POINTS,9,1);
       /* //draw a Puck
        glUniform4f(uColorLocation, 0.0f,0.0f,0.0f,1.0f);
        glDrawArrays(GL_POINTS,10,1);
        //draw margin left
        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 11, 2);
        //draw margin right
        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 13, 2);
        //draw margin top
        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 15, 2);
        //drag margin bottom
        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 17, 2);*/
    }
}
