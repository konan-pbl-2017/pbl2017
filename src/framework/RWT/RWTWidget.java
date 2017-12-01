package framework.RWT;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import javax.vecmath.Point2f;

/**
 * 親コンテナに対して相対座標で配置できるGUI部品
 * @author Wataru
 *
 */
public abstract class RWTWidget {
	protected Color color = Color.BLACK;
	Component parent;
	protected boolean visible = true;
	
	/**
	 * 親コンテナ（キャンバスの場合もある）の大きさに合わせて位置とサイズを調整する
	 * @param parent
	 */
	public abstract void adjust(Component parent);
	public abstract void paint(Graphics g);
	
	public void setParent(Component parent){
		this.parent = parent;
	}
	
	public void repaint(){
		parent.repaint();
	}
	
	/**
	 * 色を設定する
	 * @param c
	 */
	public void setColor(Color c) {
		color = c;
	}
	
	public void setVisible(boolean b) {
		visible = b;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
