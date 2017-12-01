package framework.game2D;

import java.util.ArrayList;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import template.shooting.Player;
import framework.animation.Animation3D;
import framework.gameMain.Actor;
import framework.gameMain.OvergroundActor;
import framework.model3D.BaseObject3D;
import framework.model3D.Object3D;
import framework.model3D.Universe;
import framework.physics.Velocity3D;

/**
 * 2Dゲーム上のキャラクタの抽象クラス。ゲーム上のキャラクタのクラスを作るにはこれを継承し作成する。
 * 
 * @author T.Kuno
 * 
 */
public abstract class Player2D extends Actor2D {
	private ArrayList<BaseObject3D> extraObjects = new ArrayList<BaseObject3D>();

	private static final Vector2d LEFT_AXIS = new Vector2d(-1.0, 0.0);
	private static final Vector2d Right_AXIS = new Vector2d(1.0, 0.0);
	private static final Vector2d UP_AXIS = new Vector2d(0.0, 1.0);
	private static final Vector2d DOWN_AXIS = new Vector2d(0.0, -1.0);

	// キャラクタの動きで使うグローバル変数。本来はこれを見せない設計にする。
	protected Vector3d direction = new Vector3d(1.0, 1.0, 1.0);
	private Velocity2D InitialVelocity = new Velocity2D(1.0, 0.0);

	protected Actor createActor(Object3D modelBody, Animation3D anim) {
		return new OvergroundActor(modelBody, anim);
	}

	// //////////////////////////////
	//
	// プレイヤーの作成に関するメソッド
	//
	// //////////////////////////////

	/**
	 * 引数のuniverse内にプレイヤーが作られる。（プレイヤーは取り除くことが出来る。）
	 * 
	 * @param universe
	 *            -- ゲームにおける空間
	 */
	public void createPlayer(Universe universe) {
		BaseObject3D body = this.getBody();
		if (body.isReflectionMappingApplied() || body.isBumpMappingApplied()) {
			extraObjects.add(body);
		} else {
			universe.place(this.getTransformGroupToPlace());
		}
	}

	// //////////////////////////////
	//
	// プレイヤーの位置に関するメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタの位置を設定する。
	 * 
	 * @param x
	 *            -- x座標
	 * @param y
	 *            -- y座標
	 */
	public void setPosition(double x, double y) {
		Position2D pos = new Position2D(x, y);
		super.setPosition(pos);
	}

	/**
	 * キャラクタの位置をPostion2Dで取得する。
	 * 
	 * @return Position2D
	 */
	public Position2D getPosition() {
		return super.getPosition();
	}

	// //////////////////////////////
	//
	// プレイヤー向きに関するメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタの向きをVector2dで設定する。
	 * 
	 * @param dir
	 *            -- 2次元ベクトルで表される向き
	 */
	public void setDirection(double x, double y) {
		Vector2d dir = new Vector2d(x, y);
		super.setDirection(dir);
	}

	/**
	 * キャラクタの現在の向きをVector2dで取得する。
	 * 
	 * @return Vector2d -- 2次元ベクトルで表される向き
	 */
	public Vector2d getDirection() {
		return super.getDirection();
	}

	// //////////////////////////////
	//
	// プレイヤーを速度に関するメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタの速度を変える。（引数の値で倍にする）
	 * 
	 * @param velocity
	 *            -- 倍率
	 */

	public void setVelocity(double velocity) {
		Velocity2D vel = (Velocity2D) InitialVelocity.clone();
		vel.setVelocity(velocity);
		super.setVelocity(vel);
	}

	/**
	 * キャラクタの速度を表すVelocity2Dをgetする。
	 * 
	 * @return velocity2D
	 */

	public Velocity2D getVelocity() {
		return super.getVelocity();
	}

	// //////////////////////////////
	//
	// プレイヤーを左方向に動かすメソッド
	//
	// //////////////////////////////
	/**
	 * キャラクタを向きを考慮せず、左方向の速度ベクトルを用いて動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveLeft(double d) {
		Velocity2D vel = new Velocity2D(-1.0 * d, 0.0);
		setVelocity(vel);
	}

	/**
	 * キャラクタの向きを考慮して、左方向の速度ベクトルを用いて動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveDirectionLeft(double d) {
		super.setDirection(LEFT_AXIS);
		moveLeft(d);
	}

	/**
	 * キャラクタの位置を左方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void movePositionLeft(double d) {
		Position2D p = new Position2D(getActor().getPosition().getX(),
				getActor().getPosition().getY());
		p.addX(-1.0 * d);
		setPosition(p);
	}

	// //////////////////////////////
	//
	// プレイヤーを右方向に動かすメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタを向きを考慮せず右に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveRight(double d) {
		Velocity2D vel = new Velocity2D(1.0 * d, 0.0);
		setVelocity(vel);
	}

	/**
	 * キャラクタの向きを考慮して右に動かす
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveDirectionRight(double d) {
		super.setDirection(Right_AXIS);
		moveRight(d);
	}

	/**
	 * キャラクタの位置を右方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void movePositionRight(double d) {
		Position2D p = new Position2D(getActor().getPosition().getX(),
				getActor().getPosition().getY());
		p.addX(1.0 * d);
		setPosition(p);
	}

	// //////////////////////////////
	//
	// プレイヤーを上方向に動かすメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタを向きを考慮せず上に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveUp(double d) {
		Velocity2D vel = new Velocity2D(0.0, 1.0 * d);
		setVelocity(vel);
	}

	/**
	 * キャラクタの向きを考慮して上に動かす
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveDirectionUp(double d) {
		super.setDirection(UP_AXIS);
		moveUp(d);
	}

	/**
	 * キャラクタの位置を上方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void movePositionUp(double d) {
		Position2D p = new Position2D(getActor().getPosition().getX(),
				getActor().getPosition().getY());
		p.addY(1.0 * d);
		setPosition(p);
	}

	// //////////////////////////////
	//
	// プレイヤーを下方向に動かすメソッド
	//
	// //////////////////////////////

	/**
	 * キャラクタを向きを考慮せず下に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveDown(double d) {
		Velocity2D vel = new Velocity2D(0.0, -1.0 * d);
		setVelocity(vel);
	}

	/**
	 * キャラクタの向きを考慮して下に動かす
	 * 
	 * @param d
	 *            動かす量
	 */
	public void moveDirectionDown(double d) {
		super.setDirection(DOWN_AXIS);
		moveDown(d);
	}

	/**
	 * キャラクタの位置を下方向に動かす。
	 * 
	 * @param d
	 *            動かす量
	 */
	public void movePositionDown(double d) {
		Position2D p = new Position2D(getActor().getPosition().getX(),
				getActor().getPosition().getY());
		p.addY(-1.0 * d);
		setPosition(p);
	}

	/**
	 * 
	 * キャラクタの速度を0にし、動きを止める。
	 * 
	 */

	public void stop() {
		Velocity3D curV = new Velocity3D();
		getActor().setVelocity(curV);
	}

	//////////////////////////////////////////////////////
	//
	// プレイヤーがウィンドウ内にいるかどうかのメソッド
	//
	/////////////////////////////////////////////////////
	
	public boolean doesScrrenOut(int w, int h) {
		if (this.getPosition().getX() < w / 2.0
				&& this.getPosition().getX() > -1.0 * w / 2.0) {
			if(this.getPosition().getY() < h / 2.0
					&& this.getPosition().getY() > -1.0 * h / 2.0){
				return true;
			}
		}
		return false;
	}
	
}
