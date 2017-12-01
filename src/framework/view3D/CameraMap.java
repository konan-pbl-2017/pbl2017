package framework.view3D;

import javax.media.j3d.View;
import javax.vecmath.Vector3d;

import framework.game2D.Map2D;
import framework.model3D.Position3D;
import framework.model3D.Universe;

/**
 * 2D迷路ゲーム用のカメラ
 * 
 * @author T.Kuno
 * 
 */
public class CameraMap extends Camera3D {

	private Vector3d cameraBack = null;

	// コンストラクタ
	public CameraMap(Universe universe) {
		super(universe);
		setSideView();
		this.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
	}

	/**
	 * rangeで指定された値からカメラが映り込む範囲を設定する
	 * 
	 * @param range
	 */
	public void setRange(double range) {
		cameraBack = new Vector3d(0.0, 0.0, range);
		setCameraBack(cameraBack);
	}
	
	public void addTarget(Map2D maze) {
		super.addTarget(new Position3D((double)maze.getMapWidth()/2.0,(double)maze.getMapHeight()/2.0,0.0));
	}
}
