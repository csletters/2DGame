package com.example.dgame;

import gameresources.FillAlgorithm;
import gameresources.GrassTiles;
import gameresources.Map;
import gameresources.PolygonRenderer;
import gameresources.TestUnit;
import gameresources.UnitRender;
import gameresources.UnitStats;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import loaders.RawResourceReader;
import loaders.ShaderHandles;
import loaders.ShaderHelper;
import loaders.TextureHelper;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class MyRenderer implements Renderer {

	private final Context mActivityContext;
	float[] projection = new float[16];
	float[] view = new float[16];
	float[] model = new float[16];
	float destinationY,destinationX;
	int currentlySelectedTileX, currentlySelectedTileY;
	int widthView, heightView;
	int boundaryX, boundaryY;
	int frames = 0;
	float xCamera = 0.0f, yCamera = 0.0f, zCamera = 8.0f;
	float xTrans = 0.0f, yTrans = 0.0f;
	ArrayList<ShaderHandles> shaderPrograms = new ArrayList<ShaderHandles>();
	ArrayList<UnitStats> units = new ArrayList<UnitStats>();
	float time = System.nanoTime();
	float frameTime = 0;
	float unitTime = 0;
	float startTime = 0;
	float elapsedTime = 0;
	Map map;
	int fps = 0;
	float elapsedtime = 0;
	float ratio;
	public boolean isGridViewable = false;
	float pixelsperBox, numBoxesPerScreen,diffXtrans,xTransPerIndex,startXTrans;
	float yPixelsperBox,ynumBoxesPerScreen,diffYtrans,yTransPerIndex,startYTrans;
	UnitRender test;
	PolygonRenderer poly;
	FillAlgorithm movementRange;
	float remainder=0;

	public MyRenderer(final Context activityContext) {
		mActivityContext = activityContext;
		map = new Map(mActivityContext);
		poly = new PolygonRenderer();
		
		//make a better way of doing this later
		UnitStats basic = new UnitStats();
		basic.attack =1;
		basic.defense =0;
		basic.health = 10;
		basic.movement = 2;
		basic.unitName = "tester";
		map.tile[4][4].unitOnTile = basic;
		map.tile[4][4].unitOnTile.xLocation =4;
		map.tile[4][4].unitOnTile.yLocation =4;
		units.add(basic);
		
		basic = new UnitStats();
		basic.attack =1;
		basic.defense =0;
		basic.health = 10;
		basic.movement = 2;
		basic.unitName = "tester";
		map.tile[4][5].unitOnTile = basic;
		map.tile[4][5].unitOnTile.xLocation =5;
		map.tile[4][5].unitOnTile.yLocation =4;
		units.add(basic);
		for(int x = 0;x < 10; x++ )
		{
			basic = new UnitStats();
			basic.attack =1;
			basic.defense =0;
			basic.health = 10;
			basic.movement = 2;
			basic.unitName = "tester";
			map.tile[5][x].unitOnTile = basic;
			map.tile[5][x].unitOnTile.xLocation =x;
			map.tile[5][x].unitOnTile.yLocation =5;
			units.add(basic);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		if(frames != 0)
		{
			updateMap();
		}
		GLES20.glUseProgram(shaderPrograms.get(0).programHandle);
		drawMap();
		GLES20.glEnable(GLES20.GL_BLEND);
		drawUnits();
		GLES20.glDisable(GLES20.GL_BLEND);
		if(isGridViewable)
		{
			GLES20.glUseProgram(shaderPrograms.get(1).programHandle);
			drawWireFrame();
		}
		
		fps++;
		fpsCounter();
	}


	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
		ratio = (float)width/height;
		Matrix.perspectiveM(projection, 0, 90, ratio, 1, 1000);
		Matrix.setLookAtM(view, 0, xCamera, yCamera, zCamera, xCamera, yCamera, 0, 0, 1, 0);
		Matrix.setIdentityM(model, 0);
		widthView = width;
		
		heightView = height;
		convertCameraToWorldCords();
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		GLES20.glClearDepthf(1.0f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glDepthMask(true);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		
		//create shader program handles and program for display textures
		ShaderHandles shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertextexture,R.raw.fragmenttexture);
		shader.mTextureDataHandle.add(TextureHelper.loadTexture(mActivityContext, R.drawable.mapterrain));
		shader.mTextureDataHandle.add(TextureHelper.loadTexture(mActivityContext, R.drawable.testidle));
		initBasicHandlesWTexture(shader.programHandle,shader);
		shaderPrograms.add(shader);
		
		//create shader program for wireframe
		shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertexgrid,R.raw.fragmentgrid);
		initBasicHandles(shader.programHandle,shader);
		shaderPrograms.add(shader);

		map.tiles.createVBO();
		
		//create units
		test = new TestUnit();
		
	}

	public int createShader(int vertex, int fragment) {
		String vertexShaderCode = RawResourceReader
				.readTextFileFromRawResource(mActivityContext, vertex);
		String fragmentShaderCode = RawResourceReader
				.readTextFileFromRawResource(mActivityContext, fragment);

		int vertexShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
			
		int mProgram;
		
		mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle,fragmentShaderHandle);

		return mProgram;
	}
	
	public void initBasicHandles(int mProgram, ShaderHandles shader)
	{
		//attributes
		shader.positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		
		//uniforms
		shader.modelHandle =  GLES20.glGetUniformLocation(mProgram, "model");
		shader.viewHandle =  GLES20.glGetUniformLocation(mProgram, "view");
		shader.projectionHandle =  GLES20.glGetUniformLocation(mProgram, "projection");
	}
	
	public void initBasicHandlesWTexture(int mProgram, ShaderHandles shader)
	{
		//attributes
		shader.positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		shader.mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTexCord");
									
		//uniforms
		shader.modelHandle =  GLES20.glGetUniformLocation(mProgram, "model");
		shader.viewHandle =  GLES20.glGetUniformLocation(mProgram, "view");
		shader.projectionHandle =  GLES20.glGetUniformLocation(mProgram, "projection");
		shader.mTextureUniformHandle =  GLES20.glGetUniformLocation(mProgram, "uTexture");
		
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		GLES20.glUniform1i(shader.mTextureUniformHandle, 0);
		
	}

	public void drawMap()
	{
		Matrix.translateM(model, 0, -((float)map.mapWidth/2.0f), map.mapHeight/2.0f, 0.0f);
		Matrix.translateM(model, 0, xTrans, yTrans, 0.0f);
		map.tiles.draw(projection, view, model, shaderPrograms.get(0).projectionHandle, shaderPrograms.get(0).viewHandle, shaderPrograms.get(0).modelHandle, shaderPrograms.get(0).positionHandle, shaderPrograms.get(0).mTextureCoordinateHandle,shaderPrograms.get(0).mTextureDataHandle.get(0));
		Matrix.setIdentityM(model, 0);
	}
	
	public void drawUnits()
	{
		elapsedTime = (float) (( System.nanoTime() - time ) / 1000000000.0f);
		time = System.nanoTime();
		unitTime += elapsedTime;
		if(unitTime >= 0.2)
		{
			unitTime = 0;
			test.currentFrame++;
			if(test.currentFrame == test.numFrames)
				test.currentFrame = 0;
		}
		for(int x = 0; x < units.size();x++)
		{
			Matrix.translateM(model, 0, -((float)map.mapWidth/2.0f), map.mapHeight/2.0f, 0.0f);
			Matrix.translateM(model, 0, xTrans+units.get(x).xLocation,  yTrans-units.get(x).yLocation, 0.02f);
			if(units.get(x).unitName.equals("tester"))
				test.draw(projection, view, model, shaderPrograms.get(0).projectionHandle, shaderPrograms.get(0).viewHandle, shaderPrograms.get(0).modelHandle, shaderPrograms.get(0).positionHandle, shaderPrograms.get(0).mTextureCoordinateHandle, shaderPrograms.get(0).mTextureDataHandle.get(1));
			Matrix.setIdentityM(model, 0);
		}
	}
	
	private void drawWireFrame() {
		// TODO Auto-generated method stub
		Matrix.translateM(model, 0, -((float)map.mapWidth/2.0f), map.mapHeight/2.0f, 0.0f);
		Matrix.translateM(model, 0, xTrans, yTrans, 0.03f);
		map.tiles.drawWireFrame(projection, view, model, shaderPrograms.get(1).projectionHandle, shaderPrograms.get(1).viewHandle, shaderPrograms.get(1).modelHandle, shaderPrograms.get(1).positionHandle);
		Matrix.setIdentityM(model, 0);
		
		GLES20.glEnable(GLES20.GL_BLEND);
		//draw the polygon movement range
		Matrix.translateM(model, 0, -((float)map.mapWidth/2.0f), map.mapHeight/2.0f, 0.0f);
		Matrix.translateM(model, 0, xTrans+currentlySelectedTileX,  yTrans-currentlySelectedTileY, 0.01f);
		poly.draw(projection, view, model, shaderPrograms.get(1).projectionHandle, shaderPrograms.get(1).viewHandle, shaderPrograms.get(1).modelHandle, shaderPrograms.get(1).positionHandle);
		Matrix.setIdentityM(model, 0);
		GLES20.glDisable(GLES20.GL_BLEND);

	}
	
	public void updateMap()
	{
		//float elapsedTime = (float) (( System.nanoTime() - time ) / 1000000000.0f);
		//time = System.nanoTime();
		frameTime += elapsedTime;
		if(frameTime >= 0.06f) // 60 fps
		{
			frameTime = 0;
			xTrans = xTrans + destinationX;
			yTrans = yTrans + destinationY;
			
			//camera bounds checking
			if(xTrans > (float)map.mapWidth/2.0f - (ratio*(zCamera*Math.tanh(45.0))))
			{
				xTrans = (float) (map.mapWidth/2.0 - (ratio*(zCamera*Math.tanh(45.0))));
			}
			else if(xTrans < -((float)map.mapWidth/2.0f - (ratio*(zCamera*Math.tanh(45.0)))))
			{
				xTrans = (float) -(map.mapWidth/2.0 - (ratio*(zCamera*Math.tanh(45.0))));
			}
			
			if(yTrans > (float)(map.mapHeight/2.0f - ((zCamera*Math.tanh(45.0)))-1.0f))
			{
				yTrans = (float)(map.mapHeight/2.0f - ((zCamera*Math.tanh(45.0)))-1.0f);
			}
			else if(yTrans < (float)-(map.mapHeight/2.0f - ((zCamera*Math.tanh(45.0)))+1.0f))
			{
				yTrans = (float)-(map.mapHeight/2.0f - ((zCamera*Math.tanh(45.0)))+1.0f);
			}
			frames--;
		}
			
		
	}
	
	public void fpsCounter()
	{
		float diff = (( System.nanoTime() - startTime ) / 1000000000.0f);
		startTime = System.nanoTime();
		elapsedtime += diff;
		if(elapsedtime > 1)
		{
			elapsedtime = 0;
			Log.w("fps",Integer.toString(fps));
			fps = 0;
		}
		
	}
	
	public void updateCameraX(float distance)
	{
		float translation;
		frames = 5;
		destinationY = 0;
		if(Math.abs(distance) > 400)
			translation = 1.6f;
		else if(Math.abs(distance) > 300)
			translation = 0.6f;
		else if(Math.abs(distance) > 200)
			translation = 0.4f;
		else
			translation = 0.2f;
		
		if(distance > 0)
		{
			if(xTrans == (float) -(map.mapWidth/2.0 - (ratio*(zCamera*Math.tanh(45.0)))))
				destinationX = translation-(remainder/5);
			else
				destinationX = translation;
		}
		else
			destinationX = -translation;

	}
  	
	public void updateCameraY(float distance)
	{
		float translation;
		frames = 5;
		destinationX = 0;
		if(Math.abs(distance) > 400)
			translation = 1.6f;
		else if(Math.abs(distance) > 300)
			translation = 0.6f;
		else if(Math.abs(distance) > 200)
			translation = 0.4f;
		else
			translation = 0.2f;
		
		
		if(distance > 0)
			destinationY = translation;
		else
			destinationY = -translation;
	}

	//if a unit is double tapped display the grid
	public void displayGrid(float f, float g) {
		// TODO Auto-generated method stub
		
		//get screen to world cordinates
		int box = (int) Math.floor(f/pixelsperBox);
		int xIndex = (int) Math.floor((startXTrans - xTrans)/xTransPerIndex);
		
		int yBox = (int) Math.floor(g/pixelsperBox);
		int yIndex = (int) Math.floor((yTrans - startYTrans)/yTransPerIndex);
		
		
		if(map.tile[yBox+yIndex][box+xIndex].unitOnTile != null)
		{
			currentlySelectedTileX = box+xIndex;
			currentlySelectedTileY = yBox+yIndex;
			movementRange = new FillAlgorithm(map.tile[yBox+yIndex][box+xIndex].unitOnTile.movement, box+xIndex, yBox+yIndex, map.mapWidth, map.mapHeight, map.tile);
			poly.loadPolygon(movementRange.getVertices());
			isGridViewable = !isGridViewable;
		}
	}

	public void moveUnit(float x, float y) {
		// TODO Auto-generated method stub
		//get screen to world cordinates
		int box = (int) Math.floor(x/pixelsperBox);
		int xIndex = (int) Math.floor((startXTrans - xTrans)/xTransPerIndex);
		
		int yBox = (int) Math.floor(y/pixelsperBox);
		int yIndex = (int) Math.floor((yTrans - startYTrans)/yTransPerIndex);
		boolean isValid  = movementRange.isValidLocation((box+xIndex)-currentlySelectedTileX, (yBox+yIndex)-currentlySelectedTileY);
		if(!((currentlySelectedTileY == yBox+yIndex && currentlySelectedTileX == box+xIndex) || map.tile[yBox+yIndex][box+xIndex].unitOnTile != null || !isValid))
		{
			UnitStats temp = map.tile[currentlySelectedTileY][currentlySelectedTileX].unitOnTile;
			temp.xLocation =box+xIndex;
			temp.yLocation = yBox+yIndex;
			map.tile[yBox+yIndex][box+xIndex].unitOnTile = temp;
			map.tile[currentlySelectedTileY][currentlySelectedTileX].unitOnTile = null;
		}
		isGridViewable = !isGridViewable;
	}
	
	public void convertCameraToWorldCords()
	{
		//math shit X
		pixelsperBox =  (float) ((float) widthView/(ratio*(zCamera*Math.tanh(45.0))*2.0f));
		numBoxesPerScreen = (float) (ratio*(zCamera*Math.tanh(45.0))*2.0f);
		diffXtrans = (float) (map.mapWidth/2.0 - (ratio*(zCamera*Math.tanh(45.0)))) - (float) -(map.mapWidth/2.0 - (ratio*(zCamera*Math.tanh(45.0))));
		xTransPerIndex = (float) (diffXtrans/ (map.mapWidth-Math.floor(numBoxesPerScreen)));
		startXTrans = (float) (map.mapWidth/2.0 - (ratio*(zCamera*Math.tanh(45.0))));
		
		
		//math shit Y
		yPixelsperBox = (float) (heightView/((zCamera*Math.tanh(45.0))*2.0f));
		ynumBoxesPerScreen = (float) ((zCamera*Math.tanh(45.0))*2.0f);
		diffYtrans = (float)(map.mapHeight/2.0f - ((zCamera*Math.tanh(45.0)))-1.0f) - (float)-(map.mapHeight/2.0f - ((zCamera*Math.tanh(45.0)))+1.0f);
		yTransPerIndex = (float) (diffYtrans/ (map.mapHeight-Math.floor(ynumBoxesPerScreen)));
		startYTrans = (float)-(map.mapHeight/2.0f - ((zCamera*Math.tanh(45.0)))+1.0f);
		
		remainder = (float) (numBoxesPerScreen - Math.floor(numBoxesPerScreen));
		 
	}
}
