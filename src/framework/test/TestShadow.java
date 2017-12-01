package framework.test;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cylinder;

import framework.RWT.RWTFrame3D;
import framework.RWT.RWTCanvas3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

public class TestShadow {

	public TestShadow() {
		// TODO Auto-generated constructor stub
		RWTFrame3D frame = new RWTFrame3D();
		
		// ステンシルバッファを使用する GraphicsConfiguration の生成
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfigTemplate3D gct3D = new GraphicsConfigTemplate3D();
		gct3D.setStencilSize(8);
		GraphicsConfiguration gc = gd.getBestConfiguration(gct3D);
		
		RWTCanvas3D canvas = new RWTCanvas3D(gc);
		frame.add(canvas);
		Map canvasProperty = canvas.queryProperties();
		System.out.println(canvasProperty.get("stencilSize"));

		Universe universe = new Universe();

		DirectionalLight light = new DirectionalLight(true, 
				new Color3f(1.0f, 1.0f, 1.0f), 
				new Vector3f(0.0f, -1.0f, -1.0f));
		light.setInfluencingBounds(new BoundingSphere(new Point3d(), 1000.0));
		universe.placeLight(light);
				
		// 地面全体を影の色で描画（距離情報をZバッファに書き込む）
		Appearance ap0 = new Appearance();
		Material m0 = new Material();
		m0.setDiffuseColor(0.5f, 0.5f, 0.5f);	// 影の色を灰色に
		m0.setSpecularColor(0.0f, 0.0f, 0.0f);
		ap0.setMaterial(m0);
		RenderingAttributes ra0 = new RenderingAttributes();
//		ra0.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_READ);
//		ra0.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_WRITE);
//		ra0.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_READ);
//		ra0.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_WRITE);
//		ra0.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_READ);
//		ra0.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_WRITE);
		ra0.setStencilEnable(false);
		ra0.setDepthBufferWriteEnable(true);
		ra0.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);
		ap0.setRenderingAttributes(ra0);
		PolygonAttributes pa0 = new PolygonAttributes();
		pa0.setCullFace(PolygonAttributes.CULL_BACK);
		ap0.setPolygonAttributes(pa0);
		QuadArray quadArray0 = new QuadArray(4, QuadArray.COORDINATES | QuadArray.NORMALS);
		quadArray0.setCoordinates(0, new Point3d[]{new Point3d(-10.0, 0.0, -10.0),
				new Point3d(-10.0, 0.0, 10.0), 
				new Point3d(10.0, 0.0, 10.0), 
				new Point3d(10.0, 0.0, -10.0)});
		quadArray0.setNormal(0, new Vector3f(0.0f, 1.0f, 0.0f));
		quadArray0.setNormal(1, new Vector3f(0.0f, 1.0f, 0.0f));
		quadArray0.setNormal(2, new Vector3f(0.0f, 1.0f, 0.0f));
		quadArray0.setNormal(3, new Vector3f(0.0f, 1.0f, 0.0f));
		Shape3D box0 = new Shape3D(quadArray0, ap0);
		universe.placeUnremovable(box0);

		// シャドウボリューム表面（ステンシルバッファを+1）
		Appearance ap1 = new Appearance();
//		ap1.setMaterial(new Material());
		RenderingAttributes ra1 = new RenderingAttributes();
//		ra1.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_READ);
//		ra1.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_WRITE);
//		ra1.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_READ);
//		ra1.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_WRITE);
//		ra1.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_READ);
//		ra1.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_WRITE);
		ra1.setStencilEnable(true);
		ra1.setDepthBufferWriteEnable(false);
		ra1.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);
		ra1.setStencilFunction(RenderingAttributes.ALWAYS, 0, ~0);
		ra1.setStencilOp(RenderingAttributes.STENCIL_KEEP, RenderingAttributes.STENCIL_KEEP, RenderingAttributes.STENCIL_INCR);
		ap1.setRenderingAttributes(ra1);
		PolygonAttributes pa1 = new PolygonAttributes();
		pa1.setCullFace(PolygonAttributes.CULL_BACK);
		ap1.setPolygonAttributes(pa1);
		TransparencyAttributes ta1 = new TransparencyAttributes();	// setVisible(false)だとステンシルバッファが更新されないので、透明に
		ta1.setTransparencyMode(TransparencyAttributes.BLENDED);
		ta1.setTransparency(1.0f);
		ap1.setTransparencyAttributes(ta1);
		Cylinder cyl1 = new Cylinder(1.0f, 10.0f, ap1);
		universe.placeUnremovable(cyl1);
		
		// シャドウボリューム裏面（ステンシルバッファを-1）
		Appearance ap2 = new Appearance();
//		ap2.setMaterial(new Material());
		RenderingAttributes ra2 = new RenderingAttributes();
//		ra2.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_READ);
//		ra2.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_WRITE);
//		ra2.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_READ);
//		ra2.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_WRITE);
//		ra2.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_READ);
//		ra2.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_WRITE);
		ra2.setStencilEnable(true);
		ra2.setDepthBufferWriteEnable(false);
		ra2.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);
		ra2.setStencilFunction(RenderingAttributes.ALWAYS, 0, ~0);
		ra2.setStencilOp(RenderingAttributes.STENCIL_KEEP, RenderingAttributes.STENCIL_KEEP, RenderingAttributes.STENCIL_DECR);
		ap2.setRenderingAttributes(ra2);
		PolygonAttributes pa2 = new PolygonAttributes();
		pa2.setCullFace(PolygonAttributes.CULL_FRONT);
		ap2.setPolygonAttributes(pa2);
		TransparencyAttributes ta2 = new TransparencyAttributes();	// setVisible(false)だとステンシルバッファが更新されないので、透明に
		ta2.setTransparencyMode(TransparencyAttributes.BLENDED);
		ta2.setTransparency(1.0f);
		ap2.setTransparencyAttributes(ta2);
		Cylinder cyl2 = new Cylinder(1.0f, 10.0f, ap2);
		universe.placeUnremovable(cyl2);

		// 影の部分を除いた地面
		Appearance ap3 = new Appearance();
		Material m3 = new Material();
		ap3.setMaterial(m3);
		RenderingAttributes ra3 = new RenderingAttributes();
//		ra3.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_READ);
//		ra3.setCapability(RenderingAttributes.ALLOW_DEPTH_ENABLE_WRITE);
//		ra3.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_READ);
//		ra3.setCapability(RenderingAttributes.ALLOW_DEPTH_TEST_FUNCTION_WRITE);
//		ra3.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_READ);
//		ra3.setCapability(RenderingAttributes.ALLOW_STENCIL_ATTRIBUTES_WRITE);
		ra3.setStencilEnable(true);
		ra3.setDepthBufferWriteEnable(true);
		ra3.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);
		ra3.setStencilFunction(RenderingAttributes.GREATER_OR_EQUAL, 0, ~0);	// ステンシルバッファの値が1以上のとき描画しない
		ra3.setStencilOp(RenderingAttributes.STENCIL_KEEP, RenderingAttributes.STENCIL_KEEP, RenderingAttributes.STENCIL_KEEP);
		ap3.setRenderingAttributes(ra3);
		PolygonAttributes pa3 = new PolygonAttributes();
		pa3.setCullFace(PolygonAttributes.CULL_BACK);
		ap3.setPolygonAttributes(pa3);
		QuadArray quadArray1 = new QuadArray(4, QuadArray.COORDINATES);
		quadArray1.setCoordinates(0, new Point3d[]{new Point3d(-10.0, 0.0, -10.0),
				new Point3d(-10.0, 0.0, 10.0), 
				new Point3d(10.0, 0.0, 10.0), 
				new Point3d(10.0, 0.0, -10.0)});
		Shape3D box1 = new Shape3D(quadArray1, ap3);
		universe.placeUnremovable(box1);

		frame.setSize(720, 480);
		frame.setVisible(true);

		// 表示
		universe.compile();

		// カメラの設定
		Camera3D camera = new Camera3D(universe);
		camera.setViewPoint(new Position3D(20.0, 20.0, 20.0));
		camera.addTarget(new Position3D(0.0, 0.0, 0.0));
		canvas.attachCamera(camera);

		for (;;) {
			try {
				Thread.sleep(17);
				camera.adjust(17);
				// Force3D f = PhysicsFacade.getGravity(solid);
				// solid.move(17, f, applicationPoint);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new TestShadow();
	}
}
