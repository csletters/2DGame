package com.example.dgame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
	
	
	MyRenderer renderer;
	private GestureDetector mGestureDetector;
    public MyGLSurfaceView(Context context){
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setEGLContextClientVersion(2);
        renderer = new MyRenderer(context);
        setRenderer(renderer);
        
        //gesture listener
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }
    
    private class GestureListener extends  GestureDetector.SimpleOnGestureListener
	{
    	private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;
        float diffX;
        float diffY;
   
    	  @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
              boolean result = false;
              try {
                  diffY = e2.getY() - e1.getY();
                  diffX = e2.getX() - e1.getX();
                  if (Math.abs(diffX) > Math.abs(diffY)) {
                      if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                          if (diffX > 0) {
                              onSwipeRight();
                              requestRender();
                          } else {
                              onSwipeLeft();
                              requestRender();
                          }
                      }
                  }
                  else
                  {
                	  if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                          if (diffY > 0) {
                        	  onSwipeBottom();
                        	  requestRender();
                          } else {
                        	  onSwipeTop();
                        	  requestRender();
                          }
                      }
                  }
              } catch (Exception exception) {
                  exception.printStackTrace();
              }
              return result;
          }
    	  
    	// event when double tap occurs
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {

        	if(e.getAction() == e.ACTION_UP)
        	{
        		if(!renderer.isGridViewable)
        			renderer.displayGrid(e.getX(),e.getY());
        		else
        			renderer.moveUnit(e.getX(),e.getY());
        	}

              return true;
          }
    	
    	
    	
    	@Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    	public void onSwipeRight() {
    		renderer.updateCameraX(Math.abs(diffX));
        }

        public void onSwipeLeft() {
        	renderer.updateCameraX(-Math.abs(diffX));
        }

        public void onSwipeTop() {
        	renderer.updateCameraY(Math.abs(diffY));
        }

        public void onSwipeBottom() {
        	renderer.updateCameraY(-Math.abs(diffY));
        }
	}
    
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
    	/*float x = e.getX();
    	float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	
        	Log.w("adsd",Integer.toString( renderer.widthView));
        	if (x < renderer.widthView/2)
        	{
        		Log.w("sadsa","sadsadsad");
        		renderer.updateFocus(1);
        	}
        	else
        	{
        		Log.w("sadsa","jhgjgh");
        		renderer.updateFocus(-1);
        	}
            requestRender();
    }
        return true;*/
    	return mGestureDetector.onTouchEvent(e);
    }
}
