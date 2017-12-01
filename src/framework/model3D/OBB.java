package framework.model3D;

import java.util.ArrayList;

import javax.media.j3d.BoundingPolytope;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class OBB implements Cloneable {
	public ArrayList<Vector3d> vertexList = new ArrayList<Vector3d>();
	public Vector4d[] plane = new Vector4d[6];
	public BoundingPolytope bp = null;
	public static int edges[][] = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 2, 3 },
			{ 0, 4 }, { 1, 5 }, { 3, 7 }, { 2, 6 }, { 4, 5 }, { 4, 6 },
			{ 5, 7 }, { 6, 7 } };
	static boolean[][] inside = new boolean[8][6];
	static boolean[] inside2 = new boolean[8];


	public BoundingSphere getBoundingSphere() {
		double radius = 0.0;
		Point3d p = new Point3d();
		Vector3d cv = new Vector3d();
		for (int i = 0; i < vertexList.size() - 1; i++) {
			for (int j = i + 1; j < vertexList.size(); j++) {
				Vector3d v = new Vector3d();
				v.sub(vertexList.get(i), vertexList.get(j));
				if (radius < v.length()) {
					radius = v.length();
					cv.add(vertexList.get(i), vertexList.get(j));
					cv.scale(0.5);
					p.x = cv.x;
					p.y = cv.y;
					p.z = cv.z;
				}
			}
		}
		BoundingSphere s = new BoundingSphere(p, radius / 2);
		return s;
	}

	/**
	 * 平面との衝突判定
	 * @param plane 衝突判定の対象となる平面
	 * @return 衝突判定の結果（衝突していない場合はnull）
	 */
	public CollisionResult intersect(Vector4d plane) {
		int i = 0;
		boolean inside = false;
		boolean outside = false;
		int count = 0;
		Vector3d center = new Vector3d(0, 0, 0);
		for (i = 0; i < vertexList.size(); i++) {
			if (GeometryUtility.inside(vertexList.get(i), plane)) {
				inside = true;
				center.add(vertexList.get(i));
				count++;
			} else {
				outside = true;				
			}
		}

		if (!inside || !outside) {
			// 全頂点が外側か全頂点が内側の場合
			return null;
		}

		center.scale(1.0 / count);

		double length = Math.sqrt(plane.x * plane.x + plane.y * plane.y
				+ plane.z * plane.z);
		double l = -1.0
				* (center.x * plane.x + center.y * plane.y + center.z * plane.z + plane.w)
				/ length * 0.5;
		CollisionResult cr = new CollisionResult();
		cr.length = l;
		cr.collisionPoint.setX(center.x + l * plane.x / length);
		cr.collisionPoint.setY(center.y + l * plane.y / length);
		cr.collisionPoint.setZ(center.z + l * plane.z / length);
		cr.normal.setX(plane.x / length);
		cr.normal.setY(plane.y / length);
		cr.normal.setZ(plane.z / length);

		return cr;
	}

	/**
	 * OBBとの衝突判定
	 * @param o 衝突判定の対象となるOBB
	 * @return 衝突判定の結果（衝突していない場合はnull）
	 */
	public CollisionResult intersect(OBB o) {
		// OBBの頂点とintersectするOBBのとの衝突点が内部にあるかチェックメソッド
		// あったらその衝突点と深さを返す
		for (int i = 0; i < o.vertexList.size(); i++) {
			for (int j = 0; j < plane.length; j++) {
				inside[i][j] = GeometryUtility.inside(o.vertexList.get(i), plane[j]);
			}
		}

		// i：頂点 j：面
		boolean collision = false;
		int count = 0;
		Vector3d center = new Vector3d(0, 0, 0);

		// 頂点が相手に包含されているか？
		for (int v = 0; v < 8; v++) {
			boolean f1 = false;
			for (int p = 0; p < 6; p++) {
				f1 = inside[v][p];
				if (!f1) {
					break;
				}
			}
			inside2[v] = f1;
			if (f1) {
				collision = true;
				center.add(o.vertexList.get(v));
				count++;
			}
		}

		if (!collision) {
			// 辺が相手の面と交わっているか？
			for (int p = 0; p < 6; p++) {
				Vector3d intersection = null;
				for (int e = 0; e < 12; e++) {
					if (inside[edges[e][0]][p] == !inside[edges[e][1]][p]) {
						intersection = GeometryUtility.intersect(plane[p],
								o.vertexList.get(edges[e][0]), o.vertexList
										.get(edges[e][1]));
						boolean bInside = true;
						for (int p2 = 0; p2 < 6; p2++) {
							if (p != p2
									&& !GeometryUtility.inside(intersection,
											plane[p2])) {
								bInside = false;
								break;
							}
						}
						if (bInside) {
							collision = true;
							center.add(intersection);
							count++;
						}
					}
				}
			}
		}

		if (!collision)
			return null;

		center.scale(1.0 / count);

		CollisionResult cr = new CollisionResult();

		double lMin = 0;
		int cm = -1;
		Vector3d normal = null;
		for (int m = 0; m < plane.length; m++) {
			Vector3d n = new Vector3d(plane[m].x, plane[m].y, plane[m].z);
			double l = -1.0
					* (center.x * plane[m].x + center.y * plane[m].y + center.z
							* plane[m].z + plane[m].w) / n.length() * 0.5;
			// Math.sqrt((Math.pow(center.x, 2) + Math.pow(center.y, 2) +
			// Math.pow(
			// center.z, 2)));
			if (lMin > l || cm == -1) {
				lMin = l;
				cm = m;
				normal = n;
			}
		}
		cr.length = lMin;
		// System.out.println("cr.length"+cr.length);
		cr.collisionPoint.setX(center.x + lMin * plane[cm].x / normal.length());
		cr.collisionPoint.setY(center.y + lMin * plane[cm].y / normal.length());
		cr.collisionPoint.setZ(center.z + lMin * plane[cm].z / normal.length());
		cr.normal.setX(plane[cm].x / normal.length());
		cr.normal.setY(plane[cm].y / normal.length());
		cr.normal.setZ(plane[cm].z / normal.length());
		// System.out.println("cr.collisionPoint"+cr.collisionPoint.getX());
		// System.out.println("cr.collisionPoint"+cr.collisionPoint.getY());
		// System.out.println("cr.collisionPoint"+cr.collisionPoint.getZ());

		return cr;

	}

	public Object clone() {
		OBB obb = new OBB();
		for (int i = 0; i < plane.length; i++) {
			obb.plane[i] = (Vector4d) plane[i].clone();

		}
		for (int i = 0; i < vertexList.size(); i++) {
			obb.vertexList.add((Vector3d) vertexList.get(i).clone());
		}
		obb.bp = new BoundingPolytope(obb.plane);

		// System.out.println("veretexListSIZE:"+vertexList.size());
		return obb;
	}

	public void transform(Transform3D t) {
		// TODO Auto-generated method stub
		bp.transform(t);
		bp.getPlanes(plane);
		// for(int i = 0; i<plane.length;i++){
		// System.out.println("plane");
		// System.out.print(plane[i].x + "," + plane[i].y + "," + plane[i].z +
		// "," + plane[i].w);
		// t.transform(plane[i]);
		// System.out.println("-->" + plane[i].x + "," + plane[i].y + "," +
		// plane[i].z + "," + plane[i].w);
		// }
		for (int i = 0; i < vertexList.size(); i++) {
			// System.out.println("vertex");
			// System.out.print(vertexList.get(i).x + "," + vertexList.get(i).y
			// + "," + vertexList.get(i).z);
			Matrix4d mat4d = new Matrix4d();
			t.get(mat4d);
			double x = mat4d.m00 * vertexList.get(i).x + mat4d.m01
					* vertexList.get(i).y + mat4d.m02 * vertexList.get(i).z
					+ mat4d.m03;
			double y = mat4d.m10 * vertexList.get(i).x + mat4d.m11
					* vertexList.get(i).y + mat4d.m12 * vertexList.get(i).z
					+ mat4d.m13;
			double z = mat4d.m20 * vertexList.get(i).x + mat4d.m21
					* vertexList.get(i).y + mat4d.m22 * vertexList.get(i).z
					+ mat4d.m23;
			vertexList.get(i).x = x;
			vertexList.get(i).y = y;
			vertexList.get(i).z = z;
			// t.transform(vertexList.get(i));
			// System.out.println("-->" + vertexList.get(i).x + "," +
			// vertexList.get(i).y + "," + vertexList.get(i).z);
		}
	}
}
