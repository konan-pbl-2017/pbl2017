package template.RPG;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.gameMain.BaseScenarioGameContainer;
import framework.gameMain.SimpleScenarioGame;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Velocity3D;
import framework.scenario.Event;
import framework.scenario.IWorld;
import framework.scenario.ScenarioManager;
import framework.scenario.ScenarioState;
import framework.view3D.Camera3D;

/**
 * シナリオ駆動型ゲームのためのテンプレート
 * @author 新田直也
 *
 */
public class TemplateScenarioGame extends SimpleScenarioGame implements IWorld {
	private Player player;								// プレイヤー
	private Player king;								// 王様
	private Ground stage;								// ステージ
	private RWTFrame3D frame;
	
	// サブ画面用
	private Universe subUniverse;
	private Camera3D subCamera;
	private BaseScenarioGameContainer subContainer = null;
	
	@Override
	public void init(Universe universe, Camera3D camera) {
		// 環境光
		AmbientLight amblight = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));

		amblight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(amblight);

		// 平行光源
		DirectionalLight dirlight = new DirectionalLight(true, // 光のON/OFF
				new Color3f(1.0f, 1.0f, 1.0f), // 光の色
				new Vector3f(0.0f, -1.0f, -0.5f) // 光の方向ベクトル
		);
		dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(dirlight);

		// キャラクターの3Dデータを読み込み配置する
		Object3D pochaBody = ModelFactory.loadModel("data\\TemplateRPG\\Character\\pocha\\pocha.wrl").createObject();
		Animation3D pochaAnimation = AnimationFactory.loadAnimation("data\\TemplateRPG\\Character\\pocha\\walk.wrl");
		player = new Player(pochaBody, pochaAnimation);
		player.setPosition(new Position3D(-77.68, 0.0, -1.9));
		universe.placeUnremovable(player);
		
		// 王様を配置（キャラクターは流用）
		king = new Player(pochaBody, pochaAnimation);
		king.setPosition(new Position3D(3.87, -11.92, 21.39));
		universe.placeUnremovable(king);

		// ステージの3Dデータを読み込み配置する
		Object3D stageObj = ModelFactory.loadModel("data\\konan\\konan3.wrl").createObject();
		stage = new Ground(stageObj);
		universe.placeUnremovable(stage);
		
		// カメラの設定（俯瞰視点にする）
		camera.addTarget(player);
		camera.setViewLine(new Vector3d(0.0, -10.0, -10.0));
		camera.setFieldOfView(1.5);
	}
	
	/**
	 * 戦闘画面の初期化
	 * @param universe
	 * @param camera
	 */
	private void subInit(Universe universe, Camera3D camera) {
		// 環境光
		AmbientLight amblight = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));

		amblight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(amblight);

		// 平行光源
		DirectionalLight dirlight = new DirectionalLight(true, // 光のON/OFF
				new Color3f(1.0f, 1.0f, 1.0f), // 光の色
				new Vector3f(0.0f, -1.0f, -0.5f) // 光の方向ベクトル
		);
		dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(dirlight);
		
		// 敵キャラクターの3Dデータを読み込み配置する
		Object3D enemy = ModelFactory.loadModel("data\\pocha\\pocha.wrl").createObject();
		enemy.apply(new Position3D(0.0, 0.0, 0.0), false);
		enemy.apply(new Quaternion3D(0.0, 1.0, 0.0, Math.PI / 2.0), false);
		universe.placeUnremovable(enemy);
		
		// カメラの設定（横視点にする）
		camera.addTarget(enemy.getPosition3D().add(0.0, 1.0, 0.0));
		camera.setViewLine(new Vector3d(0.0, -2.0, 10.0));
		camera.setCameraBack(new Vector3d(0.0, 0.0, 2.0));
		camera.setFieldOfView(1.5);
	}

	@Override
	public RWTFrame3D createFrame3D() {
		frame = new RWTFrame3D();
		frame.setSize(800, 600);
		frame.setTitle("Template for Role Playing Games");
		frame.setBackground(Color.BLACK);
		return frame;
	}
	
	@Override
	protected RWTContainer createRWTContainer() {
		scenario = new ScenarioManager("data\\TemplateRPG\\Scenario\\scenario2.xml", this);		// シナリオファイルの読み込み
		container = new ScenarioGameContainer(scenario);
		return container;
	}
	
	// 戦闘用画面の作成
	protected RWTContainer createSubRWTContainer() {
		subContainer = new FightContainer(scenario);
		return subContainer;
	}
	
	// 戦闘用画面への切り替え
	protected void changeToSubContainer() {
		if (subContainer == null) {
			subContainer = (BaseScenarioGameContainer)createSubRWTContainer();		
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
			subInit(subUniverse, subCamera);
			subCamera.adjust(0L);
			subContainer.getPrimaryRWTCanvas3D().attachCamera(subCamera);
			subUniverse.compile();
		} else {
			frame.setContentPane(subContainer);			
			subContainer.repaint();
		}
	}

	// 戦闘用画面からメイン画面への切り替え
	protected void changeToMainContainer() {
		frame.setContentPane(container);	
		container.repaint();
	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
		// 以下にキー操作のプログラムを書く
		Velocity3D curV = player.getVelocity();						
		if (virtualController.isKeyDown(0, RWTVirtualController.LEFT)) {
			player.setDirection(new Vector3d(-1.0, 0.0, 0.0));						
			curV.setX(-10.0);
			curV.setZ(0.0);
		} else if (virtualController.isKeyDown(0, RWTVirtualController.RIGHT)) {
			player.setDirection(new Vector3d(1.0, 0.0, 0.0));						
			curV.setX(10.0);
			curV.setZ(0.0);
		} else if (virtualController.isKeyDown(0, RWTVirtualController.UP)) {
			player.setDirection(new Vector3d(0.0, 0.0, -1.0));						
			curV.setZ(-10.0);
			curV.setX(0.0);
		} else if (virtualController.isKeyDown(0, RWTVirtualController.DOWN)) {
			player.setDirection(new Vector3d(0.0, 0.0, 1.0));						
			curV.setZ(10.0);
			curV.setX(0.0);
		} else {
			curV.setX(0.0);
			curV.setZ(0.0);
		}
		player.setVelocity(curV);						
		
		// プレイヤーを動かす
		player.motion(interval, stage);
		
		// 衝突判定
		if (PhysicsUtility.checkCollision(player.body, null, king.body, null) != null) {
			// プレイヤーと王様がぶつかった場合
			scenario.fire("王様とぶつかる");	// 「王様とぶつかる」というイベントを発生する（シナリオが進む）
		}
	}

	@Override
	public void action(String action, Event event, ScenarioState nextState) {
		// シナリオ進行による世界への作用をここに書く
		if (action.equals("startFight")) {
			changeToSubContainer();
		} else if (action.equals("endFight")) {
			changeToMainContainer();
		}
	}
	
	/**
	 * ゲームのメイン
	 * @param args
	 */
	public static void main(String[] args) {
		TemplateScenarioGame game = new TemplateScenarioGame();
		game.start();		
	}
}
