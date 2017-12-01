package framework.RWT;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.event.MouseInputListener;


import framework.model3D.Universe;


/**
 * GUI部品（RWTWidget）を配置可能なコンテナ、GUI部品のアクティベーションも管理できる
 * 
 * @author 新田直也
 * 
 */
@SuppressWarnings("serial")
public abstract class RWTContainer extends Container {
	private ArrayList<RWTCanvas3D> canvases = null;

	private ArrayList<RWTWidget> widgetList = new ArrayList<RWTWidget>();
	private RWTSelectionManager active = new RWTSelectionManager();

	public abstract void build(GraphicsConfiguration gc);

	public abstract void keyPressed(RWTVirtualKey key);

	public abstract void keyReleased(RWTVirtualKey key);

	public abstract void keyTyped(RWTVirtualKey key);

	public RWTContainer() {
		setLayout(null);
		setFocusable(false);
		widgetList.add(active.getSelector());
		enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	public void addCanvas(RWTCanvas3D cavas) {
		if (canvases == null) canvases = new ArrayList<RWTCanvas3D>();
		canvases.add(cavas);
		cavas.adjust(this);
		add(cavas.getDisplayCanvas());
	}

	public void paint(Graphics g) {
		super.paint(g);

		if (canvases != null) {
			for (int i = 0; i < canvases.size(); i++) {
				canvases.get(i).adjust(this);
				canvases.get(i).getDisplayCanvas().paint(g);
			}
		}

		for (int i = 0; i < widgetList.size(); i++) {
			RWTWidget widget = widgetList.get(i);
			if (widget.isVisible()) {
				widget.adjust(this);
				widget.paint(g);
			}
		}
	}

	public RWTCanvas3D getPrimaryRWTCanvas3D() {
		if (canvases == null) return null;
		return canvases.get(0);
	}
	
	public int getNumberOfRWTCanvas3D() {
		if (canvases == null) return 0;
		return canvases.size();
	}
	
	public RWTCanvas3D getRWTCanvas3D(int index) {
		return canvases.get(index);
	}

	/**
	 * カーソルで選べないWidgetを加えます。
	 * 
	 * @param widget
	 */
	public void addWidget(RWTWidget widget) {
		widgetList.add(widget);
	}
	
	/**
	 * カーソルで選べないWidgetを加えます。
	 * 
	 * @param widget
	 */
	public void addWidgetOnBack(RWTWidget widget) {
		widgetList.add(0, widget);
	}

	/**
	 * 　アクティベーションを操作するマネージャーを設定する。
	 * 
	 * @param manager
	 */
	public void setActiveManager(RWTSelectionManager manager) {
		active = manager;
	}

	/**
	 * カーソルで選べるWidgetを加えます。
	 * 
	 * @param w
	 * @param n
	 * @param m
	 */
	public void addSelectableWidget(RWTSelectableWidget r, int n, int m) {
		active.add(r, n, m);
		widgetList.add(0, (RWTWidget) r);
	}

	/**
	 * カーソルで選べるWidgetを前面に加えます。
	 * 
	 * @param w
	 * @param n
	 * @param m
	 */
	public void addSelectableWidgetOnFront(RWTSelectableWidget r, int n, int m) {
		active.add(r, n, m);
		widgetList.add((RWTWidget) r);
	}

	/**
	 * カーソルをひとつ上に動かします。
	 */
	public void cursorMoveUp() {
		active.up();
		repaint();
	}

	/**
	 * カーソルをひとつ下に動かします。
	 */
	public void cursorMoveDown() {
		active.down();
		repaint();
	}

	/**
	 * カーソルをひとつ左に動かします。
	 */
	public void cursorMoveLeft() {
		active.left();
		repaint();
	}

	/**
	 * カーソルをひとつ右に動かします。
	 */
	public void cursorMoveRight() {
		active.right();
		repaint();
	}

	public RWTWidget getSelectedWidget() {
		return active.getSelectedWidget();
	}
	
	public void processEvent(AWTEvent e) {
		// イベントを RWTFrame3D に返す
		Container c = this.getParent();
		while (c != null && !(c instanceof RWTFrame3D)) {
			c = c.getParent();
		}
		if (c != null) ((RWTFrame3D)c).processEvent(e);
		super.processEvent(e);
	}
}
