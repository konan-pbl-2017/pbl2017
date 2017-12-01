package framework.RWT;
import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.View;
import javax.swing.event.MouseInputListener;

import com.sun.j3d.utils.universe.SimpleUniverse;

import framework.model3D.IViewer3D;
import framework.model3D.Universe;
import framework.schedule.ScheduleManager;
import framework.schedule.TaskController;
import framework.view3D.Camera3D;
import framework.view3D.Viewer3D;

/**
 * GUI部品（RWTWidget）を配置可能なCanvas3D、親コンテナに対して相対座標で配置できる
 * @author 新田直也
 *
 */
@SuppressWarnings("serial")
public class RWTCanvas3D extends Canvas3D {
	private ArrayList<RWTWidget> widgetList = new ArrayList<RWTWidget>();
	private float relativeX = 0.0f;
	private float relativeY = 0.0f;
	private float relativeWidth = 0.0f;
	private float relativeHeight = 0.0f;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;	
	
	private Camera3D camera = null;
	private IViewer3D viewer = null;
	
	private DisplayCanvas displayCanvas = null;
	private BufferedImage image = null;
	private static TaskController renderingController = ScheduleManager.getInstance().registerTask("rendering");

	public RWTCanvas3D() {
		this(SimpleUniverse.getPreferredConfiguration());
	}
	
	public RWTCanvas3D(GraphicsConfiguration gc) {
		this(gc, false);
	}
	
	public RWTCanvas3D(boolean offScreen) {
		this(SimpleUniverse.getPreferredConfiguration(), offScreen);
	}
	
	public RWTCanvas3D(GraphicsConfiguration gc, boolean offScreen) {
		super(gc, offScreen);
		setFocusable(false);
		if (offScreen) {
			displayCanvas = new DisplayCanvas();
		} else {
			enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);			
		}
		renderingController.deactivate();
	}
	
	public Canvas getDisplayCanvas() {
		if (isOffScreen()) {
			return displayCanvas;
		}
		return this;
	}

	public void attachCamera(Camera3D camera) {
		viewer = new Viewer3D(camera);
		View oldView = this.getView();
		if (oldView != null) oldView.removeCanvas3D(this);
		camera.getView().addCanvas3D(this);
		this.camera = camera;
	}
	
	/**
	 * Widgetを配置する。
	 * @param widget
	 */
	public void addWidget(RWTWidget widget) {
		widgetList.add(widget);
	}
	
	/**
	 * RWTCanvas3Dの相対位置を決定する。
	 * @param x x座標値(0.0f～1.0f)
	 * @param y y座標値(0.0f～1.0f)
	 */
	public void setRelativePosition(float x, float y) {
		relativeX = x;
		relativeY = y;
	}

	/**
	 * RWTCanvas3Dの相対サイズを決定する。
	 * @param w 幅(0.0f～1.0f)
	 * @param h 高さ(0.0f～1.0f)
	 */
	public void setRelativeSize(float w, float h) {
		relativeWidth = w;
		relativeHeight = h;
	}

	/**
	 * 親のコンテナに応じて位置とサイズを合わせる。
	 * @param parent 親コンテナ
	 */
	public void adjust(Container parent) {
		int sx = parent.getWidth();
		int sy = parent.getHeight();
		x = (int) (sx * relativeX);
		y = (int) (sy * relativeY);
		width = (int) (sx * relativeWidth);
		height = (int) (sy * relativeHeight);
		if (isOffScreen()) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			ImageComponent2D ic = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, image, true, false);
			ic.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
			ic.setCapability(ImageComponent2D.ALLOW_IMAGE_WRITE);
			setOffScreenBuffer(ic);
			displayCanvas.setBounds(x, y, width, height);
			getScreen3D().setSize(width, height);
			getScreen3D().setPhysicalScreenWidth(0.0254/90.0 * (double)width);
			getScreen3D().setPhysicalScreenHeight(0.0254/90.0 * (double)height);
		} else {
			setBounds(x, y, width, height);			
		}
	}
	
	@Override
	public void preRender() {
		if (viewer == null || camera == null) return;
		viewer.setGraphicsContext3D(this.getGraphicsContext3D());
		camera.getUniverse().preRender(viewer);
	}
	
	@Override
	public void renderField(int fieldDesc) {
		if (viewer == null || camera == null) return;
		viewer.setGraphicsContext3D(this.getGraphicsContext3D());
		camera.getUniverse().renderField(viewer);
	}

	@Override
	public void postRender() {
		if (viewer == null || camera == null) return;
		viewer.setGraphicsContext3D(this.getGraphicsContext3D());
		camera.getUniverse().postRender(viewer);
		if (isOffScreen()) {
			//　オフスクリーンバッファ上の画像取得
			ImageComponent2D ic = getOffScreenBuffer();
			image = ic.getImage();
			Graphics src = image.getGraphics();
			//　キャンバス上の　Widget を合成
			drawWidgets(src);
			src.dispose();
			//　ディスプレイバッファに描画
			Graphics dst = displayCanvas.getGraphics();
			dst.drawImage(image, 0, 0, null);
			dst.dispose();
		}
	}
	
	@Override
	public void postSwap() {
		super.postSwap();
		
		if (!isOffScreen()) {
			//　キャンバス上の　Widget の描画
			Graphics g = getGraphics();
			drawWidgets(g);
			g.dispose();
		}
		renderingController.deactivate();
	}
	
	/**
	 * オフスクリーンの場合のみ、レンダリングを明示的にスケジューリングする
	 */
	public void doRender() {
		if (isOffScreen()) {
			if (renderingController.activate()) {
				// 前回のレンダリングが終了していた場合
				renderOffScreenBuffer();
			}
		}
	}

	private void drawWidgets(Graphics g) {
		for(int i = 0; i < widgetList.size(); i++) {
			RWTWidget widget = widgetList.get(i);
			if(widget.isVisible()) {
				widget.adjust(this);
				widget.paint(g);
			}
		}
	}
	
	public void processEvent(AWTEvent e) {
		// イベントをコンテナ側に返す
		Container c = this.getParent();
		if (c != null) c.dispatchEvent(e);
	}
	
	private class DisplayCanvas extends Canvas {
		
		public DisplayCanvas() {
			setFocusable(false);
			enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
		}
		
		public void paint(Graphics g) {
			super.paint(g);
			if (image != null) {
				synchronized (image) {
					g.drawImage(image, 0, 0, null);
				}
			}
		}
		
		public void processEvent(AWTEvent e) {
			// イベントをコンテナ側に返す
			Container c = this.getParent();
			if (c != null) c.dispatchEvent(e);
		}		
	}
}
