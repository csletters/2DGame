package gameresources;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class PolygonRenderer {
	
	public float vertices[];
	FloatBuffer vertexBuffer;
	int buffers[] = new int[1];
	int positionsBufferIdx;
	final int[] buffersToDelete = new int[] { positionsBufferIdx };
	public PolygonRenderer()
	{
		
	}
	
	public void loadPolygon(float[] vert)
	{
		vertices = vert;
		
		// buffer for cube vertices
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * vertices.length);
		buffer.order(ByteOrder.nativeOrder());

		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

	}
	
	public void draw(float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle,int positionHandle)
	{
		//create VBO here because I can only create VBO's in an opengl context basically android not being cool
		GLES20.glGenBuffers(1, buffers, 0);
				
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		positionsBufferIdx = buffers[0];
		
		// position
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, positionsBufferIdx);
		
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	0, 0);
		
		// send uniforms
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
				
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length/3);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		GLES20.glDeleteBuffers(1, buffersToDelete, 0);
	}

}
