package framework.test;

import java.awt.Component;

import javax.media.j3d.BoundingSphere;
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
import framework.model3D.CollisionResult;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.Force3D;
import framework.physics.Ground;
import framework.physics.PhysicalSystem;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;
import framework.view3D.Camera3D;

public class TestPhysics2 {
	static final double GRAVITY = 9.8;

	public TestPhysics2() {
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
		solid.apply(new Position3D(0.0, 0.0, 0.0), false);
		Solid3D solid2 = new Solid3D(obj2);
		solid2.apply(new Position3D(0.7, 4.0, 0.0), false);
		universe.placeUnremovable(solid);
		universe.placeUnremovable(solid2);

		// ライトの設定
		DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
				1.0f, 1.0f), new Vector3f(0.0f, 0.0f, -1.0f));
		light.setInfluencingBounds(new BoundingSphere(new Point3d(), 500.0));
		universe.placeLight(light);

		frame.setSize(720, 480);
		frame.setVisible(true);

		// ステージの設定
		Object3D stageObject = ModelFactory.loadModel("data//floor4.3ds").createObject();
		Ground stageGround = new Ground(stageObject);
		universe.placeUnremovable(stageGround);

		// 表示
		universe.compile();

		// カメラの設定
		Camera3D camera = new Camera3D(universe);
		camera.addTarget(solid);
		camera.addTarget(solid2);		
		canvas.attachCamera(camera);

		PhysicalSystem p_solid = new PhysicalSystem();
		p_solid.add(solid);
		p_solid.add(solid2);
		CollisionResult cr = new CollisionResult();
		cr = PhysicsUtility.checkCollision(solid, null, solid2, null);
		int id = 1;
		long time = 0;
		for (;;) {
			try {
				Thread.sleep(1);
				camera.adjust(0);
				// w.move(17, stageObject);
				Force3D f = Force3D.ZERO;
				// if(solid.getVelocity().getY()<0.1 &&
				// solid.getVelocity().getY()>-0.1){
				// System.out.println("↑"+time+","+solid.getPosition3D().getY());
				// }
				// if(solid.getPosition3D().getY()<=-4.5){
				// System.out.println("↓"+time+","+solid.getPosition3D().getY()+","+solid.getVelocity().getY());
				// }
				if (cr != null) {
					p_solid.motion(id, 1, f, cr.collisionPoint, stageGround);
				} else {
					p_solid.motion(id, 1, f, p_solid.objects.get(id)
							.getGravityCenter(), stageGround);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time++;
		}
	}

	public static void main(String[] args) {
		new TestPhysics2();
	}
}
