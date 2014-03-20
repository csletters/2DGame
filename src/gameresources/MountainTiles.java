package gameresources;

public class MountainTiles extends Tiles{

	int defense = 2;
	int movement = -2;
	int attack = 1;
	
	public MountainTiles()
	{

		uv = new float[12];
		
		//triangle one
		//top left vertex
		uv[0] = 0.2105f;
		uv[1] = 0.15f;
		
		//bottom left vertex
		uv[2] = 0.2105f;
		uv[3] = 0.9f;
		
		//bottom right vertex
		uv[4] = 0.3508f;
		uv[5] = 0.9f;
		
		
		//triangle two
		//top left vertex
		uv[6] = 0.2105f;
		uv[7] = 0.15f;
		
		//bottom right vertex
		uv[8] = 0.3508f;
		uv[9] = 0.9f;
		
		//top right vertex
		uv[10] = 0.3508f;
		uv[11] = 0.15f;

	}
}
