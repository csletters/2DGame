package gameresources;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.opengl.GLES20;

public class TestUnit extends UnitRender  {

	public TestUnit()
	{
		numFrames = 4;
		texCords = new float[48];
		//frame 1
		
		//top left vertex
		texCords[0] = 0.0f;
		texCords[1] = 0.0f;
				
		//bottom left vertex
		texCords[2] = 0.0f;
		texCords[3] = 1.0f;
				
		//bottom right vertex
		texCords[4] = 0.2153f;
		texCords[5] = 1.0f;
		
		//top left vertex
		texCords[6] = 0.0f;
		texCords[7] = 0.0f;
				
		//bottom right vertex
		texCords[8] = 0.2153f;
		texCords[9] = 1.0f;
				
		//top right vertex
		texCords[10] = 0.2153f;
		texCords[11] = 0.0f;
		
		//frame 2
		//top left vertex
		texCords[12] = 0.2615f;
		texCords[13] = 0.0f;
						
		//bottom left vertex
		texCords[14] = 0.2615f;
		texCords[15] = 1.0f;
						
		//bottom right vertex
		texCords[16] = 0.4769f;
		texCords[17] = 1.0f;
				
		//top left vertex
		texCords[18] = 0.2615f;
		texCords[19] = 0.0f;
						
		//bottom right vertex
		texCords[20] = 0.4769f;
		texCords[21] = 1.0f;
						
		//top right vertex
		texCords[22] = 0.4769f;
		texCords[23] = 0.0f;
		
		//frame 3
		//top left vertex
		texCords[24] = 0.52307f;
		texCords[25] = 0.0f;
						
		//bottom left vertex
		texCords[26] = 0.52307f;
		texCords[27] = 1.0f;
						
		//bottom right vertex
		texCords[28] = 0.7384f;
		texCords[29] = 1.0f;
				
		//top left vertex
		texCords[30] = 0.52307f;
		texCords[31] = 0.0f;
						
		//bottom right vertex
		texCords[32] = 0.7384f;
		texCords[33] = 1.0f;
						
		//top right vertex
		texCords[34] = 0.7384f;
		texCords[35] = 0.0f;
		
		//frame 4
		//top left vertex
		texCords[36] = 0.7692f;
		texCords[37] = 0.0f;
						
		//bottom left vertex
		texCords[38] = 0.7692f;
		texCords[39] = 1.0f;
						
		//bottom right vertex
		texCords[40] = 1.0f;
		texCords[41] = 1.0f;
				
		//top left vertex
		texCords[42] = 0.7692f;
		texCords[43] = 0.0f;
						
		//bottom right vertex
		texCords[44] = 1.0f;
		texCords[45] = 1.0f;
						
		//top right vertex
		texCords[46] = 1.0f;
		texCords[47] = 0.0f;
		
		// buffer for texture cords
		tBuffer = ByteBuffer.allocateDirect(4 * (2*6*numFrames));
		tBuffer.order(ByteOrder.nativeOrder());
		
		texBuffer = tBuffer.asFloatBuffer();
		texBuffer.put(texCords);
		texBuffer.position(0);
		
		//create VBO map
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texBuffer.capacity() * 4, texBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		TexCoordsBufferIdx = buffers[1];
		
	}
	
}
