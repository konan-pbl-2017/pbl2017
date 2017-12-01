package framework.gameMain;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.game2D.Sprite;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.scenario.IWorld;
import framework.scenario.ScenarioManager;
import framework.view3D.Camera3D;

public abstract class SimpleRolePlayingGame extends SimpleMapGame implements IWorld  {
	protected BaseScenarioGameContainer container;
	protected ScenarioManager scenario;
	protected RWTFrame3D frame;
	private Universe subUniverse;
	private Camera3D subCamera;
	private double subViewRangeWidth;
	private double subViewRangeHeight;	
	private Sprite subCenter;
	protected BaseScenarioGameContainer subContainer = null;
	private double subCenterX;
	private double subCenterY;
	
	@Override
	public void dialogOpen() {
		container.dialogOpen();
	}

	@Override
	public void dialogClose() {
		container.dialogClose();
	}

	@Override
	public void dialogMessage(String message) {
		container.dialogMessage(message);
	}
	
	@Override
	public void showOption(int n, String option) {
	}

	@Override
	public boolean isDialogOpen() {
		return container.isDialogOpen();
	}
	
	public void setScenario(String scenarioFile) {
		scenario = new ScenarioManager(scenarioFile, this);
		if (container != null) container.setScenario(scenario);
	}
	
	/**
	 * 戦闘画面の作成
	 */
	abstract public BaseScenarioGameContainer createSubRWTContainer();

	/**
	 * 戦闘モードの初期化
	 * @param universe
	 */
	abstract public void subInit(Universe universe);
	
	/**
	 * 戦闘画面においてカメラが見る範囲をwidthとheightで指定する
	 * @param width
	 * @param height
	 */
	protected void setSubViewRange(double width, double height) {
		subViewRangeWidth = width;
		subViewRangeHeight = height;
		updateSubViewArea();
	}
	
	protected void setSubCenter(Sprite center) {
		this.subCenter = center;
		subCenterX = center.getPosition().getX();
		subCenterY = center.getPosition().getY();
		updateSubViewArea();
		subCamera.addTarget(new Position3D(subCenterX, subCenterY, 0.0));
	}
	
	private void updateSubViewArea() {
		Transform3D t = new Transform3D();
		t.ortho(-subViewRangeWidth + subCenterX, subCenterX, 
				-subViewRangeHeight + subCenterY, subCenterY, 0.1, 10000);
		subCamera.getView().setCompatibilityModeEnable(true);
		subCamera.getView().setVpcToEc(t);
	}

	/**
	 * 戦闘画面への切り替え
	 */
	protected void changeToSubContainer() {
		if (subContainer == null) {
			subContainer = createSubRWTContainer();
			subContainer.setScenario(scenario);
			frame.setContentPane(subContainer);
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
			subContainer.build(gc);
	
			subUniverse = new Universe();
			subCamera = new Camera3D(subUniverse);
			subCamera.setSideView();
			subCamera.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
			setSubViewRange(30, 30);
			
			// 光源の作成
			// 環境光
			AmbientLight amblight = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
			amblight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
			subUniverse.placeLight(amblight);

			// 平行光源
			DirectionalLight dirlight = new DirectionalLight(true, // 光のON/OFF
					new Color3f(1.0f, 1.0f, 1.0f), // 光の色
					new Vector3f(0.0f, -1.0f, -0.5f) // 光の方向ベクトル
			);
			dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
			subUniverse.placeLight(dirlight);
			
			subInit(subUniverse);
			subContainer.getPrimaryRWTCanvas3D().attachCamera(subCamera);
			subUniverse.compile();
			
			subCamera.adjust(0L);
		} else {
			frame.setContentPane(subContainer);			
			subContainer.repaint();
		}
		subContainer.setScenario(scenario);
	}

	/**
	 * マップ画面への切り替え
	 */
	protected void changeToMainContainer() {
		frame.setContentPane(container);	
		container.repaint();
		container.setScenario(scenario);
	}
}
