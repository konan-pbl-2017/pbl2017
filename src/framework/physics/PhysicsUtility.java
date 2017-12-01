package framework.physics;

import java.util.ArrayList;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import framework.model3D.BoundingSurface;
import framework.model3D.CollisionResult;
import framework.model3D.Object3D;
import framework.model3D.Position3D;


public class PhysicsUtility {
	public static final double GRAVITY = 9.8;
	public static final Vector3d horizon = new Vector3d(1.0, 0.0, 0.0);
	public static final Vector3d vertical = new Vector3d(0.0, 1.0, 0.0);

	public static int gameClassify = 0;
	public static final int RPG_GRAVITY_FLAG = 1;
	public static final int SHOOTING_GRAVITY_FLAG = 2;
	public static final int ACTION_GRAVITY_FLAG = 3;

	public static Vector3d gravityDirection = new Vector3d(0.0, 1.0, 0.0);

	/**
	 * @param body
	 *            : オブジェクトのボディ(Solid3D)
	 * @return force : GravityDirectionから求められる重力の力
	 */
	public static Force3D getGravity(Solid3D body) {
		return new Force3D(gravityDirection.x * -body.mass * GRAVITY, gravityDirection.y * -body.mass * GRAVITY, gravityDirection.z * -body.mass * GRAVITY);
	}

	/**
	 * @param v
	 *            ：単位ベクトル、あるいはゼロベクトルを引数で渡すこと
	 */
	public static void setGravityDirection(Vector3d v) {
		gravityDirection = new Vector3d(v.x, v.y, v.z);
	}

	// モーメントの計算
	static Vector3d calcMoment(Force3D f, Position3D gravityCenter,
			Position3D applicationPoint) {
		Vector3d v0 = new Vector3d(0.0, 0.0, 0.0);
		Vector3d v1 = new Vector3d(applicationPoint.getX(), applicationPoint
				.getY(), applicationPoint.getZ());
		Vector3d v2 = new Vector3d(gravityCenter.getX(), gravityCenter.getY(),
				gravityCenter.getZ());
		// Vector3d v1 = new Vector3d(11.0, 18.0, 50.0);
		// Vector3d v2 = new Vector3d(4.0, 5.0, 6.0);
		v0.sub(v1, v2);

		Vector3d cv = new Vector3d(0.0, 0.0, 0.0);
		Vector3d fv = new Vector3d(f.x, f.y, f.z);
		cv.cross(v0, fv);
		return cv;

	}

	public static Force3D calcForce(long interval, Vector3d nor, Solid3D solid) {
		double f1 = 0.0;
		Vector3d vf = new Vector3d(solid.getVelocity().getX(), solid
				.getVelocity().getY(), solid.getVelocity().getZ());
		f1 = solid.mass * (vf.length() + solid.e * vf.length())
				/ ((double) interval / 1000.0);
		nor.scale(f1);
		Force3D f = new Force3D(nor.x, nor.y, nor.z);
		return f;
	}

	// static CollisionResult isOnground(Solid3D obj, Object3D ground) {
	// obj.setUndoMark();
	// Position3D pos = obj.getPosition3D();
	// pos.add(0.0, -0.2, 0.0);
	// obj.apply(pos);
	// CollisionResult cr = doesIntersect(obj, ground);
	// obj.undo();
	// return cr;
	// }

	public static CollisionResult doesIntersect(Solid3D obj, Ground ground) {
		CollisionResult cr = null;
		BoundingSurface boundingSurface = ground.getBoundingSurface();

		// BoundingSphereを使って大雑把に衝突判定を行う
		ArrayList<BoundingSurface> boundingSurfaceList = null;
		if (obj.bs != null) {
			BoundingSphere bs = (BoundingSphere) (obj.bs.clone());
			Transform3D t3d = new Transform3D();
			obj.center.getTransform(t3d);
			bs.transform(t3d);
			obj.scale.getTransform(t3d);
			bs.transform(t3d);
			obj.rot.getTransform(t3d);
			bs.transform(t3d);
			obj.pos.getTransform(t3d);
			bs.transform(t3d);
			// 粗い衝突判定を行う（最上位のBoundingSurfaceとBoundingSphereの間で）
			boundingSurfaceList = boundingSurface.intersect(bs);
			bs = null;
			t3d = null;
		}

		if (obj.bs == null) {
			// BoundingSphere がまだ作られていななかった場合、
			// 詳細な衝突判定のために、最上位の全 BoundingSurface を取得する
			boundingSurfaceList = new ArrayList<BoundingSurface>();
			boundingSurfaceList.add(boundingSurface);
		}

		if (boundingSurfaceList.size() > 0) {
			// 粗い衝突判定で衝突していた場合、OBBの集合を用いてより詳しい衝突判定を行う
			// （BoundingSphere がまだ作られていない場合、OBB の作成と同時に BoundingSphere を作成される）
			BoundingBoxVisitor obbVisitor = new BoundingBoxVisitor();
			obj.accept(obbVisitor);
			for (int i = 0; i < obbVisitor.getObbList().size(); i++) {
				// OBBと衝突判定をする場合は、地面を多角形のまま扱う
				for (int j = 0; j < boundingSurfaceList.size(); j++) {
					cr = boundingSurfaceList.get(j).intersect(
							obbVisitor.getObbList().get(i));
					if (cr != null) {
						return cr;
					}
				}
			}
			obbVisitor = null;
		}
		return null;
	}

	public static CollisionResult checkCollision(Object3D obj1, String part1,
			Object3D obj2, String part2) {
		CollisionResult cr = null;

		// BoundingSphereを使って大雑把に衝突判定を行う
		boolean f = false;
		if (obj1.bs != null && obj2.bs != null) {
			// sol1 の BoundingSphere　を計算
			BoundingSphere bs1 = (BoundingSphere) (obj1.bs.clone());
			Transform3D t3d = new Transform3D();
			obj1.center.getTransform(t3d);
			bs1.transform(t3d);
			obj1.scale.getTransform(t3d);
			bs1.transform(t3d);
			obj1.rot.getTransform(t3d);
			bs1.transform(t3d);
			obj1.pos.getTransform(t3d);
			bs1.transform(t3d);

			// sol2 の BoundingSphere　を計算
			BoundingSphere bs2 = (BoundingSphere) (obj2.bs.clone());
			obj2.center.getTransform(t3d);
			bs2.transform(t3d);
			obj2.scale.getTransform(t3d);
			bs2.transform(t3d);
			obj2.rot.getTransform(t3d);
			bs2.transform(t3d);
			obj2.pos.getTransform(t3d);
			bs2.transform(t3d);

			// BoundingSphere 同士の衝突判定
			if (bs1.intersect(bs2))
				f = true;
			t3d = null;
			bs1 = null;
			bs2 = null;
		}
		if (f || obj1.bs == null || obj2.bs == null) {
			BoundingBoxVisitor visitor1 = new BoundingBoxVisitor(part1);
			BoundingBoxVisitor visitor2 = new BoundingBoxVisitor(part2);
			obj1.accept(visitor1);
			obj2.accept(visitor2);
			// OBB o1 = obj1.getOBB(0);
			// OBB o2 = obj2.getOBB(0);
			// cr = o1.intersect(o2);
			int i, j;
			for (i = 0; i < visitor1.getObbList().size(); i++) {
				for (j = 0; j < visitor2.getObbList().size(); j++) {
					cr = visitor2.getObbList().get(j).intersect(
							visitor1.getObbList().get(i));
					// System.out.println("flg:"+flg);
					if (cr != null) {
						// Vector3d v = new Vector3d(0,0,0);
						// System.out.println("checkColision1
						// Yes!!"+cr.collisionPoint.getX());
						// System.out.println("checkColision1
						// Yes!!"+cr.collisionPoint.getY());
						// System.out.println("checkColision1
						// Yes!!"+cr.collisionPoint.getZ());
						// return v;
						return cr;
					} else {
						cr = visitor1.getObbList().get(i).intersect(
								visitor2.getObbList().get(j));
						if (cr != null) {
							cr.normal.scale(-1.0);
							// Vector3d v = new Vector3d(0,0,0);
							// System.out.println("checkColision2
							// Yes!!"+cr.collisionPoint.getX());
							// System.out.println("checkColision2
							// Yes!!"+cr.collisionPoint.getY());
							// System.out.println("checkColision2
							// Yes!!"+cr.collisionPoint.getZ());
							// return v;
							return cr;
						}
					}
				}
			}
		}
		// System.out.println("Yes!!");
		return null;
	}

	// private static Vector3d Vector3d(Vector3d cv) {
	// // TODO Auto-generated method stub
	// return null;
	// }
}
