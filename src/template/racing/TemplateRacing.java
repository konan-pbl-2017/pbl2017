package template.racing;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.swing.ImageIcon;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

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
import framework.model3D.Terrain3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.Solid3D;
import framework.physics.Velocity3D;
import framework.test.TestMultiView;
import framework.view3D.Camera3D;

public class TemplateRacing extends MultiViewGame {
	Automobile car1;
	Automobile car2;
	Ground ground;

	@Override
	public void init(Universe universe, Camera3D camera1, Camera3D camera2) {
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
		dirlight
				.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
		universe.placeLight(dirlight);

		Model3D carModel = ModelFactory.loadModel("data\\TemplateRacing\\Car\\GTR\\GTR.wrl",
				false, true);

		Object3D carBody1 = carModel.createObject();
		car1 = new Automobile(new Solid3D(carBody1), null);
		car1.body.apply(new Position3D(200.0, 3.0, 50.0), false);
		universe.placeUnremovable(car1);

		Object3D carBody2 = carModel.createObject();
		car2 = new Automobile(new Solid3D(carBody2), null);
		car2.body.apply(new Position3D(-150.0, 3.0, 50.0), false);
		universe.placeUnremovable(car2);

		camera1.addTarget(car1);
		camera1.setViewLine(car1.getDirection());
		camera1.setCameraBack(new Vector3d(0.0, 3.0, 5.0));
		camera1.setFieldOfView(1.5);

		camera2.addTarget(car2);
		camera2.setViewLine(car2.getDirection());
		camera2.setCameraBack(new Vector3d(0.0, 3.0, 5.0));
		camera2.setFieldOfView(1.5);

		Object3D stageObj =
		ModelFactory.loadModel("data\\TemplateRacing\\RoadStage\\RoadStage.WRL").createObject();
		ground = new Ground(stageObj);
		universe.placeUnremovable(ground);

		buildSkyBox(universe);
	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
		// TODO Auto-generated method stub
		if (virtualController.isKeyDown(0, RWTVirtualController.LEFT)) {
			car1.turnLeft(interval);
		} else if (virtualController
				.isKeyDown(0, RWTVirtualController.RIGHT)) {
			car1.turnRight(interval);
		} else {
			car1.steeringSelfCentering(interval);
		}
		if (virtualController.isKeyDown(0, RWTVirtualController.DOWN)) {
			car1.brake();
		} else if (virtualController.isKeyDown(0, RWTVirtualController.UP)) {
			car1.accelrate();
		} else {
			car1.engineBrake(interval);
		}
		car1.motion(interval, ground);
		camera1.setViewLine(car1.getDirection());

		if (virtualController.isKeyDown(1, RWTVirtualController.LEFT)) {
			car2.turnLeft(interval);
		} else if (virtualController
				.isKeyDown(1, RWTVirtualController.RIGHT)) {
			car2.turnRight(interval);
		} else {
			car2.steeringSelfCentering(interval);			
		}
		if (virtualController.isKeyDown(1, RWTVirtualController.DOWN)) {
			car2.brake();
		} else if (virtualController.isKeyDown(1, RWTVirtualController.UP)) {
			car2.accelrate();
		} else {
			car2.engineBrake(interval);			
		}
		car2.motion(interval, ground);
		camera2.setViewLine(car2.getDirection());
	}

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D f = new RWTFrame3D();
		f.setSize(800, 600);
		f.setTitle("RadishRacing");
//		f.setShadowCasting(true);
		return f;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TemplateRacing game = new TemplateRacing();
		game.start();

	}

	private void buildSkyBox(Universe universe) {
//		TextureLoader loaderTop = new TextureLoader("data\\texture\\top.jpg",
//				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
//		Texture textureTop = loaderTop.getTexture();
//		TextureLoader loaderBottom = new TextureLoader(
//				"data\\texture\\bottom.jpg", TextureLoader.BY_REFERENCE
//						| TextureLoader.Y_UP, null);
//		Texture textureBottom = loaderBottom.getTexture();
//		TextureLoader loaderNorth = new TextureLoader(
//				"data\\texture\\north.jpg", TextureLoader.BY_REFERENCE
//						| TextureLoader.Y_UP, null);
//		Texture textureNorth = loaderNorth.getTexture();
//		TextureLoader loaderSouth = new TextureLoader(
//				"data\\texture\\south.jpg", TextureLoader.BY_REFERENCE
//						| TextureLoader.Y_UP, null);
//		Texture textureSouth = loaderSouth.getTexture();
//		TextureLoader loaderWest = new TextureLoader("data\\texture\\west.jpg",
//				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
//		Texture textureWest = loaderWest.getTexture();
//		TextureLoader loaderEast = new TextureLoader("data\\texture\\east.jpg",
//				TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
//		Texture textureEast = loaderEast.getTexture();
//
//		BackgroundBox background = new BackgroundBox(textureNorth, textureWest,
//				textureSouth, textureEast, textureTop, textureBottom);
//		BoundingSphere bs = new BoundingSphere();
//		bs.setRadius(1000);
//		background.setApplicationBounds(bs);
//		universe.place(background);
	}
}
