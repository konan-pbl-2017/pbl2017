package framework.model3D;
import java.util.ArrayList;

import javax.media.j3d.BoundingPolytope;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;


public class BoundingSurface implements Cloneable {
	private Bounds bounds = null;		// 粗い衝突判定用（粗い地面用）
	private ArrayList<BoundingSurface> children = new ArrayList<BoundingSurface>();
	private ArrayList<Vector3d> vertexList = new ArrayList<Vector3d>();
	private static Vector4d plane[] = { new Vector4d(), new Vector4d(), new Vector4d(),
		new Vector4d(), new Vector4d() };
	
	public Object clone() {
		BoundingSurface s = new BoundingSurface();
		if (bounds != null) {
			s.setBounds((Bounds)bounds.clone());
		}
		for (int i = 0; i < children.size(); i++) {
			s.children.add((BoundingSurface)children.get(i).clone());
		}
		for (int i = 0; i < vertexList.size(); i++) {
			s.vertexList.add((Vector3d)vertexList.get(i).clone());
		}
		return s;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public Bounds getBounds() {
		return bounds;
	}
	
	public void addVertex(Vector3d v) {
		vertexList.add(v);
	}

	public void addChild(BoundingSurface bs, boolean bCombineBounds) {
		children.add(bs);
		if (bCombineBounds) {
			if (bounds == null) {
				bounds = bs.bounds;
			}
			bounds.combine(bs.bounds);
		}
	}
	
	public void transform(Transform3D transform3D) {
		bounds.transform(transform3D);
		for (int i = 0; i < vertexList.size(); i++) {
			Matrix4d mat4d = new Matrix4d();
			transform3D.get(mat4d);
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
		}
	}

	/**
	 * BoundingSphereとの粗い衝突判定
	 * @param bs 衝突判定の対象
	 * @return 衝突しているBoundingSurfaceのリスト（nullを返すことはない）
	 */
	public ArrayList<BoundingSurface> intersect(BoundingSphere bs) {
		ArrayList<BoundingSurface> results = new ArrayList<BoundingSurface>();
		if (children == null || children.size() == 0) {
			if (bounds.intersect(bs)) {
				results.add(this);
			}
		} else {
			if (bounds == null || bounds.intersect(bs)) {
				for (int n = 0; n < children.size(); n++) {
					results.addAll(children.get(n).intersect(bs));
				}
			}
		}
		return results;
	}
	
	/**
	 * OBBとの詳細な衝突判定
	 * @param obb 衝突判定の対象
	 * @return　衝突判定の結果
	 */
	public CollisionResult intersect(OBB obb) {
		if (children == null || children.size() == 0) {
			// 葉の場合は、凸ポリゴンを立ち上げた薄い多角柱
			if (bounds instanceof BoundingPolytope) {
				((BoundingPolytope)bounds).getPlanes(plane);
				CollisionResult cr = obb.intersect(plane[0]);	// obbが底面を含む無限平面と交わっているか？
				if (cr != null) {
					// 無限平面と交わっている場合、無限平面との衝突点が凸ポリゴンの内部に位置するか？
					if (GeometryUtility.inside(vertexList, cr.collisionPoint.getVector3d())) {
						return cr;
					}
				}
			}
			return null;
		} else {
			for (int n = 0; n < children.size(); n++) {
				CollisionResult cr = children.get(n).intersect(obb);
				if (cr != null) return cr;
			}
			return null;
		}
	}
}
