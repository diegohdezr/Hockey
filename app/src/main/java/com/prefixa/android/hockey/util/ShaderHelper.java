package com.prefixa.android.hockey.util;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;
import android.util.Log;
/**
 * Created by Prefixa_01 on 02/03/2016.
 */
public class ShaderHelper {

    //IMPORTANT: VALIDATE ONLY IN DEVELOPMENT, STOP IT AFTER RELEASE
    private static final String TAG = "ShaderHelper";

    public static int complieVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader (String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode){
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0){
            if (LoggerConfig.ON){
                Log.w(TAG,"Could not create a new shader");
            }
            return 0;
        }
        //upload and compile the shader source to OpenGL
        glShaderSource(shaderObjectId, shaderCode);
        //compile the shader
        glCompileShader(shaderObjectId);
        //check if the shader compiled correctly
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        //get a log about the shader if available
        if (LoggerConfig.ON) {
            // Print the shader info log to the Android log output.
            Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
                    + glGetShaderInfoLog(shaderObjectId));
        }

        //verify the shader compile status.
        if(compileStatus[0] == 0){
            //if it failed, delete the shader object.
            glDeleteShader(shaderObjectId);

            if(LoggerConfig.ON){
                Log.w(TAG,"Compilation of the shader failed");
            }
            return 0;
        }

        return shaderObjectId;
    }
    /*links a vertex shader and a fragment shader dogether into an OpenGL
    * program, it returns the OpenGL program object id, or 0 if the linking failed*/
    public static int linkProgram(int vertexShaderID, int fragmentShaderID){
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0){
            if (LoggerConfig.ON){
                Log.w(TAG, "Could not create the new program");
            }
            return 0;
        }
        //the integer returned is our reference to the proigram object, and we'll get a return
        //value of 0 if the object creation failed.

        //attach the shaders to the program
        glAttachShader(programObjectId,vertexShaderID);
        glAttachShader(programObjectId,fragmentShaderID);

        //link the two shaders
        glLinkProgram(programObjectId);

        //check if the link was succesfull
        final int [] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        //check log for feedback on link status
        if(LoggerConfig.ON ){
            //print the program info log to the Android log output
            Log.v(TAG,"results of the linking program: \n"+ glGetProgramInfoLog(programObjectId));
        }

        //as with the shaders we have to delete the linked program if the link failed
        //so we check the link status, if it is 0 then we cant use the program object, so we
        //delete it and most important, return 0 to the caller code so it knows we failed.
        if (linkStatus[0] == 0){
            //if the link failed, delete the program object
            glDeleteProgram(programObjectId);
            if (LoggerConfig.ON){
                Log.w(TAG,"Linking program failed.");
            }
            return 0;
        }
        return programObjectId;
    }


    /*validates the OpenGL program. should only be called in development, delete for release*/
    public static boolean validateProgram(int programObjectId){

        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS,validateStatus,0);
        Log.w(TAG,"Results of validating the program: "+ validateStatus[0]
                + "\nLog: "+ glGetProgramInfoLog(programObjectId));

        return validateStatus[0]!=0;
    }
}
