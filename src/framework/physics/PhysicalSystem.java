package framework.physics;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import framework.model3D.CollisionResult;
import framework.model3D.Position3D;


public class PhysicalSystem {

	public ArrayList<Solid3D> objects = new ArrayList<Solid3D>();

	// 物体の挿入
	public  int add(Solid3D s) {
		objects.add(s);
		return objects.size() - 1;
	}

	// 物体の運動
	public void motion(int id, long interval, Force3D f, Position3D appPoint, Ground ground) {
		ArrayList<Force3D> forces[] = new ArrayList[objects.size()];
		ArrayList<Position3D> appPoints[] = new ArrayList[objects.size()];
		for (int i = 0; i < objects.size(); i++) {
			forces[i] = new ArrayList<Force3D>();
			appPoints[i] = new ArrayList<Position3D>();
		}
		
		// id番目の外力の計算
		forces[id].add(f);
		appPoints[id].add(appPoint);
//		objects.get(id).move(interval, f, appPoint); 

		double l; // ばねの伸び

		Force3D penalty = new Force3D(0.0, 0.0, 0.0); // ペナルティ法によるペナルティの作用の力
		Force3D inversepenalty; //
		// //ペナルティ法によるペナルティの反作用の力
		CollisionResult cr;
		for (int n = 0; n < objects.size(); n++) {
			// 重力の計算
			forces[n].add(PhysicsUtility.getGravity(objects.get(n)));
			appPoints[n].add(objects.get(n).getGravityCenter());
//			objects.get(n).move(interval,
//					PhysicsFacade.getGravity(objects.get(n)),
//					objects.get(n).getGravityCenter()); // 重力の計算
			// 地面との当たり判定
			cr = PhysicsUtility.doesIntersect(objects.get(n), ground);
			// 地面に物体がめり込んでいる場合
			if (cr != null) {
				double gk = 100000; // 地面でのばね係数
				double e = 0.6; // 地面での跳ね返り時の抵抗係数
				double b = 1.0;
				l = cr.length;
				// <作用の力の計算>
				// ペナルティの変数
				Vector3d v = cr.normal;
				v.scale(gk * l);
				// 作用点ベクトルの作成
				Vector3d app_point = cr.collisionPoint.getVector3d();
				// (作用点-重心)ベクトル
				app_point.sub(objects.get(n).getGravityCenter().getVector3d());
				// 角速度ベクトルの作成
				Vector3d angular_v = objects.get(n).getAngularVelocity()
						.getVector3d();
				// 角速度ベクトルと(作用点-重心)ベクトルの外積計算
				angular_v.cross(angular_v, app_point);
				// 速度ベクトル+角速度ベクトルと(作用点-重心)ベクトルの外積計算
				Vector3d relation_v = objects.get(n).getVelocity()
						.getVector3d();
				// 相対速度ベクトルの作成
				relation_v.add(angular_v);
				// 相対速度ベクトルのe(跳ね返り時の抵抗係数)倍
				relation_v.scale(b * (1 + e) / e *  (1/ objects.get(n).mass));
				relation_v.scale( 1.0 / ((double) interval / 1000.0));
				// ペナルティの大きさ決定
				v.sub(relation_v);
				// penaltyにvを挿入
				penalty = new Force3D(v);

				// 作用の力による運動
				forces[n].add(penalty);
				appPoints[n].add(cr.collisionPoint);
//				objects.get(n).move(interval, penalty, cr.collisionPoint);
			}
			// 地面に物体がめり込んでいない場合
			else {
			}
			for (int m = 0; m < n; m++) {
				Solid3D s1 = objects.get(n);
				Solid3D s2 = objects.get(m);
				cr = PhysicsUtility.checkCollision(s1, null, s2, null);
				// 物体がめり込んでいる場合
				if (cr != null) {
					double sk = 100000; // 物体でのばね係数
					double e = 0.6; // 物体での跳ね返り時の抵抗係数
					double b = 1.0;
					l = cr.length;
					// <作用の力の計算>
					// ペナルティの変数
					Vector3d v = cr.normal;
					v.scale(sk * l);
					// 作用点ベクトルの作成
					Vector3d app_point = cr.collisionPoint.getVector3d();
					// (作用点-s2の重心)ベクトル
					app_point.sub(s2.getGravityCenter().getVector3d());
					// s2の角速度ベクトルの作成
					Vector3d s2_angular_v = s2.getAngularVelocity()
							.getVector3d();
					// s2の角速度ベクトルと(作用点-s2の重心)ベクトルの外積計算
					s2_angular_v.cross(s2_angular_v, app_point);
					// s2の速度ベクトルの作成
					Vector3d relative_v = s2.getVelocity().getVector3d();
					// s2の速度ベクトル+s2の角速度ベクトルと(作用点-s2の重心)ベクトルの外積計算
					relative_v.add(s2_angular_v);
					// (作用点-s1の重心)ベクトル
					app_point = cr.collisionPoint.getVector3d();
					app_point.sub(s1.getGravityCenter().getVector3d());
					// s1の角速度ベクトルの作成
					Vector3d s1_angular_v = s1.getAngularVelocity()
							.getVector3d();
					// s1の角速度ベクトルと(作用点-s1の重心)ベクトルの外積計算
					s1_angular_v.cross(s1_angular_v, app_point);
					// s1の速度ベクトルの作成
					Vector3d s1_v = s1.getVelocity().getVector3d();
					// s1の速度ベクトル+s1の角速度ベクトルと(作用点-s1の重心)ベクトルの外積計算
					s1_v.add(s1_angular_v);
					// 相対速度ベクトルの作成
					relative_v.sub(s1_v);
					// 相対速度ベクトルのe(跳ね返り時の抵抗係数)倍
					relative_v.scale(b * (1 + e) / e*(s1.mass + s2.mass) / (s1.mass * s2.mass));
					relative_v.scale( 1.0 / ((double) interval / 1000.0));
					// ペナルティの大きさ決定
					v.add(relative_v);
					// penaltyにvを挿入
					penalty = new Force3D(v);

					// 反作用の力の計算
					v.scale(-1);
					inversepenalty = new Force3D(v);

					// 作用の力による物体の移動
					forces[n].add(penalty);
					appPoints[n].add(cr.collisionPoint);
//					s1.move(interval, penalty, cr.collisionPoint);

					// 反作用の力による物体の移動
					forces[m].add(inversepenalty);
					appPoints[m].add(cr.collisionPoint);
//					s2.move(interval, inversepenalty, cr.collisionPoint);
				}
//				// 物体がめり込んでいない場合
//				else {
//					s2.move(interval, f, s2.getGravityCenter());
//					s1.move(interval, f, s1.getGravityCenter());
//				}
			}
		}
		for (int n2 = 0; n2 < objects.size(); n2++) {
			objects.get(n2).move(interval, forces[n2], appPoints[n2]);				
		}
	}
}
