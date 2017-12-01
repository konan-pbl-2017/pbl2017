package framework.gameMain;


import java.util.ArrayList;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import framework.animation.Animation3D;
import framework.model3D.CollisionResult;
import framework.model3D.GeometryUtility;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;
import framework.physics.Force3D;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;
import framework.physics.Velocity3D;

/**
 * ゲーム内の登場物全般
 * @author 新田直也
 *
 */
public abstract class Actor extends Animatable {
	protected Vector3d direction = new Vector3d(1.0, 0.0, 0.0);
	protected Mode mode;
	// 以下省メモリ化のため予めインスタンスを生成
	protected Mode modeFreeFall = new ModeFreeFall();
	protected Mode modeOnGround = new ModeOnGround();
	private static final Vector3d X_AXIS = new Vector3d(1.0, 0.0, 0.0);
	private static final Vector3d Y_AXIS = new Vector3d(0.0, 1.0, 0.0);
	private static final Vector3d Z_AXIS = new Vector3d(0.0, 0.0, 1.0);
	
	public Actor(Object3D body, Animation3D animation) {
		super(new Solid3D(body), animation);
		mode = modeOnGround;
	}

	public Actor(Solid3D body, Animation3D animation) {
		super(body, animation);
		mode = modeOnGround;
	}

	public Actor(ActorModel model) {
		super(model.createBody(), model.getAnimation());
		mode = modeOnGround;
	}

	
	/**
	 * 単位時間ごとの動作（衝突判定処理も行う）
	 * @param interval --- 前回呼び出されたときからの経過時間（ミリ秒単位）
	 */
	public void motion(long interval){
		// 1. 位置を動かす
		((Solid3D)body).move(interval, getGravity(), getGravityCenter());
		super.motion(interval);
	}
	
	
	/**
	 * 単位時間ごとの動作（衝突判定処理も行う）
	 * @param interval --- 前回呼び出されたときからの経過時間（ミリ秒単位）
	 * @param ground --- 地面（構造物）
	 */
	public void motion(long interval, Ground ground) {

		// 1. 位置を動かす
		((Solid3D)body).move(interval, getGravity(), getGravityCenter());

		if (animation != null) {
			// 2. アニメーションを実行
			if (animation.progress(interval) == false) {
				onEndAnimation();
			}
			
			// 3. 姿勢を変える
			body.apply(animation.getPose(), false);
		}

		// 4. 衝突判定 
		CollisionResult cr = PhysicsUtility.doesIntersect((Solid3D)body, ground);

		// 5. 衝突応答
		if (cr != null) {
			// 構造物にぶつかった、または接触している時
			if (cr.normal.dot(PhysicsUtility.vertical) > GeometryUtility.TOLERANCE) {
				// 上向きの面（＝地面）にぶつかった、または接触している時
				if (cr.length <= GeometryUtility.TOLERANCE) {
					// 地面に乗っている
					if (!(mode instanceof ModeOnGround)) {
						// 落ちてきて丁度乗った
						mode = modeOnGround;
						((ModeOnGround)mode).setNormal(cr.normal);
						onEndFall();
					}
				} else {
					// 地面にめり込んだ
					// 5.1. 押し戻す
					onIntersect(cr, interval);
					if (!(mode instanceof ModeOnGround)) {
						// 落ちてきてめり込んだ
						// 6. 地面モードにする
						mode = modeOnGround;
						((ModeOnGround)mode).setNormal(cr.normal);
						onEndFall();
					} else {
						// 歩いている途中で、アニメーションの関係で一瞬だけめり込んだ
						// または、歩いている途中で斜面に差し掛かった
						((ModeOnGround)mode).setNormal(cr.normal);
					}
				}
			} else if (cr.normal.dot(PhysicsUtility.vertical) >= -GeometryUtility.TOLERANCE) {
				// 垂直壁にめり込んだ
				// 5.1. 押し戻す
				onIntersect(cr, interval);
			} else {
				// 下からぶつかった、または接触した(頭をぶつけた)
				if (cr.length > GeometryUtility.TOLERANCE) {
					// 5.1. 押し戻す
					onIntersect(cr, interval);				
				}
			}
			cr = null;
		} else {
			// 地面とぶつかっても接触してもいない時
			// 6. 落下モードにする
			mode = modeFreeFall;
		}
	}
	
	public void motion(long interval, Ground ground,ArrayList<Force3D> forces,
			ArrayList<Position3D> appPoints) {
		
		forces.add(getGravity());
		appPoints.add(getGravityCenter());

		// 1. 位置を動かす
		((Solid3D)body).move(interval, forces, appPoints);

		if (animation != null) {
			// 2. アニメーションを実行
			if (animation.progress(interval) == false) {
				onEndAnimation();
			}
			
			// 3. 姿勢を変える
			body.apply(animation.getPose(), false);
		}

		// 4. 衝突判定 
		CollisionResult cr = PhysicsUtility.doesIntersect((Solid3D)body, ground);

		// 5. 衝突応答
		if (cr != null) {
			// 構造物にぶつかった、または接触している時
			if (cr.normal.dot(PhysicsUtility.vertical) > GeometryUtility.TOLERANCE) {
				// 上向きの面（＝地面）にぶつかった、または接触している時
				if (cr.length <= GeometryUtility.TOLERANCE) {
					// 地面に乗っている
					if (!(mode instanceof ModeOnGround)) {
						// 落ちてきて丁度乗った
						mode = modeOnGround;
						((ModeOnGround)mode).setNormal(cr.normal);
						onEndFall();
					}
				} else {
					// 地面にめり込んだ
					// 5.1. 押し戻す
					onIntersect(cr, interval);
					if (!(mode instanceof ModeOnGround)) {
						// 落ちてきてめり込んだ
						// 6. 地面モードにする
						mode = modeOnGround;
						((ModeOnGround)mode).setNormal(cr.normal);
						onEndFall();
					} else {
						// 歩いている途中で、アニメーションの関係で一瞬だけめり込んだ
						// または、歩いている途中で斜面に差し掛かった
						((ModeOnGround)mode).setNormal(cr.normal);
					}
				}
			} else if (cr.normal.dot(PhysicsUtility.vertical) >= -GeometryUtility.TOLERANCE) {
				// 垂直壁にめり込んだ
				// 5.1. 押し戻す
				onIntersect(cr, interval);
			} else {
				// 下からぶつかった、または接触した(頭をぶつけた)
				if (cr.length > GeometryUtility.TOLERANCE) {
					// 5.1. 押し戻す
					onIntersect(cr, interval);				
				}
			}
			cr = null;
		} else {
			// 地面とぶつかっても接触してもいない時
			// 6. 落下モードにする
			mode = modeFreeFall;
		}
	}
	
	public void setInitialDirection(Vector3d dir) {
		direction = dir;
	}
	
	/**
	 * 指定した方向に向かせる
	 * @param vec 新しい向き
	 */
	public void setDirection(Vector3d vec) {
		Vector3d v1 = new Vector3d();
		Vector3d v2 = new Vector3d();
		v1.cross(direction, Y_AXIS);
		v2.cross(vec, Y_AXIS);
		if (v2.length() < GeometryUtility.TOLERANCE) return;
		v1.normalize();
		v2.normalize();
		double cos = v1.dot(v2);
		v1.cross(v1, v2);
		double sin = v1.dot(Y_AXIS);
		double angle = Math.atan2(sin, cos);
		AxisAngle4d axisAngle = new AxisAngle4d(Y_AXIS, angle); 
		Quaternion3D quat = new Quaternion3D(axisAngle);
		((Solid3D)body).apply(quat, false);
	}
	
	/**
	 * 現在向いている方向を取得する
	 * @return 現在の向き
	 */
	public Vector3d getDirection() {
		Vector3d dir = new Vector3d(direction);
		Transform3D trans = new Transform3D();
		trans.set(((Solid3D)body).getQuaternion().getAxisAngle());
		trans.transform(dir);
		return dir;
	}	
	
	/**
	 * 移動速度ベクトルを設定する
	 * @param vel 新しい移動速度ベクトル
	 */
	public void setVelocity(Velocity3D vel) {
		((Solid3D)body).apply(vel, false);
	}
	
	/**
	 * 移動速度ベクトルを取得する
	 * @return 現在の移動速度ベクトル
	 */
	public Velocity3D getVelocity() {
		return ((Solid3D)body).getVelocity();
	}
	
	/**
	 * X軸を中心に回転する
	 * @param angle 回転角（反時計回り, 単位:ラジアン）
	 */
	public void rotX(double angle) {
		Quaternion3D curQuat = body.getQuaternion();
		curQuat.add(new AxisAngle4d(X_AXIS, angle));
		body.apply(curQuat, false);		
	}
	
	/**
	 * Y軸を中心に回転する
	 * @param angle 回転角（反時計回り, 単位:ラジアン）
	 */
	public void rotY(double angle) {
		Quaternion3D curQuat = body.getQuaternion();
		curQuat.add(new AxisAngle4d(Y_AXIS, angle));
		body.apply(curQuat, false);		
	}
	
	/**
	 * Z軸を中心に回転する
	 * @param angle 回転角（反時計回り, 単位:ラジアン）
	 */
	public void rotZ(double angle) {
		Quaternion3D curQuat = body.getQuaternion();
		curQuat.add(new AxisAngle4d(Z_AXIS, angle));
		body.apply(curQuat, false);		
	}

	/**
	 * 加わっている重力を取得する
	 * @return 重力
	 */
	public Force3D getGravity() {
		return mode.getForce((Solid3D)body);
	}

	/**
	 * 重心を取得する
	 * @return 重心位置
	 */
	public Position3D getGravityCenter() {
		return ((Solid3D)body).getGravityCenter();
	}

	/**
	 * 地面に乗っている状態か否かを取得する
	 * @return true --- 地面に乗っている, false --- 地面に乗っていない（空中にいる）
	 */
	public boolean isOnGround() {
		return (mode instanceof ModeOnGround);
	}

	/**
	 * 地面（構造物）に落下した瞬間に呼び出される
	 */
	public abstract void onEndFall();

	/**
	 * 地面（構造物）に衝突した瞬間に呼び出される
	 * @param normal --- 地面の法線方向ベクトル
	 * @param interval --- 前回の動作からの経過時間（ミリ秒単位）
	 */
	public abstract void onIntersect(CollisionResult normal, long interval);

}
