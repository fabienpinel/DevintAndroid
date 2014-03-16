package com.polytech.devintandroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.AttributeSet;

public class Draw extends GLSurfaceView implements Renderer {
	private final String vertexShaderCode =
		    // This matrix member variable provides a hook to manipulate
		    // the coordinates of objects that use this vertex shader.
		    "uniform mat4 uMVPMatrix;   \n" +

		    "attribute vec4 vPosition;  \n" +
		    "void main(){               \n" +
		    // The matrix must be included as part of gl_Position
		    // Note that the uMVPMatrix factor *must be first* in order
		    // for the matrix multiplication product to be correct.
		    " gl_Position = uMVPMatrix * vPosition; \n" +

		    "}  \n";
		
		public Draw(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

		public static int loadShader(int type, String shaderCode){

		    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		    int shader = GLES20.glCreateShader(type);

		    // add the source code to the shader and compile it
		    GLES20.glShaderSource(shader, shaderCode);
		    GLES20.glCompileShader(shader);

		    return shader;
		}
	
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

		gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl10.glClearColor(0.0f, 0.0f, 0.0f, 1);

		// Enable Flat Shading.

		gl10.glShadeModel(GL10.GL_FLAT);

		// we don't need to worry about depth testing!

		gl10.glDisable(GL10.GL_DEPTH_TEST);

		// Set OpenGL to optimise for 2D Textures

		gl10.glEnable(GL10.GL_TEXTURE_2D);

		// Disable 3D specific features.

		// but you'll have to figure that out for yourself.

		gl10.glDisable(GL10.GL_DITHER);

		gl10.glDisable(GL10.GL_LIGHTING);

		gl10.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE); // Initial clear of the screen.

		gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		 Object muMVPMatrixHandle = GLES20.glGetUniformLocation(0, "uMVPMatrix");
		 float[] mVMatrix = null;
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		// initialize a triangle
	    Triangle triangle = new Triangle();

	}


	public void onDrawFrame(GL10 gl) {
	    // Set GL_MODELVIEW transformation mode
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glLoadIdentity();                      // reset the matrix to its default state

	    // When using GL_MODELVIEW, you must set the camera view
	    GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	    gl.glViewport(0, 0, width, height);

	    // make adjustments for screen ratio
	    float ratio = (float) width / height;
	    gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
	    gl.glLoadIdentity();                        // reset the matrix to its default state
	    gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix
	}
	
}
