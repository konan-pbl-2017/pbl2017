package framework.test;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Texture;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.image.TextureLoader;

import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.gameMain.MultiViewGame;
import framework.gameMain.OvergroundActor;
import framework.model3D.BackgroundBox;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.Velocity3D;
import framework.view3D.Camera3D;

public class TestMultiView extends MultiViewGame {
	OvergroundActor pocha1;
	OvergroundActor pocha2;
	Ground stage;
	Velocity3D pocha1Direction = new Velocity3D(5.0, 0.0, 0.0);
	Velocity3D pocha2Direction = new Velocity3D(5.0, 0.0, 0.0);

	@Override
	public void init(Universe universe, Camera3D camera1, Camera3D camera2) {
		//環境光
		AmbientLight amblight = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
		
		amblight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(amblight);

		//平行光源
        DirectionalLight dirlight = new DirectionalLight(
        		true,                           //光のON/OFF
                new Color3f(1.0f, 1.0f, 1.0f),  //光の色
                new Vector3f(0.0f, -1.0f, -0.5f) //光の方向ベクトル
        );
        dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
        universe.placeLight(dirlight);
        
		Model3D pochaModel = ModelFactory.loadModel("data\\pocha\\pocha.wrl", false, true);
		Animation3D pochaAnimation = AnimationFactory.loadAnimation("data\\pocha\\walk.wrl");

		Object3D pochaBody1 = pochaModel.createObject();
		pocha1 = new OvergroundActor(pochaBody1, pochaAnimation);
		pocha1.body.apply(new Position3D(3.0, 0.0, 0.0), false);
		universe.placeUnremovable(pocha1);

		Object3D pochaBody2 = pochaModel.createObject();
		pocha2 = new OvergroundActor(pochaBody2, pochaAnimation);
		pocha2.body.apply(new Position3D(-3.0, 0.0, 0.0), false);
		universe.placeUnremovable(pocha2);
		
		camera1.setViewPoint(pocha1.getPosition().add(0.0, 0.5, 0.0));
		camera1.addTarget(pocha1);
		camera1.setFieldOfView(1.5);		
		
		camera2.setViewPoint(pocha2.getPosition().add(0.0, 0.5, 0.0));
		camera2.addTarget(pocha2);
		camera2.setFieldOfView(1.5);		
		
		Object3D stageObj = ModelFactory.loadModel("data\\stage3\\stage3.wrl").createObject();
		stage = new Ground(stageObj);
		universe.placeUnremovable(stage);
	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
		Velocity3D curV = pocha1.getVelocity();
		if (virtualController.isKeyDown(0, RWTVirtualController.LEFT)) {
			pocha1Direction.rotY(0.02 * (double)(interval / 15.0));
			Quaternion3D curQuat = pocha1.body.getQuaternion();
			curQuat.add(new AxisAngle4d(0.0, 1.0, 0.0, 0.02 * (double)(interval / 15.0)));
			pocha1.body.apply(curQuat, false);
		} else if (virtualController.isKeyDown(0, RWTVirtualController.RIGHT)) {
			pocha1Direction.rotY(-0.02 * (double)(interval / 15.0));
			Quaternion3D curQuat = pocha1.body.getQuaternion();
			curQuat.add(new AxisAngle4d(0.0, 1.0, 0.0, -0.02 * (double)(interval / 15.0)));
			pocha1.body.apply(curQuat, false);
		}
		if (virtualController.isKeyDown(0, RWTVirtualController.DOWN)) {
			curV.setX(pocha1Direction.getX());
			curV.setZ(pocha1Direction.getZ());
			pocha1.body.apply(curV, false);						
		} else {
			curV.setX(0.0);
			curV.setZ(0.0);
			pocha1.body.apply(curV, false);						
		}
		if (virtualController.isKeyDown(0, RWTVirtualController.UP)) {
			if (pocha1.isOnGround()) {
				curV.setY(10.0);
				pocha1.body.apply(curV, false);
			}
		} 
		pocha1.motion(interval, stage);
		camera1.setViewPoint(pocha1.getPosition().add(0.0, 1.5, 0.0));
		camera1.setViewLine(pocha1Direction.getVector3d());
		
		curV = pocha2.getVelocity();
		if (virtualController.isKeyDown(1, RWTVirtualController.LEFT)) {
			pocha2Direction.rotY(0.02 * (double)(interval / 15.0));
			Quaternion3D curQuat = pocha2.body.getQuaternion();
			curQuat.add(new AxisAngle4d(0.0, 1.0, 0.0, 0.02 * (double)(interval / 15.0)));
			pocha2.body.apply(curQuat, false);
		} else if (virtualController.isKeyDown(1, RWTVirtualController.RIGHT)) {
			pocha2Direction.rotY(-0.02 * (double)(interval / 15.0));
			Quaternion3D curQuat = pocha2.body.getQuaternion();
			curQuat.add(new AxisAngle4d(0.0, 1.0, 0.0, -0.02 * (double)(interval / 15.0)));
			pocha2.body.apply(curQuat, false);
		}
		if (virtualController.isKeyDown(1, RWTVirtualController.DOWN)) {
			curV.setX(pocha2Direction.getX());
			curV.setZ(pocha2Direction.getZ());
			pocha2.body.apply(curV, false);						
		} else {
			curV.setX(0.0);
			curV.setZ(0.0);
			pocha2.body.apply(curV, false);						
		}
		if (virtualController.isKeyDown(1, RWTVirtualController.UP)) {
			if (pocha2.isOnGround()) {
				curV.setY(10.0);
				pocha2.body.apply(curV, false);
			}
		} 
		pocha2.motion(interval, stage);
		camera2.setViewPoint(pocha2.getPosition().add(0.0, 1.5, 0.0));
		camera2.setViewLine(pocha2Direction.getVector3d());
	}

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D f = new RWTFrame3D();
		f.setSize(800, 600);
		f.setTitle("Multi View Test");
		return f;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestMultiView game = new TestMultiView();
		game.start();
	}
}
