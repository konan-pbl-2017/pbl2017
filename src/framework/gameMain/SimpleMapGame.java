package framework.gameMain;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import template.PRG2D.Player;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.game2D.Maze2D;
import framework.game2D.Sprite;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.PhysicsUtility;
import framework.view3D.Camera3D;
import framework.view3D.CameraMap;

/**
 * 2D迷路ゲーム用のクラス
 * 
 * @author T.Kuno
 * 
 */

public abstract class SimpleMapGame extends SimpleMazeGame {
	protected double mapCenterX = 0;
	protected double mapCenterY = 0;
	protected Sprite center = null;

	public void update(RWTVirtualController virtualController, long interval) {
		progress(virtualController, interval);
		setMapCenter(center.getPosition().getX(), center.getPosition().getY());
		camera.adjust(interval);
	}
	
	protected void setCenter(Sprite center) {
		this.center = center;
		setMapCenter(center.getPosition().getX(), center.getPosition().getY());
	}

	/////////////////////////////////////////////////////////
	//
	// 2Dゲームにおけるカメラの設定や見え方、その範囲などに関するメソッド
	//
	/////////////////////////////////////////////////////////
	
	/**
	 * カメラが見る範囲をwidthとheightで指定する
	 * @param width
	 * @param height
	 */
	protected void setViewRange(double width, double height) {
		viewRangeWidth = width;
		viewRangeHeight = height;
		updateViewArea();
	}

	public void setMapCenter(double x, double y) {
		mapCenterX = x;
		mapCenterY = y;
		updateViewArea();		
	}
	
	public double getMapCenterX() {
		return mapCenterX;
	}
	
	public double getMapCenterY() {
		return mapCenterY;
	}
	
	private void updateViewArea() {
		Transform3D t = new Transform3D();
		t.ortho(-viewRangeWidth + mapCenterX, mapCenterX, -viewRangeHeight + mapCenterY,
				mapCenterY, 0.1, 10000);
		camera.getView().setCompatibilityModeEnable(true);
		camera.getView().setVpcToEc(t);
	}
}
