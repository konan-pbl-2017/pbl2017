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


import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.model3D.CollisionResult;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;
import framework.view3D.Camera3D;

public class TestMathmatics {

	public TestMathmatics() {
		// TODO Auto-generated constructor stub
		RWTFrame3D frame = new RWTFrame3D();
		RWTCanvas3D canvas = new RWTCanvas3D();
		frame.add(canvas);

		Model3D model = ModelFactory.loadModel("data\\cube2.3ds");
		Object3D obj1 = model.createObject();
		Object3D obj2 = model.createObject();
		Transform3D s1 = new Transform3D();
		Transform3D s2 = new Transform3D();
		obj1.children[0].scale.setTransform(s1);
		obj2.children[0].scale.setTransform(s2);

		Universe universe = new Universe();
		
		DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
				1.0f, 1.0f), new Vector3f(0.0f, 0.0f, -1.0f));
		light.setInfluencingBounds(new BoundingSphere(new Point3d(), 500.0));
		universe.placeUnremovable(light);

		// obj1.setPosition(new Position3D(3.0,0.0,0.0));
		// obj2.setPosition(new Position3D(-3.0,0.0,0.0));
		Solid3D sol1 = new Solid3D(obj1);
		Solid3D sol2 = new Solid3D(obj2);
		sol1.apply(new Position3D(1.5, 1.5, 0.0), false);
		universe.placeUnremovable(sol1);
		universe.placeUnremovable(sol2);
		universe.compile();

		Camera3D camera = new Camera3D(universe);
		camera.setViewPoint(new Position3D(0.0, 0.0, 10.0));
		camera.adjust(0L);
		canvas.attachCamera(camera);

		CollisionResult cr = PhysicsUtility
				.checkCollision(sol1, null, sol2, null);

		frame.setSize(720, 480);
		frame.setVisible(true);
		if (cr != null) {
			System.out.println("Yes!!");
		} else {
			System.out.println("No!!");
		}
		// for (;;) {
		//        	
		// }
	}

	@SuppressWarnings("serial")
	public static void main(String[] args) {
		new TestMathmatics();
	}
}
