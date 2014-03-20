package gameresources;

public class GrassTiles extends Tiles {
	
	int defense = 1;
	int movement = -1;
	int attack = 0;
	public GrassTiles()
	{
		uv = new float[12];
		//top left vertex
		uv[0] = 0.8608f;
		uv[1] = 0.15f;
		
		//bottom left vertex
		uv[2] = 0.8608f;
		uv[3] = 0.85f;
		
		//bottom right vertex
		uv[4] = 0.9824f;
		uv[5] = 0.85f;
		
		
		
		//top left vertex
		uv[6] = 0.8608f;
		uv[7] = 0.15f;
		
		//bottom right vertex
		uv[8] = 0.9824f;
		uv[9] = 0.85f;
		
		//top right vertex
		uv[10] = 0.9824f;
		uv[11] = 0.15f;
	}
}
