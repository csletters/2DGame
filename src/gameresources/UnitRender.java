package gameresources;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;


public class UnitRender {

	public int currentFrame  = 0;
	public float[] texCords;
	float baseVertices[] = { 
			-0.0f, 1.0f, 0.0f,
			-0.0f, -0.0f, 0.0f,
			1.0f, -0.0f, 0.0f,
			
			-0.0f, 1.0f, 0.0f,
			1.0f, -0.0f, 0.0f,
			1.0f, 1.0f, 0.0f };
	
	FloatBuffer vertexBuffer, texBuffer;
	ByteBuffer tBuffer;
	int PositionsBufferIdx,TexCoordsBufferIdx;
	int buffers[] = new int[2];
	public int numFrames;
	
	public UnitRender()
	{
		// buffer for unit vertices
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * (3*6));
		buffer.order(ByteOrder.nativeOrder());
		
		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(baseVertices);
		vertexBuffer.position(0);
		
 
		//create VBO map
		GLES20.glGenBuffers(2, buffers, 0);
				
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
		
		PositionsBufferIdx = buffers[0];
		
	}
	
	public void draw(float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle,int positionHandle,int mTextureCoordinateHandle, int mTextureDataHandle) {
		
		// position
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, PositionsBufferIdx);
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	0, 0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
		
		// texture
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, TexCoordsBufferIdx);
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 2*4, currentFrame*12*4);

		// send uniforms
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, baseVertices.length/3);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
	}
	
}
