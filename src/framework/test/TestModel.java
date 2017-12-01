package framework.test;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Clip;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Locale;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

import cv97.Constants;
import cv97.SceneGraph;
import cv97.j3d.SceneGraphJ3dObject;
import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTFrame3D;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.view3D.Camera3D;



public class TestModel extends Frame implements Constants {
	SceneGraph sg;
	Canvas3D c;
	
	TestModel(){
		super("VRML Simple Viewer");
		
		
		sg = new SceneGraph(SceneGraph.NORMAL_GENERATION);
		setLayout(new BorderLayout());

		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        
		c = new Canvas3D(config);
		add("Center",c);
		SceneGraphJ3dObject sgObject = new SceneGraphJ3dObject(c,sg);		
		sg.setObject(sgObject);
		
//		sg.load("data\\Q07077-2.3ds");
//		sg.save("newGLViewLib.wrl");
		
//		setSize(400,400);
//		sg.print();
//		show();
		
	}
	public static void main(String[] args) {
//		TestModel testModel = new TestModel();
		RWTFrame3D frame = new RWTFrame3D();
		RWTCanvas3D canvas = new RWTCanvas3D();
		frame.add(canvas);
		
//		Model3D model = ModelFactory.loadModel("data\\doordemo.wrl");
//		Model3D model = ModelFactory.loadModel("data\\cube2.3ds");
		Model3D model = ModelFactory.loadModel("data\\Head.3ds");
		Object3D obj = model.createObject();
		Transform3D s = new Transform3D();
//		s.setScale(0.01);
		obj.children[0].scale.setTransform(s);
		
		Universe universe = new Universe();
		
        DirectionalLight light = new DirectionalLight( true,
                new Color3f(1.0f, 1.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, -1.0f));
        light.setInfluencingBounds(new BoundingSphere(new Point3d(), 500.0));
        universe.placeUnremovable(light);
        
//		SimpleUniverse universe = new SimpleUniverse(testModel.c);
        universe.placeUnremovable(obj);
        universe.compile();
        
		Camera3D camera = new Camera3D(universe);
		camera.setViewPoint(new Position3D(0.0, 0.0, 1000.0));
		camera.adjust(0L);
		canvas.attachCamera(camera);
				
        frame.setVisible(true);
       // obj.rotate(0.0, 1.0, 0.0, 2 * Math.PI / 4);
//        Transform3D trans = new Transform3D();
//        Transform3D trans2 = new Transform3D();
//        obj.children[0].children[4].pos.getTransform(trans);
//        obj.children[0].children[0].rot.getTransform(trans2);
//        
//		Vector3d vector = new Vector3d();
//		trans.get(vector);
//		vector.x = vector.x + 20;
//		trans.set(vector);
//		
//		trans2.rotY(2 * Math.PI / 8);
//		
//		frame.setSize(600, 480);
//        obj.children[0].children[4].pos.setTransform(trans);
//		obj.children[0].children[0].rot.setTransform(trans2);
	}
}
