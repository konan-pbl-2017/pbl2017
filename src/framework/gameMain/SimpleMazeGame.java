package framework.gameMain;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.game2D.Maze2D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.PhysicsUtility;
import framework.view3D.Camera3D;
import framework.view3D.CameraMap;

/**
 * 2D迷路ゲーム用のクラス
 * 
 * @author T.Kuno
 * 
 */

public abstract class SimpleMazeGame extends AbstractGame implements IGameState {
	protected Universe universe;
	protected CameraMap camera;

	protected int windowSizeWidth;
	protected int windowSizeHeight;

	protected double viewRangeWidth;
	protected double viewRangeHeight;
	
	private IGameState currentState = this;

	public abstract void init(Universe universe);

	public abstract void progress(RWTVirtualController virtualController, long interval);	
	
	public SimpleMazeGame() {
		PhysicsUtility.setGravityDirection(new Vector3d(0.0, 0.0, 0.0));
	}
	
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
		// TODO Auto-generated method stub
		RWTContainer container = createRWTContainer();
		frame.setContentPane(container);
		GraphicsConfiguration gc = null;
		if (frame.isShadowCasting()) {
			// 影を付ける場合
			// ステンシルバッファを使用する GraphicsConfiguration の生成
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfigTemplate3D gct3D = new GraphicsConfigTemplate3D();
			gct3D.setStencilSize(8);
			gc = gd.getBestConfiguration(gct3D);
		}
		container.build(gc);

		universe = new Universe();
		camera = new CameraMap(universe);

		setViewRange(30, 30);

		// 光源の作成
		// 環境光
		AmbientLight amblight = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
		amblight
				.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(amblight);

		// 平行光源
		DirectionalLight dirlight = new DirectionalLight(true, // 光のON/OFF
				new Color3f(1.0f, 1.0f, 1.0f), // 光の色
				new Vector3f(0.0f, -1.0f, -0.5f) // 光の方向ベクトル
		);
		dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(dirlight);

		init(universe);
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
	
	/////////////////////////////////////////////////////////
	//
	// 2Dゲームにおけるカメラの設定や見え方、その範囲などに関するメソッド
	//
	/////////////////////////////////////////////////////////
	
	/**
	 * カメラが見る範囲をwidthとheightで指定する
	 * @param width
	 * @param height
	 */
	protected void setViewRange(double width, double height) {
		viewRangeWidth = width;
		viewRangeHeight = height;
		Transform3D t = new Transform3D();
		t.ortho(-1.0 * width / 2.0, width / 2.0, -1.0 * height / 2.0,
				height / 2.0, 0.1, 10000);
		camera.getView().setCompatibilityModeEnable(true);
		camera.getView().setVpcToEc(t);
	}
	
	/**
	 * カメラが見ている範囲の幅を返す。
	 * 
	 * @return 見ている範囲の幅
	 */
	public double getViewRangeWidth() {
		return viewRangeWidth;
	}

	/**
	 * カメラが見ている範囲の幅をviewRangeWidthで設定する
	 * 
	 * @param viewRangeWidth
	 *            --- 新しいカメラが見ている範囲の幅
	 */
	public void setViewRangeWidth(double viewRangeWidth) {
		this.viewRangeWidth = viewRangeWidth;
	}

	/**
	 * カメラが見ている範囲の高さを返す。
	 * 
	 * @return 見ている範囲の高さ
	 */
	public double getViewRangeHeight() {
		return viewRangeHeight;
	}

	/**
	 * カメラが見ている範囲の高さをviewRangeWidthで設定する
	 * 
	 * @param viewRangeHeight
	 *            --- 新しいカメラが見ている範囲の高さ
	 */
	public void setViewRangeHeight(double viewRangeHeight) {
		this.viewRangeHeight = viewRangeHeight;
	}

}
