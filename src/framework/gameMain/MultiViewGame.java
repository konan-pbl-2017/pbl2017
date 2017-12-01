package framework.gameMain;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GraphicsConfigTemplate3D;

import com.sun.j3d.utils.universe.SimpleUniverse;

import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

/**
 * 状態遷移のないゲーム用のクラス
 * @author 新田直也
 *
 */
public abstract class MultiViewGame extends AbstractGame implements IGameState {
	protected Universe universe;
	protected Camera3D camera1;
	protected Camera3D camera2;
	protected RWTContainer container;
	
	public abstract void init(Universe universe, Camera3D camera1, Camera3D camera2);
	public abstract void progress(RWTVirtualController virtualController, long interval);
	
	@Override
	protected IGameState getCurrentGameState() {
		return this;
	}

	@Override
	public boolean useTimer() {
		return true;
	}

	@Override
	public void init(RWTFrame3D frame) {
		container = new RWTContainer() {
			@Override
			public void build(GraphicsConfiguration gc) {				
				RWTCanvas3D canvas1, canvas2;
				if (gc != null) {
					canvas1 = new RWTCanvas3D(gc);
					canvas2 = new RWTCanvas3D(gc);
				} else {
					canvas1 = new RWTCanvas3D();
					canvas2 = new RWTCanvas3D();
				}
				canvas1.setRelativePosition(0.0f, 0.0f);
				canvas1.setRelativeSize(0.5f, 1.0f);
				canvas2.setRelativePosition(0.5f, 0.0f);
				canvas2.setRelativeSize(0.5f, 1.0f);
				addCanvas(canvas1);
				addCanvas(canvas2);
				
				repaint();
			}
			// RWT側ではイベント処理をしない
			@Override
			public void keyPressed(RWTVirtualKey key) {}
			@Override
			public void keyReleased(RWTVirtualKey key) {}
			@Override
			public void keyTyped(RWTVirtualKey key) {}
		};		
		frame.setContentPane(container);
		GraphicsConfiguration gc = null;
		if (frame.isShadowCasting()) {
			// 影を付ける場合
			// ステンシルバッファを使用する GraphicsConfiguration の生成
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfigTemplate3D gct3D = new GraphicsConfigTemplate3D();
			gct3D.setStencilSize(8);
			gc = gd.getBestConfiguration(gct3D);
		}
		container.build(gc);

		universe = new Universe();
		camera1 = new Camera3D(universe);
		camera2 = new Camera3D(universe);
		init(universe, camera1, camera2);
		container.getRWTCanvas3D(0).attachCamera(camera1);
		container.getRWTCanvas3D(1).attachCamera(camera2);
		universe.compile();
	}
	
	@Override
	public void update(RWTVirtualController virtualController, long interval) {
		progress(virtualController, interval);
		camera1.adjust(interval);
		camera2.adjust(interval);
	}
}
