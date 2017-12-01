package framework.view3D;

import javax.media.j3d.View;
import javax.vecmath.Vector3d;

import framework.model3D.Universe;

/**
 * 2Dシューティング用のカメラ
 * 
 * @author K.Yamane
 * 
 */
public class CameraShooting extends Camera3D {

	private Vector3d cameraBack = null;

	// コンストラクタ
	public CameraShooting(Universe universe) {
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
}
