package framework.game2D;

import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import framework.model3D.BaseObject3D;
import framework.model3D.Placeable;
import framework.model3D.Position3D;
import framework.physics.Solid3D;
import framework.physics.Velocity3D;

public interface Movable extends Placeable {

	// /////////////////////////////////////////////////
	//
	// 動作する物体のPositionに関するインターフェース
	//
	// /////////////////////////////////////////////////

	/**
	 * 動作する物体のposを設定する
	 * 
	 * @param pos
	 *            -- 新しい位置
	 */
	public void setPosition(Position2D pos);

	/**
	 *　動作する物体の現在の位置を返す。
	 * 
	 * @return 現在の位置
	 */
	public Position2D getPosition();

	// ///////////////////////////////////////////////
	//
	// 動作する物体のVelocityに関するインターフェース
	//
	// ///////////////////////////////////////////////

	/**
	 * 動作する物体にvelを設定する。
	 * 
	 * @param vel
	 *            -- 新しい速度
	 */
	public void setVelocity(Velocity2D vel);

	/**
	 * 動作する物体の現在の速度を返す。
	 * 
	 * @return 現在の速度
	 */
	public Velocity2D getVelocity();

	// /////////////////////////////////////////
	//
	// 動作する物体のmotionインターフェース
	//
	// ////////////////////////////////////////

	/**
	 * 動作する物体を固有の速度で動かす
	 * 
	 * @param interval
	 */
	public void motion(long interval);

	/**
	 * 自分と別の登場物との当たり判定
	 * 
	 * @param other
	 *            自分以外の登場物
	 * @return true --- 当たった, false --- 当たってない
	 */
	public boolean checkCollision(Movable other);
}
