package framework.test;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Clip;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.model3D.CollisionResult;
import framework.model3D.GeometryUtility;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;
import framework.view3D.Camera3D;

public class TestMathmatics2 implements KeyListener {

	Solid3D sol1 = null;
	Solid3D sol2 = null;
	Ground stageGround = null;

	// コンストラクタ
	@SuppressWarnings("serial")
	public TestMathmatics2() {
		// TODO Auto-generated constructor stub
		RWTFrame3D frame = new RWTFrame3D();
		RWTCanvas3D canvas = new RWTCanvas3D();
		frame.add(canvas);

		frame.addKeyListener(this);

		Model3D model1 = ModelFactory.loadModel("data\\cube2.3ds");
		Model3D model2 = ModelFactory.loadModel("data\\cube2.3ds");
		Object3D obj1 = model1.createObject();
		Object3D obj2 = model2.createObject();
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

		sol1 = new Solid3D(obj1);
		sol2 = new Solid3D(obj2);
		sol1.apply(new Position3D(-2.0, 0.0, 0.0), false);
		sol2.apply(new Position3D(0.0, 0.0, 0.0), false);
		stageGround = new Ground(sol2);
		universe.placeUnremovable(sol1);
		universe.placeUnremovable(stageGround);
		universe.compile();
		
		Camera3D camera = new Camera3D(universe);
		camera.setViewPoint(new Position3D(0.0, -3.0, 30.0));
		camera.adjust(0L);
		canvas.attachCamera(camera);

		frame.setSize(720, 480);
		frame.setVisible(true);
		frame.requestFocus();

		// for (;;) {
		//        	
		// }
	}

	public static void main(String[] args) {
		boolean f = GeometryUtility.inside(new Vector3d(0.0, 0.0, 1.0),
				new Vector4d(0.0, 0.0, -1.0, 0.0));
		System.out.println(f);
		new TestMathmatics2();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// sol2.setUndoMark();
		Position3D p = sol2.getPosition3D();
		switch (e.getKeyCode()) {
		case 65:
			p.setX(p.getX() - 0.1);
			sol2.apply(p, false);
			break;
		case 68:
			p.setX(p.getX() + 0.1);
			sol2.apply(p, false);
			break;
		case 87:
			p.setY(p.getY() + 0.1);
			sol2.apply(p, false);
			break;
		case 88:
			p.setY(p.getY() - 0.1);
			sol2.apply(p, false);
			break;
		}
		// CollisionResult cr = PhysicsFacade.isOnground(sol1, sol2);
		CollisionResult cr = PhysicsUtility.doesIntersect(sol1, stageGround);
		// CollisionResult cr = PhysicsFacade.checkColision(sol1, sol1, sol2,
		// sol2);
		if (cr != null) {
			System.out.println("TestMathmatics2 Yes!!");
		} else {
			System.out.println("TestMathmatics2 No!!");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
