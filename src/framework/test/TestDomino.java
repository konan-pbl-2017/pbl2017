package framework.test;

import java.awt.Component;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Clip;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import fight3D.Stage;
import fight3D.StageManager;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.AngularVelocity3D;
import framework.physics.Force3D;
import framework.physics.Ground;
import framework.physics.PhysicalSystem;
import framework.physics.Solid3D;
import framework.view3D.Camera3D;

public class TestDomino {
	static final double GRAVITY = 9.8;

	public TestDomino() {
		// TODO Auto-generated constructor stub
		RWTFrame3D frame = new RWTFrame3D();
		RWTCanvas3D canvas = new RWTCanvas3D();
		frame.add(canvas);

		Model3D model = ModelFactory.loadModel("data\\cube2.3ds");
		Object3D obj = model.createObject();
		Object3D obj2 = model.createObject();
		Transform3D s = new Transform3D();
		obj.children[0].scale.setTransform(s);
		Transform3D s2 = new Transform3D();
		obj2.children[0].scale.setTransform(s2);

		// オブジェクトの設定
		Universe universe = new Universe();
				
		Solid3D solid = new Solid3D(obj);
		Solid3D solid2 = new Solid3D(obj);
		Solid3D solid3 = new Solid3D(obj);
		Solid3D solid4 = new Solid3D(obj);
		Solid3D solid5 = new Solid3D(obj);
		Solid3D solid6 = new Solid3D(obj);
		Solid3D solid7 = new Solid3D(obj);
		Solid3D solid8 = new Solid3D(obj);
		Solid3D solid9 = new Solid3D(obj);

		solid.apply(new Position3D(-2, -4.0, 0.0), false);
		solid3.apply(new Position3D(-1.2, -4.0, 0.0), false);
		solid4.apply(new Position3D(-0.4, -4.0, 0.0), false);
		solid5.apply(new Position3D(0.4, -4.0, 0.0), false);
		solid6.apply(new Position3D(1.2, -4.0, 0.0), false);
		solid7.apply(new Position3D(2.0, -4.0, 0.0), false);
		solid8.apply(new Position3D(2.8, -4.0, 0.0), false);
		solid9.apply(new Position3D(3.6, -4.0, 0.0), false);
		solid2.apply(new Position3D(4.2, -4.0, 0.0), false);
		solid.scale(0.5, 2.0, 1.0);
		solid2.scale(0.5, 2.0, 1.0);
		solid3.scale(0.5, 2.0, 1.0);
		solid4.scale(0.5, 2.0, 1.0);
		solid5.scale(0.5, 2.0, 1.0);
		solid6.scale(0.5, 2.0, 1.0);
		solid7.scale(0.5, 2.0, 1.0);
		solid8.scale(0.5, 2.0, 1.0);
		solid9.scale(0.5, 2.0, 1.0);

		solid2.apply(new AngularVelocity3D(0.0, 0.0, 0.005), false);
		universe.placeUnremovable(solid);
		universe.placeUnremovable(solid2);
		universe.placeUnremovable(solid3);
		universe.placeUnremovable(solid4);
		universe.placeUnremovable(solid5);
		universe.placeUnremovable(solid6);
		universe.placeUnremovable(solid7);
		universe.placeUnremovable(solid8);
		universe.placeUnremovable(solid9);

		// ライトの設定
		DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
				1.0f, 1.0f), new Vector3f(0.0f, 0.0f, -1.0f));
		light.setInfluencingBounds(new BoundingSphere(new Point3d(), 500.0));
		universe.placeLight(light);

		frame.setSize(720, 480);
		frame.setVisible(true);

		// ステージの設定
		Stage stage = StageManager.getInstance().getStage(1);
		Object3D stageObject = stage.getModel().createObject();
		Ground stageGround = new Ground(stageObject);
		universe.placeUnremovable(stageGround);

		// カメラの設定
		Camera3D camera = new Camera3D(universe);
		canvas.attachCamera(camera);
		camera.addTarget(solid);
		camera.addTarget(solid2);
		camera.addTarget(solid3);
		camera.addTarget(solid4);
		camera.addTarget(solid5);
		camera.addTarget(solid6);
		camera.addTarget(solid7);
		camera.addTarget(solid8);
		camera.addTarget(solid9);

		// 表示
		universe.compile();

		PhysicalSystem p_solid = new PhysicalSystem();
		p_solid.add(solid);
		p_solid.add(solid2);
		p_solid.add(solid3);
		p_solid.add(solid4);
		p_solid.add(solid5);
		p_solid.add(solid6);
		p_solid.add(solid7);
		p_solid.add(solid8);
		p_solid.add(solid9);

		// CollisionResult cr = new CollisionResult();
		// cr = PhysicsFacade.checkColision(solid, solid, solid2, solid2);

		int id = 1;
		long time = 0;
		for (;;) {
			try {
				Thread.sleep(0);
				camera.adjust(0);
				// w.move(17, stageObject);
				Force3D f = Force3D.ZERO;
				if (solid.getPosition3D().getY() > -0.1) {
					// System.out.println(time+","+solid.getPosition3D().getY());

				}
				p_solid.motion(id, 1, f, p_solid.objects.get(id)
						.getGravityCenter(), stageGround);
				// if(cr != null){
				// p_solid.move(id, 1, f, cr.collisionPoint, stageObject);
				// }
				// else{
				// p_solid.move(id, 1, f,
				// p_solid.objects.get(id).getGravityCenter(), stageObject);
				// )
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time++;
		}
	}

	public static void main(String[] args) {
		new TestDomino();
	}
}
