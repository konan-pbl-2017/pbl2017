package framework.test;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Clip;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import fight3D.Character;
import fight3D.CharacterManager;
import fight3D.Player;
import fight3D.Stage;
import fight3D.StageManager;
import fight3D.Weapon;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.Velocity3D;
import framework.view3D.Camera3D;

public class TestPhysics {

	public TestPhysics() {
		// TODO Auto-generated constructor stub
		RWTFrame3D frame = new RWTFrame3D();
		RWTCanvas3D canvas = new RWTCanvas3D();
		frame.add(canvas);

		Model3D model = ModelFactory.loadModel("data\\cube2.3ds");
		Object3D obj = model.createObject();
		Transform3D s = new Transform3D();
		obj.children[0].scale.setTransform(s);

		Universe universe = new Universe();
		
		DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
				1.0f, 1.0f), new Vector3f(0.0f, 0.0f, -1.0f));
		light.setInfluencingBounds(new BoundingSphere(new Point3d(), 500.0));
		universe.placeLight(light);

		universe.placeUnremovable(obj);

		frame.setSize(720, 480);
		frame.setVisible(true);

		// プレイヤーの設定
		Character c = CharacterManager.getInstance().getCharacter(0);
		Player p = new Player(c);
		universe.placeUnremovable(p);

		// 飛び道具の設定
		Weapon w = new Weapon(c.getWeaponModel(), p, 0);
		universe.placeUnremovable(w);
		w.body.apply(new Velocity3D(-5.0, 0.0, 0.0), false);
		w.body.apply(new Quaternion3D(), false);

		// ステージの設定
		Stage stage = StageManager.getInstance().getStage(0);
		Object3D stageObject = stage.getModel().createObject();
		Ground stageGround = new Ground(stageObject);
		universe.placeUnremovable(stageGround);

		// 表示
		universe.compile();

		// カメラの設定
		Camera3D camera = new Camera3D(universe);
		camera.addTarget(p.body);
		camera.addTarget(w.body);
		canvas.attachCamera(camera);

		// Solid3D solid = new Solid3D(obj);
		Position3D applicationPoint = new Position3D(10, 10, 10);
		for (;;) {
			try {
				Thread.sleep(17);
				camera.adjust(17);
				w.motion(17, stageGround);
				// Force3D f = PhysicsFacade.getGravity(solid);
				// solid.move(17, f, applicationPoint);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new TestPhysics();
	}
}
