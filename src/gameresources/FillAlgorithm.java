package gameresources;

import java.util.ArrayList;

import android.util.Log;

public class FillAlgorithm {

	
	 EmptyGrid[][] grid; 
	 Tiles[][] temp;
	 int xBound, yBound;
	 ArrayList<Float> vertex = new ArrayList<Float>();
	 float[] vertices;
	 int maxMovement;
	 
	 public FillAlgorithm(int range,int originX,int originY, int boundaryX, int boundaryY, Tiles[][] tiles )
	 {
		 maxMovement = range;
		 grid = new EmptyGrid[1+(range*2)][1+(range*2)];
		 for(int y = 0; y < 1+(range*2);y++)
		 {
			 for(int x = 0; x < 1+(range*2);x++)
			 {
				 grid[y][x] = new EmptyGrid();
			 }
		 }
		
		 grid[range][range].isReachable = true;
		 temp = tiles;
		 xBound = boundaryX;
		 yBound = boundaryY;
		 fill(range,range,range,originX,originY);
		 createVertices(range);
	 }
	 private void createVertices(int range) {
		// TODO Auto-generated method stub
		 for(int y = 0; y < 1+(range*2);y++)
		 {
			 for(int x = 0; x < 1+(range*2);x++)
			 {
				 if(grid[y][x].isReachable == true)
				 {
					 //top left vertex
					 vertex.add(0.0f+x-range);
					 vertex.add(1.0f+range-y);
					 vertex.add(0.0f);
					 
					 //bottom left vertex
					 vertex.add(0.0f+x-range);
					 vertex.add(0.0f+range-y);
					 vertex.add(0.0f);
					 
					 //bottom right vertex
					 vertex.add(1.0f+x-range);
					 vertex.add(0.0f+range-y);
					 vertex.add(0.0f);
					 
					 
					 //top left vertex
					 vertex.add(0.0f+x-range);
					 vertex.add(1.0f+range-y);
					 vertex.add(0.0f);
					 
					 //bottom right vertex
					 vertex.add(1.0f+x-range);
					 vertex.add(0.0f+range-y);
					 vertex.add(0.0f);
					 
					 //top right vertex
					 vertex.add(1.0f+x-range);
					 vertex.add(1.0f+range-y);
					 vertex.add(0.0f);
					 
				 }
			 }
		 }
		 vertices = new float[vertex.size()];
		 
	}
	
	public float[] getVertices()
	{
		for(int x =0; x< vertices.length;x++)
		{
			vertices[x] = vertex.get(x);
		}
		return vertices;
	}
	
	public boolean isValidLocation(float diffx, float diffy)
	{
		boolean isvalid = false;
		//check if the diff is within movement range
		if((Math.abs(diffx) <= maxMovement && Math.abs(diffy) <= maxMovement))
		{
			if(grid[(int) (maxMovement+diffy)][(int) (maxMovement+diffx)].isReachable == true)
			{
				isvalid = true;
			}
		}
		return isvalid;
	}
	 
	
	public  void fill(int movesLeft, int startX,int StartY, int maptileCordX, int maptileCordY)
	 {
		 if(movesLeft > 0)
		 {
			 //check top
			 int upIndex = maptileCordY -1;
			 if(upIndex > -1 && temp[upIndex][maptileCordX].unitOnTile == null)
			 {
				 grid[StartY-1][startX].isReachable = true;
				 fill(movesLeft-1,startX,StartY-1,maptileCordX,upIndex);
			 }
			 
			 //check left
			 int leftIndex = maptileCordX-1;
			 if(leftIndex > -1 &&  temp[maptileCordY][leftIndex].unitOnTile == null)
			 {
				 grid[StartY][startX-1].isReachable = true;
				 fill(movesLeft-1,startX-1,StartY,leftIndex,maptileCordY);
			 }
			 
			 //check right
			 int rightIndex = maptileCordX+1;
			 if(rightIndex < xBound && temp[maptileCordY][rightIndex].unitOnTile == null)
			 {
				 grid[StartY][startX+1].isReachable = true;
				 fill(movesLeft-1,startX+1,StartY,rightIndex,maptileCordY);
			 }
			 
			 //check bottom
			 int downIndex = maptileCordY+1;
			 if(downIndex < yBound && temp[downIndex][maptileCordX].unitOnTile == null)
			 {
				 grid[StartY+1][startX].isReachable = true;
				 fill(movesLeft-1,startX,StartY+1,maptileCordX,downIndex);
			 }
		 }
	
	 }
	 
	 
	 
}
