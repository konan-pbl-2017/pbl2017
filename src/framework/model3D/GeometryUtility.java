package framework.model3D;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.media.j3d.IndexedGeometryArray;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class GeometryUtility {
	public static final double TOLERANCE = 0.0000002;

	public static ProjectionResult projection3D(
			ArrayList<Vector3d> vertex3Dlist, Vector3d axis) {
		double k = 0.0;
		int i = 0;

		ProjectionResult pr = new ProjectionResult();

		axis.normalize();

		for (i = 0; i < vertex3Dlist.size(); i++) {
			Vector3d p = vertex3Dlist.get(i);
			Vector3d p1 = new Vector3d();

			k = p.dot(axis);
			p1.scaleAdd(-k, axis, p);

			if (i == 0 || k >= pr.max) {
				pr.max = k;
			} else if (i == 0 || k <= pr.min) {
				pr.min = k;
			}
			pr.vertexList.add(p1);
		}
		return pr;
	}

	public static ProjectionResult projection2D(
			ArrayList<Vector3d> vertex2Dlist, Vector3d axis) {
		int i = 0;
		double k = 0.0;
		// System.out.println("point3："+axis);
		if (axis.x != 0 || axis.y != 0 || axis.z != 0) {
			axis.normalize();
		}
		// System.out.println("point3_1："+axis);
		ProjectionResult pr = new ProjectionResult();

		for (i = 0; i < vertex2Dlist.size(); i++) {
			Vector3d p = vertex2Dlist.get(i);

			k = p.dot(axis);
			// System.out.println("k："+k);
			// System.out.println("point3："+axis);
			if (i == 0 || k >= pr.max) {
				pr.max = k;
			} else if (i == 0 || k <= pr.min) {
				pr.min = k;
			}
		}
		return pr;
	}

	// 以下、頂点の表裏の判定メソッド
	public static boolean inside(Vector3d v, Vector4d plane) {
		// System.out.println("vertex:" + v.x + "," + v.y + "," + v.z);
		// System.out.println("plane:" + plane.x + "," + plane.y + "," + plane.z
		// + "," + plane.w);
		Vector3d pv = new Vector3d(plane.x, plane.y, plane.z);
		// Vector3d nv = (Vector3d)pv.clone();
		// nv.scaleAdd(plane.w / pv.lengthSquared(),v);
		if (pv.dot(v) + plane.w <= TOLERANCE) {
			// System.out.println("内部判定！！"+nv.dot(pv));
			pv = null;
			return true;
		}
		// System.out.println("外部判定！！"+nv.dot(pv));
		pv = null;
		return false;
	}

	/**
	 * 3点を通る平面を作成する（ただし、v1, v2, v3の内容が書き換えられるので注意）
	 * 
	 * @param v1
	 *            --- 1点目の座標
	 * @param v2
	 *            --- 2点目の座標
	 * @param v3
	 *            --- 3点目の座標
	 * @return　v1, v2, v3を通る平面
	 */
	public static Vector4d createPlane(Vector3d v1, Vector3d v2, Vector3d v3) {
		v2.sub(v1);
		v3.sub(v1);
		v2.cross(v2, v3);
		v2.normalize();
		return new Vector4d(v2.x, v2.y, v2.z, -v2.dot(v1));
	}

	/**
	 * 
	 * 平面と直線の交点を求める
	 * 
	 * @param plane
	 *            平面
	 * @param v1
	 *            直線上の点1
	 * @param v2
	 *            直線状の点2
	 * @return 交点
	 */
	public static Vector3d intersect(Vector4d plane, Vector3d v1, Vector3d v2) {
		Vector3d n = new Vector3d(plane.x, plane.y, plane.z);
		Vector3d v21 = (Vector3d) v2.clone();
		v21.sub(v1);
		v21.scale((-plane.w - n.dot(v1)) / n.dot(v21));
		v21.add(v1);
		return v21;
	}

	/**
	 * 
	 * 指定された点が凸ポリゴンの内部に包含されているか？
	 * 
	 * @param vertexList
	 *            凸ポリゴンの頂点列
	 * @param point
	 *            指定点
	 * @return true --- 包含されている, false --- 包含されていない
	 */
	public static boolean inside(ArrayList<Vector3d> vertexList, Vector3d point) {
		boolean inside = true;
		Vector3d v0 = null;
		for (int i = 0; i < vertexList.size(); i++) {
			// ポリゴンの各辺に対して衝突点が右側か左側か？
			Vector3d center = (Vector3d) point.clone();
			Vector3d v2 = (Vector3d) (vertexList.get((i + 1)
					% vertexList.size()).clone());
			Vector3d v1 = (Vector3d) vertexList.get(i).clone();
			center.sub(v1);
			v2.sub(v1);
			v1.cross(center, v2);
			if (v0 != null && v0.dot(v1) < -GeometryUtility.TOLERANCE) {
				inside = false;
				break;
			}
			v0 = v1;
		}
		// すべて右側、またはすべて左側だった場合、凸ポリゴンの内部に位置したと考える
		return inside;
	}

	// IndexedGeometryArrayのインデックスのうち、同じ座標をさすインデックスを置き換える
	public static void compressGeometry(IndexedGeometryArray g) {
		// ①Hashtableを作りながら、representation[]を作る
		Hashtable<Double, ArrayList<Integer>> h = new Hashtable<Double, ArrayList<Integer>>();
		Point3d p = new Point3d();
		Point3d p2 = new Point3d();
		double hash;
		ArrayList<Integer> list;
		int[] representation = new int[g.getVertexCount()];

		for (int i = 0; i < g.getVertexCount(); i++) {
			g.getCoordinate(i, p);

			hash = p.getX() + p.getY() + p.getZ();

			list = h.get(new Double(hash));

			if (list == null) {// hashに対応する要素がない場合
				// Hashtableを作る
				list = new ArrayList<Integer>();
				list.add(new Integer(i));
				h.put(new Double(hash), list);
				// representation[]を作る
				representation[i] = i;
			} else {
				boolean bFound = false;
				for (int j = 0; j < list.size(); j++) {
					g.getCoordinate(list.get(j).intValue(), p2);
					if (p.getX() == p2.getX() && p.getY() == p2.getY()
							&& p.getZ() == p2.getZ()) {
						representation[i] = list.get(j).intValue();
						bFound = true;
						break;
					}
				}
				if (!bFound) {
					list.add(new Integer(i));
					// representation[]を作る
					representation[i] = i;
				}
			}
		}

		// ②indexの置き換え
		for (int i = 0; i < g.getIndexCount(); i++) {
			int index = representation[g.getCoordinateIndex(i)];
			g.setCoordinateIndex(i, index);
			
		}

	}

}