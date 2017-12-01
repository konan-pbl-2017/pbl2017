package framework.gameMain;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.GraphicsConfigTemplate3D;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

/**
 * 状態遷移のないゲーム用のクラス
 * @author 新田直也
 *
 */
public abstract class SimpleGame extends AbstractGame implements IGameState {
	protected Universe universe;
	protected Camera3D camera;
	private IGameState currentState = this;
	
	public abstract void init(Universe universe, Camera3D camera);
	public abstract void progress(RWTVirtualController virtualController, long interval);
	
	@Override
	protected IGameState getCurrentGameState() {
		return currentState;
	}
	
	protected void setCurrentGameState(IGameState state) {
		currentState = state;
	}

	@Override
	public boolean useTimer() {
		return true;
	}

	@Override
	public void init(RWTFrame3D frame) {
		RWTContainer container = createRWTContainer();		
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
		camera = new Camera3D(universe);
//		universe.setCamera(camera);
//		SceneGraph root = new SceneGraph();
//		universe.setRoot(root);
		init(universe, camera);
		container.getPrimaryRWTCanvas3D().attachCamera(camera);
		universe.compile();
	}
	
	@Override
	public void update(RWTVirtualController virtualController, long interval) {
		progress(virtualController, interval);
		camera.adjust(interval);
	}
	
	protected RWTContainer createRWTContainer() {
		return new RWTContainer() {
				@Override
				public void build(GraphicsConfiguration gc) {				
					RWTCanvas3D canvas;
					if (gc != null) {
						canvas = new RWTCanvas3D(gc);
					} else {
						canvas = new RWTCanvas3D();
					}
					canvas.setRelativePosition(0.0f, 0.0f);
					canvas.setRelativeSize(1.0f, 1.0f);
					addCanvas(canvas);
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
	}
}
