package templete.action2D;

import javax.vecmath.Vector2d;

import framework.game2D.OvergroundActor2D;
import framework.game2D.Position2D;

/**
 * プレイヤーのクラス
 * 
 * @author H.Kotobuki
 * 
 */
public class Player extends OvergroundActor2D {
	
	private static final Vector2d LEFT_AXIS = new Vector2d(-1.0, 0.0);
	private static final Vector2d Right_AXIS = new Vector2d(1.0, 0.0);
	public double getCollisionRadius() {
		return collisionRadius;
	}

	public String getAnimationFileName() {
		return "data\\TemplateShooting\\Character\\pocha\\walk.wrl";
	}

	public String getModelFileName() {
		return "data\\TemplateShooting\\Character\\pocha\\pocha.wrl";
	}

	/**
	 * キャラクタの位置を左に向くのと同時に左方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void movePositionLeft(double d) {
		super.setDirection(LEFT_AXIS);
		super.movePositionLeft(d);
	}
	
	/**
	 * キャラクタの位置を右に向くのと同時に右方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void movePositionRight(double d) {
		super.setDirection(Right_AXIS);
		super.movePositionRight(d);
	}
	
	public boolean isOnGround() {
		if (getActor().isOnGround()) {
			return true;
		} else {
			return false;
		}
	}
	

}
