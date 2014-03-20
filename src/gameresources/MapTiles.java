package gameresources;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class MapTiles {

	int mapPositionsBufferIdx;
	int mapTexCoordsBufferIdx;
	int wireframePositions;
	
	float baseVertices[] = { 
			-0.0f, 1.0f, 0.0f,
			-0.0f, -0.0f, 0.0f,
			1.0f, -0.0f, 0.0f,
			
			-0.0f, 1.0f, 0.0f,
			1.0f, -0.0f, 0.0f,
			1.0f, 1.0f, 0.0f };
	
	float vertices[];
	float uvCords[];
	float wireVertices[];

	int mapWidth, mapHeight;
	int buffers[] = new int[2];
	int wirebuffers[] = new int[1];
	FloatBuffer vertexBuffer, wireBuffer, texBuffer;
	ShortBuffer drawlistBuffer;
	int texIndex= 0;

	public MapTiles(int width, int height) {
		
		mapWidth = width;
		mapHeight = height;
		// buffer for cube vertices
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * (width*height*6*3));
		buffer.order(ByteOrder.nativeOrder());
		
		//create vertex buffer to hold entire map
		vertices =  new float[width*height*6*3];
		
		//create vertex locations
		createVertices();
				
		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// buffer for wire vertices
		ByteBuffer wbuffer = ByteBuffer.allocateDirect(((width*2) + (height*2))*3*4);
		wbuffer.order(ByteOrder.nativeOrder());
		
		//create wireframe vertices
		wireVertices = new float[((width*2) + (height*2))*3];
		createWireVertices();
		
		wireBuffer = wbuffer.asFloatBuffer();
		wireBuffer.put(wireVertices);
		wireBuffer.position(0);
		
		// buffer for texture cords
		ByteBuffer tBuffer = ByteBuffer.allocateDirect(4 * (width*height*6*2));
		tBuffer.order(ByteOrder.nativeOrder());

		//create tex buffer for uvCords
		uvCords = new float[width*height*6*2];
		
		texBuffer = tBuffer.asFloatBuffer();
		
	}

	private void createWireVertices() {
		
		int index = 0;
		for(int x = 0; x < mapWidth; x++)
		{
			
			//top of line
			wireVertices[index] = baseVertices[0]+x;
			index++;
			wireVertices[index] = baseVertices[1];
			index++;
			wireVertices[index] = baseVertices[2];
			index++;
			
			//bottom of line
			wireVertices[index] = baseVertices[0]+x;
			index++;
			wireVertices[index] = baseVertices[1]-mapHeight;
			index++;
			wireVertices[index] = baseVertices[2];
			index++;
		}
		
		for(int x = 0; x < mapHeight;x++)
		{
			//left of line
			wireVertices[index] = baseVertices[0];
			index++;
			wireVertices[index] = baseVertices[1]-x;
			index++;
			wireVertices[index] = baseVertices[2];
			index++;
			
			//right of line
			wireVertices[index] = baseVertices[0]+mapWidth; 
			index++;
			wireVertices[index] = baseVertices[1]-x;
			index++;
			wireVertices[index] = baseVertices[2];
			index++;
		}
		
	}

	private void createVertices() {

		int index = 0;
		int rowLocation = 0;
		int heightLocation= 0;
		//create all vertices
		for(int x=1; x <= mapWidth*mapHeight; x++)
		{

			//triangle one
			//top left corner
			vertices[index] = (baseVertices[0])+rowLocation;
			index++;
			vertices[index] = (baseVertices[1])-heightLocation;
			index++;
			vertices[index] = baseVertices[2];
			index++;
			
			//bottom left corner
			vertices[index] =  (baseVertices[3])+rowLocation;
			index++;
			vertices[index] = (baseVertices[4] )-heightLocation;
			index++;
			vertices[index] = baseVertices[5];
			index++;
			
			//bottom right corner
			vertices[index] =  (baseVertices[6])+rowLocation;
			index++;
			vertices[index] = (baseVertices[7])-heightLocation;
			index++;
			vertices[index] = baseVertices[8];
			index++;
			
			//triangle two
			//top left corner
			vertices[index] = (baseVertices[9])+rowLocation;
			index++;
			vertices[index] = (baseVertices[10])-heightLocation;
			index++;
			vertices[index] = baseVertices[11];
			index++;
			
			//bottom right corner
			vertices[index] =  (baseVertices[12])+rowLocation;
			index++;
			vertices[index] = (baseVertices[13])-heightLocation;
			index++;
			vertices[index] = baseVertices[14];
			index++;
			
			//top right corner
			vertices[index] =  (baseVertices[15])+rowLocation;
			index++;
			vertices[index] = (baseVertices[16])-heightLocation;
			index++;
			vertices[index] = baseVertices[17];
			index++;
			
			rowLocation++;
			
			if((rowLocation) == 30)
			{
				rowLocation =0;
				heightLocation++;
			}
		}
	}

	public void setUVCords(float[] cords) {

		for(int x = 0; x < cords.length;x++)
		{
			uvCords[texIndex] = cords[x];
			texIndex++;
		}
		if(texIndex == ((mapWidth*mapHeight*6*2)))
		{
			texBuffer.put(uvCords);
			texBuffer.position(0);
			
		}
	}

	public void createVBO()
	{
		//create VBO map
		GLES20.glGenBuffers(2, buffers, 0);
				
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texBuffer.capacity() * 4, texBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		mapPositionsBufferIdx = buffers[0];
		mapTexCoordsBufferIdx = buffers[1];
		vertexBuffer = null;
		texBuffer = null;
		
		//create VBO wireframe
		GLES20.glGenBuffers(1, wirebuffers, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, wirebuffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, wireBuffer.capacity() * 4, wireBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		wireframePositions = wirebuffers[0];
		wireBuffer = null;
	}
	
	public void draw(float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle,int positionHandle,int mTextureCoordinateHandle, int mTextureDataHandle) {
		
		// position
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mapPositionsBufferIdx);
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	0, 0);

		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
		
		// texture
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mapTexCoordsBufferIdx);
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2,
				GLES20.GL_FLOAT, false, 0, 0);

		// send uniforms
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length/3);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

	}
	
	public void drawWireFrame(float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle,int positionHandle)
	{
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, wireframePositions);
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	0, 0);
		
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
		
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, wireVertices.length/3);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
}
