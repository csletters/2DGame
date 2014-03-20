package gameresources;

public class ForestTiles extends Tiles{

	int defense = 1;
	int movement = -1;
	int attack = 0;
	
	public ForestTiles()
	{
		uv = new float[12];
		//top left vertex
		uv[0] = 0.4f;
		uv[1] = 0.15f;
		
		//bottom left vertex
		uv[2] = 0.4f;
		uv[3] = 0.95f;
		
		//bottom right vertex
		uv[4] = 0.5438f;
		uv[5] =  0.95f;
		
		//top left vertex
		uv[6] = 0.4f;
		uv[7] = 0.15f;
		
		//bottom right vertex
		uv[8] = 0.5438f;
		uv[9] =  0.95f;
		
		
		//top right vertex
		uv[10] = 0.5438f;
		uv[11] = 0.15f;
	}
	
	
}
