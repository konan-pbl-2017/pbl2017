package framework.game2D;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import template.shooting.Player;

import framework.animation.Animation3D;
import framework.gameMain.Actor;
import framework.gameMain.FlyingActor;
import framework.gameMain.OvergroundActor;
import framework.model3D.BaseObject3D;
import framework.model3D.Object3D;
import framework.model3D.Universe;

public abstract class Bullet2D extends Actor2D {
	private ArrayList<BaseObject3D> extraObjects = new ArrayList<BaseObject3D>();
	
	protected Actor createActor(Object3D modelBody, Animation3D anim) {
		return new FlyingActor(modelBody, anim);
	}
	
	// //////////////////////////////
	//
	// 弾の作成に関するメソッド
	//
	// //////////////////////////////

	/**
	 * 引数のuniverse内に弾が作られる。（弾は取り除くことが出来る。）
	 * 
	 * @param universe
	 *            -- ゲームにおける空間
	 */
	public void createPlayer(Universe universe) {
		BaseObject3D body = this.getBody();
		if (body.isReflectionMappingApplied() || body.isBumpMappingApplied()) {
			extraObjects.add(body);
		} else {
			universe.placeUnremovable(this.getTransformGroupToPlace());
		}
	}
	
	/**
	 * キャラクタの位置をsetする。
	 * @param x -- x座標
	 * @param y -- y座標
	 */
	public void setPosition(double x, double y) {
		Position2D pos = new Position2D(x,y);
		super.setPosition(pos);
	}
	
	/**
	 * キャラクタの位置をPostion2Dでgetする。
	 * @return	Position2D
	 */
	public Position2D getPosition() {
		return super.getPosition();
	}

	/**
	 * キャラクタの向きをVector2dでsetする。
	 * @param dir -- 2次元ベクトルで表される向き
	 */
	public void setDirection(double x, double y) {
		Vector2d dir = new Vector2d(x, y);
		super.setDirection(dir);
	}
	
	/**
	 * キャラクタの現在の向きをVector2dでgetする。
	 * @return Vector2d -- 2次元ベクトルで表される向き
	 */
	public Vector2d getDirection() {
		return super.getDirection();
	}
	
	/**
	 * キャラクタの速度を変える。（引数の値で倍にする）
	 * @param velocity -- 倍率  
	 */

	public void setMulVelocity(Velocity2D vel, double velocity) {
		vel.setX(vel.getX() * velocity);
		vel.setY(vel.getY() * velocity);
		super.setVelocity(vel);
	}

	/**
	 * キャラクタの速度を表すVelocity2Dをgetする。
	 * @return	velocity2D  
	 */
	
	public Velocity2D getVelocity() {
		return super.getVelocity();
	}
	
}
