package framework.test;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Clip;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.image.TextureLoader;

import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.animation.PartAnimation;
import framework.animation.Pose3D;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

public class TestAnimation {

	/**
	 * @param args
	 */
	public TestAnimation() {
		// TODO Auto-generated method stub
		RWTFrame3D frame = new RWTFrame3D();
		RWTCanvas3D canvas = new RWTCanvas3D();
		frame.add(canvas);

//		Model3D model = ModelFactory.loadModel("data\\Head4.wrl");
		Model3D model = ModelFactory.loadModel("data\\pocha\\pocha.wrl");
		Object3D obj = model.createObject();
		Transform3D s = new Transform3D();
		((Object3D)obj.children[0]).scale.setTransform(s);

		// --- ここから、テクスチャ張り替えのテスト用コード
//		BufferedImage image1 = null, image2 = null, image3 = null;
//		try {
//			image1 = ImageIO.read(new File("data\\black.jpg"));
//			image2 = ImageIO.read(new File("data\\gray.jpg"));
//			image3 = ImageIO.read(new File("data\\white.jpg"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Object3D body = obj.getPart("Body");
//		Shape3D shape = body.children[0].getShape3D();
//		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
//		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
//		if (true) {
//			// Appearance以下、全部設定
//			Appearance ap = new Appearance();
//			Texture texture;
//			// テクスチャローダを使用 → うまくいった
//			TextureLoader loader = new TextureLoader("data\\gray.jpg", TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
//			texture = loader.getTexture();
//			texture.setCapability(Texture.ALLOW_IMAGE_READ);
//			texture.setCapability(Texture.ALLOW_IMAGE_WRITE);
//			ap.setTexture(texture);
//			shape.setAppearance(ap);
//		} else if (true) {
//			// Appearanceは再利用、Texture以下、全部設定
//			Texture texture;
//			if (false) {
//				// テクスチャローダを使用 → うまくいった
//				TextureLoader loader = new TextureLoader("data\\gray.jpg", TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, frame.getComponent(0));
//				texture = loader.getTexture();
//				texture.setCapability(Texture.ALLOW_IMAGE_READ);
//				texture.setCapability(Texture.ALLOW_IMAGE_WRITE);
//			} else if (true) {
//				// テクスチャローダのイメージのみ使用、テクスチャは作成 → イメージサイズの問題でうまくいかないようだ（イメージを256x128にする？）
//				TextureLoader loader = new TextureLoader("data\\gray.jpg", TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, frame.getComponent(0));
//				ImageComponent2D ic = loader.getImage();
//				ic.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
//				ic.setCapability(ImageComponent2D.ALLOW_FORMAT_READ);
//				ic.setCapability(ImageComponent2D.ALLOW_IMAGE_WRITE);
//				texture = new Texture2D(Texture.BASE_LEVEL, 
//						Texture.RGB, 
//						image2.getWidth(), 
//						image2.getHeight());
//				texture.setCapability(Texture.ALLOW_IMAGE_READ);
//				texture.setCapability(Texture.ALLOW_IMAGE_WRITE);
//				texture.setImage(0, ic);
//			} else {
//				texture = new Texture2D(Texture.BASE_LEVEL, 
//						Texture.RGB, 
//						image2.getWidth(), 
//						image2.getHeight());
//				texture.setCapability(Texture.ALLOW_IMAGE_READ);
//				texture.setCapability(Texture.ALLOW_IMAGE_WRITE);
//				ImageComponent2D ic = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, 
//						image2,
//						true, 
//						true);
//				ic.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
//				ic.setCapability(ImageComponent2D.ALLOW_FORMAT_READ);
//				ic.setCapability(ImageComponent2D.ALLOW_IMAGE_WRITE);
//				// Mipmapレベルは0のみ
//				texture.setImage(0, ic);
//			}
//			shape.getAppearance().setTexture(texture);
//		} else {
//			shape.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_READ);
//			shape.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);
//			shape.getAppearance().getTexture().setCapability(Texture.ALLOW_IMAGE_READ);
//			shape.getAppearance().getTexture().setCapability(Texture.ALLOW_IMAGE_WRITE);
//			shape.getAppearance().getTexture().getImage(0).setCapability(ImageComponent2D.ALLOW_FORMAT_READ);
//			shape.getAppearance().getTexture().getImage(0).setCapability(ImageComponent2D.ALLOW_IMAGE_READ);
//			shape.getAppearance().getTexture().getImage(0).setCapability(ImageComponent2D.ALLOW_IMAGE_WRITE);
//			((ImageComponent2D)shape.getAppearance().getTexture().getImage(0)).set(image1);
//		}
		// --- ここまで、テクスチャ張り替えのテスト用コード
		
		Universe universe = new Universe();
		Camera3D camera = new Camera3D(universe);
		camera.setViewPoint(new Position3D(0.0, 0.0, 10.0));
		camera.adjust(0L);
		canvas.attachCamera(camera);
		
		DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
				1.0f, 1.0f), new Vector3f(0.0f, 0.0f, -1.0f));
		light.setInfluencingBounds(new BoundingSphere(new Point3d(), 500.0));
		universe.placeLight(light);

		universe.placeUnremovable(obj);
		universe.compile();

		frame.setSize(720, 480);
		frame.setVisible(true);

		// //Pose3Dのテスト
		// Pose3D pose = new Pose3D();
		// Position3D pos = new Position3D(0.0, 1.0, 0.0);
		// Quaternion3D quat = new Quaternion3D(0.0, 0.0, 1.0, Math.PI / 4.0);
		// pose.addPose("ArmL", pos, quat);
		//        
		// Position3D pos1 = new Position3D(1.0, 0.0, 0.0);
		// Quaternion3D quat1 = new Quaternion3D(0.0, 0.0, 1.0, Math.PI / 2.0);
		// pose.addPose("ArmR", pos1, quat1);
		//        
		// Position3D pos2 = new Position3D(0.0, 1.0, 0.0);
		// Quaternion3D quat2 = new Quaternion3D(0.0, 0.0, 1.0, Math.PI / 4.0);
		// pose.addPose("obj1", pos2, quat2);
		//        
		// Position3D pos3 = new Position3D(0.0, 1.0, 0.0);
		// Quaternion3D quat3 = new Quaternion3D(0.0, 1.0, 1.0, Math.PI / 3.0);
		// pose.addPose("obj2", pos3, quat3);
		//        
		// Position3D pos4 = new Position3D(0.0, 0.0, 1.0);
		// Quaternion3D quat4 = new Quaternion3D(0.0, 0.0, 1.0, Math.PI / 4.0);
		// pose.addPose("obj3", pos4, quat4);
		//        
		// Position3D pos5 = new Position3D(0.0, 1.0, 1.0);
		// Quaternion3D quat5 = new Quaternion3D(0.0, 0.0, 1.0, Math.PI / 4.0);
		// pose.addPose("obj4", pos5, quat5);
		//        
		// Position3D pos6 = new Position3D(1.0, 1.0, 0.0);
		// Quaternion3D quat6 = new Quaternion3D(0.0, 0.0, 1.0, Math.PI / 4.0);
		// pose.addPose("obj5", pos6, quat6);
		// // obj.apply(pose);
		//        
		// //Animation3Dのテスト
		// Animation3D animation = new Animation3D();
		//
		// PartAnimation pa1 = new PartAnimation("ArmL");
		// pa1.add(0, new Position3D(0.0, 0.0, 0.0), new Quaternion3D());
		// pa1.add(10000, pos, quat);
		// animation.addPartAnimation(pa1);
		//        
		// PartAnimation pa2 = new PartAnimation("ArmR");
		// pa2.add(0, new Position3D(0.0, 0.0, 0.0), new Quaternion3D());
		// pa2.add(10000, pos, quat);
		// animation.addPartAnimation(pa2);
		//        
		// PartAnimation pa3 = new PartAnimation("obj1");
		// pa3.add(0, new Position3D(0.0, 0.0, 0.0), new Quaternion3D());
		// pa3.add(10000, pos, quat);
		// animation.addPartAnimation(pa3);
		//        
		// PartAnimation pa4 = new PartAnimation("obj2");
		// pa4.add(0, new Position3D(0.0, 0.0, 0.0), new Quaternion3D());
		// pa4.add(10000, pos, quat);
		// animation.addPartAnimation(pa4);
		//        
		// PartAnimation pa5 = new PartAnimation("obj3");
		// pa5.add(0, new Position3D(0.0, 0.0, 0.0), new Quaternion3D());
		// pa5.add(10000, pos, quat);
		// animation.addPartAnimation(pa5);
		//        
		// PartAnimation pa6 = new PartAnimation("obj4");
		// pa6.add(0, new Position3D(0.0, 0.0, 0.0), new Quaternion3D());
		// pa6.add(10000, pos, quat);
		// animation.addPartAnimation(pa6);
		//        
		// PartAnimation pa7 = new PartAnimation("obj5");
		// pa7.add(0, new Position3D(0.0, 0.0, 0.0), new Quaternion3D());
		// pa7.add(10000, pos, quat);
		// animation.addPartAnimation(pa7);
		
//		// テクスチャアニメーションのテスト
//		Animation3D animation = new Animation3D();
//		PartAnimation pa = new PartAnimation("Body");
//		TextureLoader loader1 = new TextureLoader("data\\white.jpg", TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
//		Texture texture1 = loader1.getTexture();
//		TextureLoader loader2 = new TextureLoader("data\\gray.jpg", TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
//		Texture texture2 = loader2.getTexture();
//		TextureLoader loader3 = new TextureLoader("data\\black.jpg", TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
//		Texture texture3 = loader3.getTexture();
//		pa.addTexture(0, texture1, new Position3D());
//		pa.addTexture(100, texture2, new Position3D(0.4, 0.0, 0.0));
//		pa.addTexture(200, texture3, new Position3D(0.8, 0.0, 0.0));
//		pa.addTexture(300, texture2, new Position3D(0.4, 0.0, 0.0));
//		pa.addTexture(400, texture1, new Position3D());
//		animation.addPartAnimation(pa);

		// アニメーションファイル読み込みのテスト
//		Animation3D animation = AnimationFactory
//				.loadAnimation("data\\character\\walk.wrl");
		Animation3D animation = AnimationFactory.loadAnimation("data\\pocha\\jump.wrl");

		for (;;) {
			try {
				Thread.sleep(1);
				boolean b = animation.progress(1);
				System.out.println(animation.time);
				// if (b == false) break;
				Pose3D pose2 = animation.getPose();
				obj.apply(pose2, false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new TestAnimation();
	}
}
