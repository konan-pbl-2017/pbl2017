package framework.physics;

import java.util.ArrayList;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;

/**
 * 物理的な振る舞いをする物体（剛体）を表す
 * @author 新田直也
 *
 */
public class Solid3D extends Object3D {
	private Velocity3D velocity;
	private AngularVelocity3D angularvelocity;
//	private Inertia3D inertia = new Inertia3D(this);
	private Position3D gravityCenter = getPosition3D();
	double mass = 10;
	double e = 1.0;

	// コピーコンストラクタ
	public Solid3D(Object3D obj) {
		super(obj);
		velocity = new Velocity3D();
		quaternion = new Quaternion3D();
		angularvelocity = new AngularVelocity3D();
	}

	public Solid3D(Solid3D solid) {
		super(solid);
		velocity = new Velocity3D(solid.velocity);
		quaternion = new Quaternion3D(solid.getQuaternion());
		angularvelocity = new AngularVelocity3D(solid.angularvelocity);
	}

	/**
	 * 力学運動の計算（加わる力が1つの場合）
	 * @param interval 単位時間
	 * @param f 力
	 * @param applicationPoint 力の作用点
	 */
	public void move(long interval, Force3D f, Position3D applicationPoint) {
		// モーメントの計算
		Vector3d moment = PhysicsUtility.calcMoment(f, getGravityCenter(),
				applicationPoint);
		moveSub(interval, f, moment);
	}
	
	/**
	 * 力学運動の計算（同時に複数の力が加わる場合）
	 * @param interval 単位時間
	 * @param forces 力（複数）
	 * @param appPoints それぞれの力の作用点
	 */
	public void move(long interval, ArrayList<Force3D> forces,
			ArrayList<Position3D> appPoints) {
		// 重心に加わる力の合計を求める
		Force3D f = new Force3D(0.0, 0.0, 0.0);
		for (int n = 0; n < forces.size(); n++) {
			f.add(forces.get(n));
		}
		
		// モーメントの合計を計算する
		Position3D gc = getGravityCenter();
		Vector3d moment = new Vector3d(0.0, 0.0, 0.0);
		for (int n2 = 0; n2 < forces.size(); n2++) {
			moment.add(PhysicsUtility.calcMoment(forces.get(n2), gc, appPoints.get(n2)));
		}
		moveSub(interval, f, moment);		
	}
	
	private void moveSub(long interval, Force3D f, Vector3d moment) {
		// 1.重心の運動方程式（ニュートン方程式）
		Vector3d deltaP = velocity.getVector3d(); // 速度ベクトルの取得
		deltaP.scale(((double) interval / 1000.0));
		Position3D p = getPosition3D().add(deltaP); // 位置に差分を加算
		apply(p, false);

		// 加速度計算
		Vector3d deltaV = f.getVector3d(); // 力ベクトルの取得
		deltaV.scale(1.0 / mass * ((double) interval / 1000.0)); // 加速度から速度の差分を計算
		Velocity3D v = getVelocity().add(deltaV); // 速度に差分を加算
		apply(v, false);
		
		// 2.オイラーの角運動方程式

//		// 角加速度計算
//		Vector3d deltaAngularV = new Vector3d(moment.x / inertia.ixx,
//				moment.y / inertia.iyy, moment.z / inertia.izz);
//		deltaAngularV.scale(((double) interval / 1000.0 / mass));
//		AngularVelocity3D w = getAngularVelocity().add(deltaAngularV);
//		apply(w, false);
//
//		// 角速度による回転
//		AxisAngle4d axisAngle = w.getAxisAngle4d();
//		axisAngle.angle *= ((double) interval / 1000.0);
//		Quaternion3D q = getQuaternion().add(axisAngle);
//		apply(q, false);		
	}

	// 複製を作る
	public Object3D duplicate() {
		Object3D copy = new Solid3D(this);
		return copy;
	}

	public Velocity3D getVelocity() {
		return (Velocity3D) velocity.clone();
	}

	public AngularVelocity3D getAngularVelocity() {
		return (AngularVelocity3D) angularvelocity.clone();
	}

	// Velocity3D の applyTo 以外からは呼ばないこと
	void setVelocity(Velocity3D v) {
		velocity = (Velocity3D) v.clone();
	}

	// AngularVelocity3D の applyTo 以外からは呼ばないこと
	void setAngularVelocity(AngularVelocity3D w) {
		angularvelocity = (AngularVelocity3D) w.clone();
	}

	public void setGravityCenter(Position3D gravityCenter) {
		this.gravityCenter = gravityCenter;
	}

	public Position3D getGravityCenter() {
		return getPosition3D().add(gravityCenter);
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getMass() {
		return mass;
	}

}
