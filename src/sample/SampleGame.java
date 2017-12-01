package sample;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;


import framework.RWT.*;
import framework.gameMain.*;
import framework.model3D.*;
import framework.animation.*;
import framework.physics.*;
import framework.view3D.Camera3D;


public class SampleGame extends SimpleGame {
	OvergroundActor pocha;
	Ground stage;

	@Override
	public void init(Universe universe, Camera3D camera) {
		//環境光
		AmbientLight amblight = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
		
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
        
		Object3D pochaBody = ModelFactory.loadModel("data\\Sample\\Character\\pocha\\pocha.wrl").createObject();
		Animation3D pochaAnimation = AnimationFactory.loadAnimation("data\\Sample\\Character\\pocha\\walk.wrl");
		pocha = new OvergroundActor(pochaBody, pochaAnimation);
		pocha.setPosition(new Position3D(3.87, 0.0, 24.0));
		universe.placeUnremovable(pocha);
		
		Object3D stageObj = ModelFactory.loadModel("data\\Sample\\Stage\\konan\\konan3.wrl").createObject();
		stage = new Ground(stageObj);
		universe.placeUnremovable(stage);
		
		camera.setViewPoint(pocha.getPosition().add(0.0, 1.5, 0.0));
		camera.setViewLine(pocha.getDirection());
		camera.setFieldOfView(1.5);
	}

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D f = new RWTFrame3D();
		f.setSize(800, 600);
		f.setTitle("Sample");
		return f;
	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
		Velocity3D curV = pocha.getVelocity();
		if (virtualController.isKeyDown(0, RWTVirtualController.LEFT)) {
			pocha.rotY(0.02 * (double)(interval / 15.0));
		} else if (virtualController.isKeyDown(0, RWTVirtualController.RIGHT)) {
			pocha.rotY(-0.02 * (double)(interval / 15.0));
		}
		if (virtualController.isKeyDown(0, RWTVirtualController.DOWN)) {
			curV.setX(pocha.getDirection().getX() * 5.0);
			curV.setZ(pocha.getDirection().getZ() * 5.0);
			pocha.setVelocity(curV);						
		} else {
			curV.setX(0.0);
			curV.setZ(0.0);
			pocha.setVelocity(curV);						
		}
		if (virtualController.isKeyDown(0, RWTVirtualController.UP)) {
			if (pocha.isOnGround()) {
				curV.setY(10.0);
				pocha.setVelocity(curV);						
			}
		} 
		pocha.motion(interval, stage);
		camera.setViewPoint(pocha.getPosition().add(0.0, 1.5, 0.0));
		camera.setViewLine(pocha.getDirection());
	}

}
