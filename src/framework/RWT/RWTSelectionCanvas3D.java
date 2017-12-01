package framework.RWT;

import java.util.Enumeration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import framework.model3D.BaseObject3D;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

public class RWTSelectionCanvas3D extends RWTCanvas3D {
	Universe universe = null;
	BranchGroup objRoot = null;
	
	public RWTSelectionCanvas3D() {
		this(0.0, 0.0, 12.0);
	}

	public RWTSelectionCanvas3D(double cameraX, double cameraY, double cameraZ) {
		universe = new Universe();
		objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		universe.getRoot().addChild(objRoot);
		createLight(universe);
		setCamera(universe, cameraX, cameraY, cameraZ);
		universe.compile();
	}

	private void setCamera(Universe universe, double cameraX, double cameraY, double cameraZ) {
		Camera3D camera = new Camera3D(universe);
		camera.setViewPoint(new Position3D(cameraX, cameraY, cameraZ));
		camera.addTarget(new Position3D(0.0, 0.0, 0.0));
		camera.adjust(0L);
		attachCamera(camera);
	}

	void createLight(Universe universe) {
		// 環境光
		AmbientLight amblight = new AmbientLight(new Color3f(0.5f, 0.5f, 0.5f));
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
	}

	public void setObject(BaseObject3D obj) {
		BranchGroup newObj = new BranchGroup();
		newObj.setCapability(BranchGroup.ALLOW_DETACH);
		newObj.addChild(obj.getTransformGroupToPlace());
		objRoot.removeAllChildren();
		objRoot.addChild(newObj);
	}
}
