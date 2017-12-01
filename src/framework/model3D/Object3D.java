package framework.model3D;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingPolytope;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.LOD;
import javax.media.j3d.Leaf;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;

/**
 * 階層化され名前付けられた基本オブジェクト
 * @author 新田直也
 *
 */
public class Object3D extends BaseObject3D {
	public Object3D[] children = new Object3D[0];
	public String name;
	public TransformGroup pos;
	public TransformGroup rot;
	public TransformGroup scale;
	protected Position3D position = new Position3D();
	protected Quaternion3D quaternion;

	public BoundingSphere bs = null;
	private OBB obb = new OBB();
	private UndoBuffer undoBuffer = new UndoBuffer();
	private boolean bLOD = false;
	private LOD lodNode = null;

	// コンストラクタ
	public Object3D(String name, Primitive node) {
		if (name == null) name = "";
		this.name = new String(name);
		pos = new TransformGroup();
		pos.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		pos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rot = new TransformGroup();
		rot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		scale = new TransformGroup();
		scale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		center = new TransformGroup();
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		pos.addChild(rot);
		rot.addChild(scale);
		scale.addChild(center);
		center.addChild(node);
	}
	
	public Object3D(String name, Leaf node) {
		if (name == null) name = "";
		this.name = new String(name);
		if (!(node instanceof LOD)) {
			pos = new TransformGroup();
			pos.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			pos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			rot = new TransformGroup();
			rot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			scale = new TransformGroup();
			scale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			scale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			center = new TransformGroup();
			center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			pos.addChild(rot);
			rot.addChild(scale);
			scale.addChild(center);
			center.addChild(node);
		} else {
			// LOD の場合
			bLOD = true;
			lodNode = (LOD)node;
			pos = new TransformGroup();
			pos.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			pos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			rot = new TransformGroup();
			rot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			scale = new TransformGroup();
			scale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			scale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			center = new TransformGroup();
			center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			pos.addChild(rot);
			rot.addChild(scale);
			scale.addChild(center);
			BranchGroup lodRoot = new BranchGroup();
			lodRoot.addChild(lodNode);
			lodRoot.addChild(lodNode.getSwitch(0));
			center.addChild(lodRoot);
		}
	}
	
	public Object3D(String name, Geometry g, Appearance a) {
		if (name == null) name = "";
		this.name = new String(name);
		pos = new TransformGroup();
		pos.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		pos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rot = new TransformGroup();
		rot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		scale = new TransformGroup();
		scale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		center = new TransformGroup();
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		a.setCapability(Appearance.ALLOW_TEXTURE_READ);
		a.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		a.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
		a.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
		a.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
		a.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_WRITE);
		Shape3D shape = new Shape3D(g, a);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		pos.addChild(rot);
		rot.addChild(scale);
		scale.addChild(center);
		center.addChild(shape);
	}

	// コンストラクタ
	public Object3D(String name, Object3D[] children) {
		if (name == null)
			name = "";
		this.name = name;
		this.children = children;
		int n = children.length;
		int i = 0;

		pos = new TransformGroup();
		pos.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		pos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		rot = new TransformGroup();
		rot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		scale = new TransformGroup();
		scale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		center = new TransformGroup();
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		pos.addChild(rot);
		rot.addChild(scale);
		scale.addChild(center);

		for (i = 0; i < n; i++) {
			center.addChild(children[i].pos);
			if (children[i].isBumpMappingApplied()) bBumpMapApplied = true;
			if (children[i].isReflectionMappingApplied()) bReflectionMapApplied = true;
		}
	}
		
	public Object3D(String name, Object3D[] children,
			Transform3D defaultTransform) {
		this(name, children);
		if (defaultTransform == null) return;
		pos.setTransform(defaultTransform);
	}

	// コピーコンストラクタ
	public Object3D(Object3D obj) {
		this.name = new String(obj.name);
		this.position = new Position3D(obj.position);
		Transform3D transPos = new Transform3D();
		obj.pos.getTransform(transPos);
		this.pos = new TransformGroup(transPos);
		this.pos.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.pos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D transRot = new Transform3D();
		obj.rot.getTransform(transRot);
		this.rot = new TransformGroup(transRot);
		this.rot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.rot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D transScale = new Transform3D();
		obj.scale.getTransform(transScale);
		this.scale = new TransformGroup(transScale);
		this.scale.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.scale.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D transCenter = new Transform3D();
		obj.center.getTransform(transCenter);
		this.center = new TransformGroup(transCenter);
		this.center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		this.pos.addChild(this.rot);
		this.rot.addChild(this.scale);
		this.scale.addChild(this.center);
		this.bLOD = obj.bLOD;
		if (obj.hasChildren()) {
			this.children = new Object3D[obj.children.length];
			for (int i = 0; i < obj.children.length; i++) {
				this.children[i] = new Object3D(obj.children[i]);
				this.center.addChild(this.children[i].pos);
			}
		} else {
			this.children = new Object3D[0];
			if (!bLOD) {
				Enumeration<Node> nodes = obj.getPrimitiveNodes();
				while (nodes.hasMoreElements()) {
					Node node = nodes.nextElement();
					if (node != null && node instanceof Shape3D) {
						Shape3D shape = (Shape3D)node;
						shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
						shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
						Appearance a = (Appearance)shape.getAppearance().cloneNodeComponent(true);
						a.setCapability(Appearance.ALLOW_TEXTURE_READ);
						a.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
						a.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
						a.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
						a.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_READ);
						a.setCapability(Appearance.ALLOW_TEXTURE_UNIT_STATE_WRITE);
						this.center.addChild(new Shape3D((Geometry) shape
								.getAllGeometries().nextElement(), a));
					} else if (node != null && node instanceof Primitive) {
						Primitive primitive = (Primitive)node;
						primitive = (Primitive)primitive.cloneTree();
						primitive.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
						this.center.addChild(primitive);
					}
				}
			} else {
				// LOD の場合
				this.lodNode = (LOD)obj.lodNode.cloneTree();
				BranchGroup lodRoot = new BranchGroup();
				lodRoot.addChild(lodNode);
				lodRoot.addChild(lodNode.getSwitch(0));
				this.center.addChild(lodRoot);				
			}
		}
		if (obj.obb != null) {
			this.obb = obj.obb;
		}
		if (obj.bs != null) {
			this.bs = obj.bs;
		}
		if (obj.boundingSurfaces != null) {
			this.boundingSurfaces = obj.boundingSurfaces;
		}
		this.undoBuffer = new UndoBuffer(obj.undoBuffer);
	}

	public Node getPrimitiveNode() {
		if (hasChildren()) return null;
		if (!bLOD) {
			return (Node)center.getChild(0);
		} else {
			// LOD の場合
			return lodNode.getSwitch(0).getChild(0);			
		}
	}

	public Enumeration<Node> getPrimitiveNodes() {
		if (hasChildren()) return null;
		if (!bLOD) {
			return (Enumeration<Node>)center.getAllChildren();
		} else {
			// LOD の場合
			return lodNode.getSwitch(0).getAllChildren();						
		}
	}
	
	public ArrayList<Appearance> getAppearances() {
		ArrayList<Appearance> appearances = new ArrayList<Appearance>();
		if (!bLOD) {
			if (!hasChildren()) {
				appearances.add(getAppearance());
			} else {
				for (int n = 0; n < children.length; n++) {
					appearances.addAll(children[n].getAppearances());
				}
			}
			return appearances;
		} else {
			// LOD の場合
			Enumeration<Node> nodes = getPrimitiveNodes();
			while (nodes.hasMoreElements()) {
				Node node = nodes.nextElement();
				Appearance ap = null;
				if (node != null && node instanceof Shape3D) {
					Shape3D shape = (Shape3D)node;
					ap = shape.getAppearance();
					if (ap == null) {
						ap = new Appearance();
						shape.setAppearance(ap);
					}
				} else if (node != null && node instanceof Primitive) {
					Primitive primitive = (Primitive)node;
					ap = primitive.getAppearance();
					if (ap == null) {
						ap = new Appearance();
						primitive.setAppearance(ap);
					}
				}
				appearances.add(ap);
			}			
			return appearances;			
		}
	}

	// 引数part（部分）のオブジェクトを探す
	public Object3D getPart(String part) {

		int j = 0;
		int N = children.length;
		Object3D obj;

		// if(part == children[j].name) return this;
		if (part.equals(this.name))
			return this;

		for (j = 0; j < N; j++) {
			if (children[j] != null) {
				obj = children[j].getPart(part);
				if (obj != null)
					return obj;
				else
					continue;
			}
		}
		return null;
	}

	@Override
	public TransformGroup getTransformGroupToPlace() {
		return pos;
	}	

	// Position3D の applyTo() 以外からは呼ばないこと
	void setPosition(Position3D p) {
		position = (Position3D) p.clone();
		Vector3d v = new Vector3d(p.getX(), p.getY(), p.getZ());
		Transform3D trans = new Transform3D();
		trans.set(v);
		pos.setTransform(trans);
	}

	public void scale(double s) {
		Transform3D trans = new Transform3D();
		trans.setScale(s);
		scale.setTransform(trans);
	}

	public void scale(double x, double y, double z) {
		Transform3D trans = new Transform3D();
		trans.setScale(new Vector3d(x, y, z));
		scale.setTransform(trans);
	}

	public Position3D getPosition3D() {
		return (Position3D) position.clone();

	}

	// キャラクターを回転させる
	public void rotate(double vx, double vy, double vz, double a) {
		AxisAngle4d t = new AxisAngle4d(vx, vy, vz, a);
		Transform3D trans = new Transform3D();
		trans.setRotation(t);
		rot.setTransform(trans);
	}
	
	private void rotate(Quat4d quat) {
		Transform3D trans = new Transform3D();
		trans.setRotation(quat);
		rot.setTransform(trans);
	}

	// キャラクターのpart(体の部分)を回転させる
	public void rotate(String part, double vx, double vy, double vz, double a) {
		getPart(part).rotate(vx, vy, vz, a);
	}

	public void apply(Property3D p, boolean enableUndo) {
		if (enableUndo) {
			undoBuffer.push(p);
		}
		p.applyTo(this);
	}

	// 自分を複製する（クローンを作る）
	public Object3D duplicate() {
		Object3D obj = new Object3D(this);
		return obj;
	}

	// v（訪問者）に訪問先を教える
	public void accept(ObjectVisitor v) {
		int i;
		v.preVisit(this);
		if (children != null) {
			for (i = 0; i < children.length; i++)
				children[i].accept(v);
		} else
			;
		v.postVisit(this);
	}

	// objectに子供がいるかどうかを調べる
	public boolean hasChildren() {
		if (this.children != null && this.children.length > 0)
			return true;
		else
			return false;
	}

	/*
	 * undoするポイントを設定する。
	 */
	public void setUndoMark() {
		undoBuffer.setUndoMark();
	}

	/*
	 * undoする。
	 */
	public void undo() {
		Iterator<Property3D> iterator = undoBuffer.undo().iterator();
		while (iterator.hasNext()) {
			Property3D p = iterator.next();
			p.applyTo(this);
		}
	}

	/**
	 * 当たり判定処理、Oriented Bounding Box(OBB)をする。 0:最大面積法　1:最長対角線法
	 */
	public OBB getOBB(int pattern) {
		if (obb.bp == null) {
			int i = 0;
			Node node = getPrimitiveNode();
			if (node == null) return null;
			if (node instanceof Box) {
				// Boxの場合
				Box box = ((Box)node);
				double xDim = box.getXdimension();
				double yDim = box.getYdimension();
				double zDim = box.getZdimension();
				obb.vertexList.add(new Vector3d(-xDim, -yDim, -zDim));
				obb.vertexList.add(new Vector3d(-xDim, yDim, -zDim));
				obb.vertexList.add(new Vector3d(-xDim, -yDim, zDim));
				obb.vertexList.add(new Vector3d(-xDim, yDim, zDim));
				obb.vertexList.add(new Vector3d(xDim, -yDim, -zDim));
				obb.vertexList.add(new Vector3d(xDim, yDim, -zDim));
				obb.vertexList.add(new Vector3d(xDim, -yDim, zDim));
				obb.vertexList.add(new Vector3d(xDim, yDim, zDim));
			} else {
				if (!(node instanceof Shape3D)) return null;
				// Shape3Dの場合
				Shape3D shape = (Shape3D)node;
				double coordinate[] = new double[3];
	
				// 3D上の頂点のArrayList、3D上から求めた2D上頂点のArrayListの宣言
				ArrayList<Vector3d> vertex3DList = new ArrayList<Vector3d>();
	
				// GeometryArrayの習得
				IndexedGeometryArray iga = (IndexedGeometryArray) shape
						.getGeometry();
	
				// x,y,zで構成される頂点の数がnullになるまで頂点を3D上の頂点をvertex3DListに入れていく。
				for (i = 0; i < iga.getIndexCount() - 3; i++) {
					iga.getCoordinates(iga.getCoordinateIndex(i), coordinate);
					Vector3d p = new Vector3d(coordinate[0], coordinate[1],
							coordinate[2]);
					// System.out.println(i+1);
					// System.out.print("x："+coordinate[0]);
					// System.out.print(" y："+coordinate[1]);
					// System.out.println(" z："+coordinate[2]);
					vertex3DList.add(p);
					// System.out.println("vertex3DList(" + i +
					// "):"+vertex3DList.get(i));
				}
	
				// System.out.println("count："+vertex3DList.size());
	
				if (pattern == 0) {
					Vector3d cv1 = new Vector3d();
					Vector3d cv2 = new Vector3d();
		
					double cvMax = 0.0;
					Vector3d axis1 = new Vector3d(); // 3D頂点から求めた法線ベクトル
					Vector3d axis2 = new Vector3d(); // 2D頂点から求めた法線ベクトル
					Vector3d axis3 = new Vector3d(); // axis1,axis2の外積から求めた法線ベクトル
		
					// 面積を3D頂点リストから求め、法線を求める処理
					for (i = 0; i < vertex3DList.size(); i += 3) {
						cv1.sub(vertex3DList.get(i + 2), vertex3DList.get(i));
						// System.out.println("cv1" +cv1);
						cv2.sub(vertex3DList.get(i + 1), vertex3DList.get(i));
						// System.out.println("cv2" +cv2);
						Vector3d cv = new Vector3d();
						cv.cross(cv1, cv2);
						// System.out.println("cv" +cv);
						if (i == 0 || cv.length() >= cvMax) {
							cvMax = cv.length();
							// / System.out.println("cvMax" +cvMax);
							axis1 = cv;
							// System.out.println("point1："+axis1);
						}
					}
		
					// System.out.println("point1："+axis1);
					ProjectionResult pr1 = GeometryUtility.projection3D(vertex3DList,
							axis1);
					// System.out.println("axis1："+axis1);
					// 辺のを2D頂点リストから求め、法線を求める処理
					for (i = 0; i < pr1.vertexList.size() - 1; i++) {
						// System.out.println("vertexList(" + i +
						// "):"+pr1.vertexList.get(i));
						// System.out.println("vertexList(" + i +
						// "):"+pr1.vertexList.get(i+1));
						Vector3d cv = new Vector3d();
						cv.sub(vertex3DList.get(i + 1), vertex3DList.get(i));
						if (i == 0 || cv.length() >= cvMax) {
							cvMax = cv.length();
							// System.out.println("cvMax" +cvMax);
							axis2 = cv;
							// System.out.println("point2："+axis2);
						}
					}
		
					// System.out.println("point2："+axis2);
		
					ProjectionResult pr2 = GeometryUtility.projection2D(
							pr1.vertexList, axis2);
					// axis1,axis2で外積でaxis3を求める。
					axis3.cross(axis1, axis2);
					// System.out.println("point3_1："+axis3);
		
					ProjectionResult pr3 = GeometryUtility.projection2D(
							pr1.vertexList, axis3);
					// System.out.println("point3_1："+axis3);
					AxisResult ar = new AxisResult(axis1, axis2, axis3);
		
					// System.out.println("ar_point1："+ar.axis1);
					// System.out.println("ar_point2："+ar.axis2);
					// System.out.println("ar_point3："+ar.axis3);
					// ここから最大面積法で求めた点を取得していく処理
					// ArrayList<Vector3d> vertexCollisionList = new
					// ArrayList<Vector3d>();
		
					// System.out.println("pr1.max："+pr1.max);
					// System.out.println("pr1.min："+pr1.min);
					// System.out.println("pr2.max："+pr2.max);
					// System.out.println("pr2.min："+pr2.min);
					// System.out.println("pr3.max："+pr3.max);
					// System.out.println("pr3.min："+pr3.min);
		
					// No.1
					ar.axis1.scale(pr1.min);
					ar.axis2.scale(pr2.min);
					ar.axis3.scale(pr3.min);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
		
					// No.2
					ar = new AxisResult(axis1, axis2, axis3);
					ar.axis1.scale(pr1.min);
					ar.axis2.scale(pr2.max);
					ar.axis3.scale(pr3.min);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
		
					// No.3
					ar = new AxisResult(axis1, axis2, axis3);
					ar.axis1.scale(pr1.min);
					ar.axis2.scale(pr2.min);
					ar.axis3.scale(pr3.max);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
		
					// No.4
					ar = new AxisResult(axis1, axis2, axis3);
					ar.axis1.scale(pr1.min);
					ar.axis2.scale(pr2.max);
					ar.axis3.scale(pr3.max);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
		
					// No.5
					ar = new AxisResult(axis1, axis2, axis3);
					ar.axis1.scale(pr1.max);
					ar.axis2.scale(pr2.min);
					ar.axis3.scale(pr3.min);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
		
					// No.6
					ar = new AxisResult(axis1, axis2, axis3);
					ar.axis1.scale(pr1.max);
					ar.axis2.scale(pr2.max);
					ar.axis3.scale(pr3.min);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
		
					// No.7
					ar = new AxisResult(axis1, axis2, axis3);
					ar.axis1.scale(pr1.max);
					ar.axis2.scale(pr2.min);
					ar.axis3.scale(pr3.max);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
		
					// No.8
					ar = new AxisResult(axis1, axis2, axis3);
					ar.axis1.scale(pr1.max);
					ar.axis2.scale(pr2.max);
					ar.axis3.scale(pr3.max);
					ar.axis1.add(ar.axis2);
					ar.axis3.add(ar.axis1);
					obb.vertexList.add(ar.axis3);
				} else {
					double minX, maxX, minY, maxY, minZ, maxZ;
					minX = maxX = vertex3DList.get(0).x;
					minY = maxY = vertex3DList.get(0).y;
					minZ = maxZ = vertex3DList.get(0).z;
					for (int n = 1; n < vertex3DList.size(); n++) {
						Vector3d v = vertex3DList.get(n);
						if (minX > v.x) minX = v.x;
						if (maxX < v.x) maxX = v.x;
						if (minY > v.y) minY = v.y;
						if (maxY < v.y) maxY = v.y;
						if (minZ > v.z) minZ = v.z;
						if (maxZ < v.z) maxZ = v.z;
					}
					obb.vertexList.add(new Vector3d(minX, minY, minZ));
					obb.vertexList.add(new Vector3d(minX, maxY, minZ));
					obb.vertexList.add(new Vector3d(minX, minY, maxZ));
					obb.vertexList.add(new Vector3d(minX, maxY, maxZ));
					obb.vertexList.add(new Vector3d(maxX, minY, minZ));
					obb.vertexList.add(new Vector3d(maxX, maxY, minZ));
					obb.vertexList.add(new Vector3d(maxX, minY, maxZ));
					obb.vertexList.add(new Vector3d(maxX, maxY, maxZ));
				}
			}

			// for(i=0;i < vertexCollisionList.size();i++){
			// System.out.println(i+":"+vertexCollisionList.get(i));
			// }

			Vector3d v1 = new Vector3d();
			Vector3d v2 = new Vector3d();
			Vector3d v3 = new Vector3d();
			Vector4d[] plane = new Vector4d[6];

			// 0123
			v1 = obb.vertexList.get(0);
			v2.sub(obb.vertexList.get(2), obb.vertexList.get(0));
			v3.sub(obb.vertexList.get(1), obb.vertexList.get(0));
			Vector3d n = new Vector3d();
			n.cross(v2, v3);
			n.normalize();
			plane[0] = new Vector4d();
			plane[0].set(n.x, n.y, n.z, -n.dot(v1));
			

			// 0145
			v1 = obb.vertexList.get(0);
			v2.sub(obb.vertexList.get(1), obb.vertexList.get(0));
			v3.sub(obb.vertexList.get(4), obb.vertexList.get(0));
			n = new Vector3d();
			n.cross(v2, v3);
			n.normalize();
			plane[1] = new Vector4d();
			plane[1].set(n.x, n.y, n.z, -n.dot(v1));

			// 0357
			v1 = obb.vertexList.get(1);
			v2.sub(obb.vertexList.get(3), obb.vertexList.get(1));
			v3.sub(obb.vertexList.get(5), obb.vertexList.get(1));
			n = new Vector3d();
			n.cross(v2, v3);
			n.normalize();
			plane[2] = new Vector4d();
			plane[2].set(n.x, n.y, n.z, -n.dot(v1));

			// 6745
			v1 = obb.vertexList.get(6);
			v2.sub(obb.vertexList.get(4), obb.vertexList.get(6));
			v3.sub(obb.vertexList.get(7), obb.vertexList.get(6));
			n = new Vector3d();
			n.cross(v2, v3);
			n.normalize();
			plane[3] = new Vector4d();
			plane[3].set(n.x, n.y, n.z, -n.dot(v1));

			// 6240
			v1 = obb.vertexList.get(6);
			v2.sub(obb.vertexList.get(2), obb.vertexList.get(6));
			v3.sub(obb.vertexList.get(4), obb.vertexList.get(6));
			n = new Vector3d();
			n.cross(v2, v3);
			n.normalize();
			plane[4] = new Vector4d();
			plane[4].set(n.x, n.y, n.z, -n.dot(v1));

			// 6723
			v1 = obb.vertexList.get(6);
			v2.sub(obb.vertexList.get(7), obb.vertexList.get(6));
			v3.sub(obb.vertexList.get(2), obb.vertexList.get(6));
			n = new Vector3d();
			n.cross(v2, v3);
			n.normalize();
			plane[5] = new Vector4d();
			plane[5].set(n.x, n.y, n.z, -n.dot(v1));
			
			for(i = 0; i< plane.length;i++){
				obb.plane[i] = plane[i];
			}
			
			BoundingPolytope bp = new BoundingPolytope();
			bp.setPlanes(plane);
			obb.bp = bp;
			// System.out.println("△ポリゴン数："+TriangleCount);
			return obb;
		}
		// System.out.println();;
		return obb;
	}

	public class AxisResult {
		Vector3d axis1;
		Vector3d axis2;
		Vector3d axis3;

		AxisResult(Vector3d a1, Vector3d a2, Vector3d a3) {
			axis1 = (Vector3d) a1.clone();
			axis2 = (Vector3d) a2.clone();
			axis3 = (Vector3d) a3.clone();
		}
	}

	// Quaternion3D の applyTo() 以外からは呼ばないこと
	void setQuaternion(Quaternion3D quaternion) {
		this.quaternion = quaternion;
		rotate(quaternion.getQuat());
	}

	public Quaternion3D getQuaternion() {
		return quaternion;
	}
	
	public boolean isLODSet() {
		return bLOD;
	}
	
	public LOD getLOD() {
		return lodNode;
	}
}
