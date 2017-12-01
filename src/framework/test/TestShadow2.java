package framework.test;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;


import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.gameMain.SimpleGame;
import framework.model3D.BaseObject3D;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

public class TestShadow2 extends SimpleGame {
	Object3D box;
	BaseObject3D ground;
	double a = 0.0;
	
	public static void main(String[] args) {
		TestShadow2 game = new TestShadow2();
		game.setFramePolicy(100, false);
		game.start();
	}

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D f = new RWTFrame3D();
		f.setSize(800, 600);
		f.setShadowCasting(true);
		f.setTitle("Flag Rush");
		return f;
	}
	
	@Override
	public void init(Universe universe, Camera3D camera) {
		// 光源の設定
        DirectionalLight dirlight = new DirectionalLight(
        		true,                           //光のON/OFF
                new Color3f(1.0f, 1.0f, 1.0f),  //光の色
                new Vector3f(1.0f, -1.0f, -0.5f) //光の方向ベクトル
        );
        dirlight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));        
        universe.placeLight(dirlight, 100.0);
        
        PointLight pointLight = new PointLight(
    		true,                           //光のON/OFF
            new Color3f(1.0f, 0.3f, 0.0f),  //光の色
            new Point3f(0.0f, 25.0f, 0.0f), //光源の位置
            new Point3f(0.1f, 0.0f, 0.001f)  //光の減衰        	
        );
        pointLight.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));        
        universe.placeLight(pointLight);

		Appearance ap1 = new Appearance();
		Material m1 = new Material();
		m1.setDiffuseColor(1.0f, 1.0f, 0.0f);
		ap1.setMaterial(m1);
		ColoringAttributes ca = new ColoringAttributes();
		ca.setShadeModel(ColoringAttributes.NICEST);
		ap1.setColoringAttributes(ca);
		box = new Object3D("occuluder", new Box(2.0f, 2.0f, 2.0f, ap1));
		box.apply(new Position3D(0.0, 10.0, 0.0), false);
//		box = new Object3D("occuluder", boxGeometry, ap1);
		universe.placeAsAnOcculuder(box);
		
		IndexedTriangleArray groundGeometry = new IndexedTriangleArray(4, 
				IndexedTriangleArray.COORDINATES | IndexedTriangleArray.NORMALS, 6);
		groundGeometry.setCoordinate(0, new Point3d(-20.0, 0.0, -20.0));
		groundGeometry.setCoordinate(1, new Point3d(20.0, 0.0, -20.0));
		groundGeometry.setCoordinate(2, new Point3d(20.0, 0.0, 20.0));
		groundGeometry.setCoordinate(3, new Point3d(-20.0, 0.0, 20.0));
		groundGeometry.setNormal(0, new Vector3f(0.0f, 1.0f, 0.0f));
		groundGeometry.setNormal(1, new Vector3f(0.0f, 1.0f, 0.0f));
		groundGeometry.setNormal(2, new Vector3f(0.0f, 1.0f, 0.0f));
		groundGeometry.setNormal(3, new Vector3f(0.0f, 1.0f, 0.0f));
		groundGeometry.setCoordinateIndices(0, new int[]{0, 3, 2});
		groundGeometry.setCoordinateIndices(3, new int[]{0, 2, 1});
		Appearance ap2 = new Appearance();
		Material m = new Material();
		m.setDiffuseColor(1.0f, 1.0f, 1.0f);
		m.setAmbientColor(0.5f, 0.5f, 0.5f);
		m.setSpecularColor(0.0f, 0.0f, 0.0f);
		m.setShininess(1.0f);
		ap2.setMaterial(m);
		ap2.setColoringAttributes(new ColoringAttributes(0.5f, 0.5f, 0.5f, ColoringAttributes.NICEST));
		ground = new BaseObject3D(groundGeometry, ap2);
		universe.placeAsAReceiver(ground);
		
		// カメラの設定
		camera.setViewPoint(new Position3D(50.0, 50.0, 50.0));
		camera.addTarget(new Position3D(0.0, 0.0, 0.0));
	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
//		Position3D pos = box.getPosition3D();
//		pos.add(0.1, 0.0, 0.0);
//		box.apply(pos, false);
		Quaternion3D q = new Quaternion3D(1.0, 0.0, 0.0, a);
		box.apply(q, false);
		a += .1;
		if (a > Math.PI * 2) a -= Math.PI * 2;
	}
}
