package template.racing;

import java.util.ArrayList;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import framework.animation.Animation3D;
import framework.gameMain.OvergroundActor;
import framework.model3D.Position3D;
import framework.physics.AngularVelocity3D;
import framework.physics.Force3D;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;
import framework.physics.Velocity3D;

public class Automobile extends OvergroundActor {
	double frontWheelRot; // 前輪角度
	double maxSpeed = 90.0;		// 最高速度(m/s)
	double maxSteering = Math.PI * 0.25;	// 最大舵角
	double mass = 1100;			// 車重

	static final double halfWheelbase = 2.45 / 2.0;	// ホイールベース
	static final double a = 8041.0; // 駆動力
	static final double b = 88.9; // 制動減速度（127×摩擦係数）
	static final double b2 = 10.0; // 自然減速係数
	static final double s = 3.0; // サイドフォース最大静止摩擦係数
	static final double s2 = 0.8; // サイドフォース動摩擦係数
	static final Vector3d eX = new Vector3d(1.0, 0.0, 0.0);
	static final Vector3d eY = new Vector3d(0.0, 1.0, 0.0);

	ArrayList<Force3D> forces = new ArrayList<Force3D>();
	ArrayList<Position3D> appPoints = new ArrayList<Position3D>();

	public static final double EPSILON = 2.220446049250313E-16d;

	public Automobile(Solid3D body, Animation3D animation) {
		super(body, animation);
		body.setMass(mass); // 車重1t
		setInitialDirection(eX);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
		appPoints.add(body.getGravityCenter());
	}

	/**
	 * アクセルを踏んで加速する
	 */
	public void accelrate() {
		// 駆動力の計算（前輪が向いている方向に加速する）
		if (getFrontWheelVelocity().getVector3d().length() > maxSpeed) return;	// 最高速度を超えた場合
		Vector3d frontWheelDir = getFrontWheelDir();	// 前輪の方向ベクトル
		frontWheelDir.scale(a);
		forces.set(0, new Force3D(frontWheelDir));

		// 前輪の位置（駆動力の作用点になる）
		appPoints.set(0, getFrontPosition());
	}

	/**
	 * ブレーキを踏んで減速する
	 */
	public void brake() {
		// 前輪の制動力の計算
		Vector3d frontWheelDir = getFrontWheelDir();	// 前輪の方向ベクトル
		double dir = frontWheelDir.dot(getFrontWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		frontWheelDir.scale(-b * dir * ((Solid3D)body).getMass());
		forces.set(3, new Force3D(frontWheelDir));

		// 前輪の位置（前輪の制動力の作用点になる）
		appPoints.set(3, getFrontPosition());

		// 後輪の制動力の計算
		Vector3d rearWheelDir = getDirection(); // 後輪の方向ベクトル
		dir = rearWheelDir.dot(getRearWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		rearWheelDir.scale(-b * dir * ((Solid3D)body).getMass());
		forces.set(4, new Force3D(rearWheelDir));

		// 後輪の位置（後輪の制動力の作用点になる）
		appPoints.set(4, getRearPosition());
	}

	/**
	 * アクセルもブレーキも離してエンジンブレーキを掛ける
	 * @param interval
	 */
	public void engineBrake(long interval) {
		// 前輪の制動力の計算
		Vector3d frontWheelDir = getFrontWheelDir();	// 前輪の方向ベクトル
		double dir = frontWheelDir.dot(getFrontWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		frontWheelDir.scale(-b2 * dir * ((Solid3D)body).getMass());
		forces.set(3, new Force3D(frontWheelDir));

		// 前輪の位置（前輪の制動力の作用点になる）
		appPoints.set(3, getFrontPosition());

		// 後輪の制動力の計算
		Vector3d rearWheelDir = getDirection(); // 後輪の方向ベクトル
		dir = rearWheelDir.dot(getRearWheelVelocity().getVector3d());
		if (dir > EPSILON) {
			dir = 1.0;
		} else if (dir < -EPSILON) {
			dir = -1.0;
		} else {
			dir = 0.0;
		}
		rearWheelDir.scale(-b2 * dir * ((Solid3D)body).getMass());
		forces.set(4, new Force3D(rearWheelDir));

		// 後輪の位置（後輪の制動力の作用点になる）
		appPoints.set(4, getRearPosition());
	}

	/**
	 * ハンドルを左に切る
	 * @param interval
	 */
	public void turnLeft(long interval) {
		if (frontWheelRot <= -maxSteering) {
			// 最大舵角を超えた
			frontWheelRot = -maxSteering;
			return;
		}
		frontWheelRot -= 4.0 * (double) (interval / 1000.0); // 前輪の回転角度

	}

	/**
	 * ハンドルを右に切る
	 * @param interval
	 */
	public void turnRight(long interval) {
		if (frontWheelRot >= maxSteering) {
			// 最大舵角を超えた
			frontWheelRot = maxSteering;
			return;
		}
		frontWheelRot += 4.0 * (double) (interval / 1000.0); // 前輪の回転角度
	}

	/**
	 * ハンドルを離して自然に元の位置に復元させる
	 * @param interval
	 */
	public void steeringSelfCentering(long interval) {
		if (frontWheelRot > EPSILON) {
			frontWheelRot -= 2.0 * (double) (interval / 1000.0);

		} else if (frontWheelRot < -EPSILON) {
			frontWheelRot += 2.0 * (double) (interval / 1000.0);

		} else {
			frontWheelRot = 0.0;
		}
	}

	public void motion(long interval, Ground ground) {
		if (interval == 0L) return;
		
		// 前輪によるサイドフォースの計算
		Vector3d frontWheelDir = getFrontWheelDir();	// 前輪の方向ベクトル
		Vector3d frontWheelSideDir = new Vector3d();
		frontWheelSideDir.cross(eY, frontWheelDir);			// 前輪の横方向ベクトル
		Vector3d frontWheelVelocity = getFrontWheelVelocity().getVector3d();
		double frontWheelSideSlip = frontWheelSideDir.dot(frontWheelVelocity);	// 前輪の横滑り速度
		double omega = ((Solid3D)body).getAngularVelocity().getVector3d().dot(eY);	// 角速度
		frontWheelSideSlip -= frontWheelDir.dot(frontWheelVelocity) * Math.tan(omega / 2.0 * ((double)interval / 1000.0));	// 車体の回転に伴う前輪の回転の効果を加味する
		double frontWheelSideForce;		// 前輪の横方向の摩擦力
		// 静止摩擦係数の計算
		double frontWheelSideAccel = frontWheelSideSlip / ((double)interval / 1000.0);	// 滑っていない状態から加速したと仮定する
		if (frontWheelSideAccel > s * PhysicsUtility.GRAVITY) {
			// 最大静止摩擦係数を超えた場合、動摩擦力の計算に切り替え
				frontWheelSideForce = -s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("前輪予測横滑り速度(動摩擦に切り替え):" + frontWheelSideAccel);
		} else if (frontWheelSideAccel < -s * PhysicsUtility.GRAVITY) {
			// 最大静止摩擦係数を超えた場合、動摩擦力の計算に切り替え
				frontWheelSideForce = s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("前輪予測横滑り速度(動摩擦に切り替え):" + frontWheelSideAccel);
		} else {
			// 最大静止摩擦係数以内の場合（横向きの速度を打ち消す）
			frontWheelSideForce = -frontWheelSideAccel * ((Solid3D)body).getMass();
//System.out.println("前輪予測横滑り速度:" + frontWheelSideAccel);
		}
		frontWheelSideDir.scale(frontWheelSideForce);
		forces.set(1, new Force3D(frontWheelSideDir));

		// 前輪の位置（前輪に対するサイドフォースの作用点になる）
		appPoints.set(1, getFrontPosition());

		// 後輪によるサイドフォースの計算
		Vector3d rearWheelDir = getDirection(); // 後輪の方向ベクトル
		Vector3d rearWheelSideDir = new Vector3d();
		rearWheelSideDir.cross(eY, rearWheelDir); // 後輪の横方向ベクトル
		Vector3d rearWheelVelocity = getRearWheelVelocity().getVector3d();
		double rearWheelSideSlip = rearWheelSideDir.dot(rearWheelVelocity);	// 後輪の横滑り速度
		rearWheelSideSlip -= rearWheelDir.dot(rearWheelVelocity) * Math.tan(omega / 2.0 * ((double)interval / 1000.0));	// 車体の回転に伴う後輪の回転の効果を加味する
		double rearWheelSideForce;		// 後輪の横方向の摩擦力
		// 静止摩擦係数の計算
		double rearWheelSideAccel = rearWheelSideSlip / ((double)interval / 1000.0);	// 滑っていない状態から加速したと仮定する
		if (rearWheelSideAccel > s * PhysicsUtility.GRAVITY) {
			// 最大静止摩擦係数を超えた場合、動摩擦力の計算に切り替え
				rearWheelSideForce = -s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("後輪予測横滑り速度(動摩擦に切り替え):" + rearWheelSideAccel);
		} else if (rearWheelSideAccel < -s * PhysicsUtility.GRAVITY) {
			// 最大静止摩擦係数を超えた場合、動摩擦力の計算に切り替え
				rearWheelSideForce = s2 * PhysicsUtility.getGravity((Solid3D)body).getSeverity();
//System.out.println("後輪予測横滑り速度(動摩擦に切り替え):" + rearWheelSideAccel);
		} else {
			// 最大静止摩擦係数以内の場合（横向きの速度を打ち消す）
			rearWheelSideForce = -rearWheelSideAccel * ((Solid3D)body).getMass();
//System.out.println("後輪予測横滑り速度:" + rearWheelSideAccel);
		}
		rearWheelSideDir.scale(rearWheelSideForce);
		forces.set(2, new Force3D(rearWheelSideDir));

		// 後輪の位置（後輪に対するサイドフォースの作用点になる）
		appPoints.set(2, getRearPosition());
		
//System.out.println(frontWheelRot);

		// 物理演算、衝突判定、衝突応答
		super.motion(interval, ground, forces, appPoints);

		// 計算が終わったらクリアし次回に備える
		forces.clear();
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		forces.add(Force3D.ZERO);
		appPoints.clear();
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
		appPoints.add(((Solid3D) body).getGravityCenter());
	}

	/**
	 * 前輪の位置を求める
	 * @return 前輪の位置
	 */
	private Position3D getFrontPosition() {
		Vector3d front = ((Solid3D) body).getGravityCenter().getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(halfWheelbase);
		front.add(direction);
		return new Position3D(front);
	}

	/**
	 * 後輪の位置を求める
	 * @return 後輪の位置
	 */
	private Position3D getRearPosition() {
		Vector3d rear = ((Solid3D) body).getGravityCenter().getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(-halfWheelbase);
		rear.add(direction);
		return new Position3D(rear);
	}

	/**
	 * 前輪の方向ベクトルを求める
	 * @return 前輪の方向ベクトル
	 */
	private Vector3d getFrontWheelDir() {
		Vector3d ftV;
		ftV = getDirection();
		Transform3D trans = new Transform3D();
		trans.rotY(-frontWheelRot);
		trans.transform(ftV);
		return ftV;
	}
	
	/**
	 * 前輪の速度ベクトルを求める
	 * @return 前輪の速度ベクトル
	 */
	private Velocity3D getFrontWheelVelocity() {
		Velocity3D velocity = ((Solid3D) body).getVelocity();
		Vector3d angularVelocity = ((Solid3D) body).getAngularVelocity()
				.getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(halfWheelbase);
		angularVelocity.cross(angularVelocity, direction); // 角速度ベクトルと中心からの位置ベクトルの外積が自転による相対速度になる
		return velocity.add(angularVelocity);
	}

	/**
	 * 後輪の速度ベクトルを求める
	 * @return 後輪の速度ベクトル
	 */
	private Velocity3D getRearWheelVelocity() {
		Velocity3D velocity = ((Solid3D) body).getVelocity();
		Vector3d angularVelocity = ((Solid3D) body).getAngularVelocity()
				.getVector3d();
		Vector3d direction = getDirection();
		direction.normalize();
		direction.scale(-halfWheelbase);
		angularVelocity.cross(angularVelocity, direction); // 角速度ベクトルと中心からの位置ベクトルの外積が自転による相対速度になる
		return velocity.add(angularVelocity);
	}
}
