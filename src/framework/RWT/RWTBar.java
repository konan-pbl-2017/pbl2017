package framework.RWT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;


public class RWTBar extends RWTWidget {
	int max;
	int value;
	
	private float relativeX = 0.05f;
	private float relativeY = 0.02f;
	private float relativeWidth = 0.90f;
	private float relativeHeight = 0.05f;
	
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;	

	public RWTBar(int max, int value){
		this.max=max;
		this.value=value;
	}

	public int getAbsoluteHeight() {
		return height;
	}

	public int getAbsoluteWidth() {
		return width;
	}

	public int getAbsoluteX() {
		return x;
	}

	public int getAbsoluteY() {
		return y;
	}

	@Override
	public void adjust(Component parent) {
		int sx = parent.getWidth();
		int sy = parent.getHeight();
		x = (int) (sx * relativeX);
		y = (int) (sy * relativeY);
		width = (int) (sx * relativeWidth);
		height = (int) (sy * relativeHeight);

	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.drawRect(x, y, width, height);
		g.setColor(Color.GREEN);
		g.fillRect(x+1, y+1, (width-1)*value/max, height-1);
		g.setColor(color.RED);
		g.fillRect(((width-1)*value/max)+x, y+1, width - (width-1)*value/max, height-1);
		
	}
	
	public void setValue(int y){
		value = y;
//		repaint();
	}
	
	public int getValue(){
		return value;
	}
	/**
	 * 相対位置を指定する。
	 * @param x x座標値(0.0f～1.0f)
	 * @param y y座標値(0.0f～1.0f)
	 */
	public void setRelativePosition(float x, float y) {
		relativeX = x;
		relativeY = y;
	}
	/**
	 * 相対サイズを指定する
	 * @param width 横幅(0.0f～1.0f)
	 * @param　height 縦幅(0.0f～1.0f)
	 */
	public void setRelativeSize(float width, float height) {
		relativeWidth = width;
		relativeHeight = height;
	}
}