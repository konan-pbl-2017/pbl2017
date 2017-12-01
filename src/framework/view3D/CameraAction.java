package framework.view3D;

import javax.vecmath.Vector3d;

import framework.game2D.OvergroundActor2D;
import framework.model3D.Position3D;
import framework.model3D.Universe;

/**
 * 2Dアクション用のカメラ
 * 
 * @author K.Yamane
 * 
 */
public class CameraAction extends Camera3D {

	private Vector3d cameraBack = null;
	private static double cameraRange;

	public CameraAction(Universe universe) {
		super(universe);
		setSideView();
		addTarget(new Position3D(0.0, 0.0, 0.0));
		// TODO Auto-generated constructor stub
	}

	public void setRange(double range) {
		cameraRange = range;
		cameraBack = new Vector3d(0.0, 0.0, range);
		setCameraBack(cameraBack);
	}

	public void setChaseMode(OvergroundActor2D actor) {
		setViewPoint(new Position3D(actor.getPosition().getX(), 0.0,
				cameraRange));
	}

	public void setFixedMode(double x, double y) {
		addTarget(new Position3D(x, y, cameraRange));
	}
}