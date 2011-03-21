package com.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View {

	private static final String TAG = "Sudoku";
	private final SudokuGrabber game;
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();
	
	public PuzzleView(Context context) {
		super(context);
		this.game = (SudokuGrabber) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		
	}
	@Override
	protected void onDraw(Canvas canvas){
		Paint background =  new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		int newWidth  = getWidth();
		int newHeight = getHeight();
		if(newWidth>newHeight){
			newWidth = newHeight;
		}else{
			newHeight = newWidth;
		}
		
		canvas.drawRect(0,0,newWidth,newHeight, background);
		
		// Draw the board
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilte));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		//Draw the minor grid lines
		for(int i=0; i<9; i++){
			canvas.drawLine(0, i*height, newWidth, i*height, light);
			canvas.drawLine(i*width,0,i*width,newHeight,light);
		}
		
		//Draw the major grid lines
		for(int i=0; i<4;i++){
			canvas.drawLine(0, 3*i*height, newWidth, 3*i*height, dark);
			canvas.drawLine(3*i*width,0,3*i*width,newHeight,dark);
		}
		
		//Drawing the numbers
		//Define color and style for numbers
		
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height*0.75f);
		foreground.setTextScaleX(width/height);
		foreground.setTextAlign(Paint.Align.CENTER);
		Paint hint = new Paint();
		hint.setColor(getResources().getColor(R.color.puzzle_hint));

		// Draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		
		//Centering in X: use alignment
		float x = width/2;
		
		//Centering in Y: use alignment
		float y = height/2 - (fm.ascent+fm.descent)/2;
		
		Rect t = new Rect();
		for(int i=0;i<9;i++){
			for(int j=0; j<9; j++){
				canvas.drawText(this.game.getTileString(i,j), i*width+x, j*height + y, foreground);
				if(game.isHint(i,j)){
					getRect(i,j,t);
					canvas.drawRect(t, hint);
				}
			}
		}
		
		// Draw the invalid tile
		Paint invalid = new Paint();
		invalid.setColor(getResources().getColor(R.color.puzzle_invalid));
		Rect r = new Rect();
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(!game.isValid(i, j)){
					getRect(i,j,r);
					canvas.drawRect(r,invalid);
				}
			}
			
		}
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		width = w/9f;
		height = h/9f;
		if(width>height){
			width = height;
		}else{
			height = width;
		}
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width "+ width + "height "+ height);
		super.onSizeChanged(w, h, oldw, oldh);
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selX,selY-1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selX,selY+1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selX-1,selY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selX+1,selY);
			break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE:
			setSelectedTile(0);
			break;
		case KeyEvent.KEYCODE_1:
			setSelectedTile(1);
			break;
		case KeyEvent.KEYCODE_2:
			setSelectedTile(2);
			break;
		case KeyEvent.KEYCODE_3:
			setSelectedTile(3);
			break;
		case KeyEvent.KEYCODE_4:
			setSelectedTile(4);
			break;
		case KeyEvent.KEYCODE_5:
			setSelectedTile(5);
			break;
		case KeyEvent.KEYCODE_6:
			setSelectedTile(5);
			break;
		case KeyEvent.KEYCODE_7:
			setSelectedTile(7);
			break;
		case KeyEvent.KEYCODE_8:
			setSelectedTile(8);
			break;
		case KeyEvent.KEYCODE_9:
			setSelectedTile(9);
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			game.showKeypad(selX,selY);
			break;
		default:
			return super.onKeyDown(keyCode, event);
	
		}
	
		return true;
	}
	
	
	@Override 
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction()!=MotionEvent.ACTION_DOWN){
			return super.onTouchEvent(event);
		}
		if(event.getX()>(9*width)|| event.getY()>(9*height)){
			return super.onTouchEvent(event);
		}
		select((int)(event.getX()/width),(int)(event.getY()/height));
		game.showKeypad(selX, selY);
		Log.d(TAG,"onTouchEvent: x "+selX +", y "+ selY);
		return true;
	}
	private void getRect(int x, int y, Rect rect){
		rect.set((int)(x*width),(int)(y*height), (int)(x*width+width),(int)(y*height+height));
	}
	
	private void select(int x, int y){
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		invalidate(selRect);
	}
	
	public void setSelectedTile(int tile){
		game.setTile(selX, selY, tile);
		invalidate();
	}
}