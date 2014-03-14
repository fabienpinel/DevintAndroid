package com.polytech.devintandroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;

public class Draw extends GLSurfaceView implements Renderer {
	
		
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
		// initialize a triangle
	    Triangle triangle = new Triangle();

	}

	public void onDrawFrame(GL10 gl10) {

		// just clear the screen and depth buffer.

		gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

	}

	public void onSurfaceChanged(GL10 gl10, int i, int i1) {

		gl10.glViewport(0, 0, i, i1);

		/*
		 * 
		 * Set our projection matrix. This doesn't have to be done each time we
		 * 
		 * draw, but usually a new projection needs to be set when the viewport
		 * 
		 * is resized.
		 */

		float ratio = (float) i / i1;

		gl10.glMatrixMode(GL10.GL_PROJECTION);

		gl10.glLoadIdentity();

		gl10.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

	}
	
}
