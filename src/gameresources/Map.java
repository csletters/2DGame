package gameresources;


import com.example.dgame.R;

import android.content.Context;
import loaders.RawResourceReader;

public class Map {

	
	public int mapWidth, mapHeight;
	public MapTiles tiles;
	public Tiles[][] tile;
	private final Context mActivityContext;
	//in future load map details from a text file
	public Map(final Context activityContext)
	{
		mActivityContext = activityContext;
		
		generateTerrain();
		
	}
	
	//in future read a file and assign each tile
	public void generateTerrain()
	{
		String map = loadfile();
		String[] tokens = map.split("[ ]+");
		mapWidth = Integer.parseInt(tokens[0]);
		mapHeight = Integer.parseInt(tokens[1]);
		tiles = new MapTiles(mapWidth,mapHeight);
		tile =  new Tiles[mapHeight][mapWidth];
		for(int y = 0; y < mapHeight; y ++)
		{
			for(int x = 0; x < mapWidth; x++)
			{
				if(tokens[2].charAt((y*mapWidth)+x) == 'm')
				{
					tile[y][x] = new MountainTiles();
					tiles.setUVCords(tile[y][x].uv);
				}
				else if(tokens[2].charAt((y*mapWidth)+x) == 'g')
				{
					tile[y][x] = new GrassTiles();
					tiles.setUVCords(tile[y][x].uv);
				}
				else if(tokens[2].charAt((y*mapWidth)+x) == 'f')
				{
					tile[y][x] = new ForestTiles();
					tiles.setUVCords(tile[y][x].uv);
				}
				else
				{}
			}
		}
	}
	
	public String loadfile()
	{
		String body = RawResourceReader.readTextFile(mActivityContext,R.raw.map );
		
		return body;
	}
	
	
	
}
