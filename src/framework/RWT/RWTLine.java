package framework.RWT;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.vecmath.Point2f;

/**
 * ラインです。
 * @author Wataru
 *
 */
public class RWTLine extends RWTWidget {
	private float relativeX1 = 0.0f;
	private float relativeY1 = 0.0f;
	private float relativeX2 = 0.0f;
	private float relativeY2 = 0.0f;
	private int x1 = 0;
	private int y1 = 0;
	private int x2 = 0;
	private int y2 = 0;

	/**
	 * 場所を設定します。値は相対値です。
	 * @param w
	 * @param h
	 */
	public void setRelativePosition(float x1, float y1, float x2, float y2) {
		relativeX1 = x1;
		relativeY1 = y1;
		relativeX2 = x2;
		relativeY2 = y2;
	}

	@Override
	public void adjust(Component parent) {
		int sx = parent.getWidth();
		int sy = parent.getHeight();
		x1 = (int) (sx * relativeX1);
		y1 = (int) (sy * relativeY1);
		x2 = (int) (sx * relativeX2);
		y2 = (int) (sy * relativeY2);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}
	
//	@Override
//	public void paint(Graphics g, ArrayList<Point> list, double scale) {
//		// TODO Auto-generated method stub
//		g.setColor(color);
//		g.drawLine(list.get(0).x, list.get(0).y, list.get(1).x, list.get(1).y);
//	}

}
