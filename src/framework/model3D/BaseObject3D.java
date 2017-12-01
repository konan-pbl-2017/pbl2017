package framework.model3D;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingPolytope;
import javax.media.j3d.Geometry;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import com.sun.j3d.utils.geometry.Primitive;

public class BaseObject3D implements Placeable {
	public TransformGroup center;
	
	protected BoundingSurface[] boundingSurfaces = null;
	private ArrayList<ShadowVolume> shadowVolumes = new ArrayList<ShadowVolume>();
	
	private BumpMapGenerator bumpMapGenerator = null;
	private ReflectionMapGenerator reflectionMapGenerator = null;
	protected boolean bBumpMapApplied = false;
	protected boolean bReflectionMapApplied = false;
	
	public BaseObject3D() {
		center = new TransformGroup();
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	}
	
	public BaseObject3D(Geometry g, Appearance a) {
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
		center.addChild(shape);
	}
	
	// コピーコンストラクタ
	public BaseObject3D(BaseObject3D obj) {
		Transform3D transCenter = new Transform3D();
		obj.center.getTransform(transCenter);
		this.center = new TransformGroup(transCenter);
		this.center.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.center.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Enumeration<Node> nodes = obj.getPrimitiveNodes();
		for (; nodes.hasMoreElements();) {
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
		if (obj.boundingSurfaces != null) {
			this.boundingSurfaces = obj.boundingSurfaces;
		}
		this.bumpMapGenerator = obj.bumpMapGenerator;
		this.reflectionMapGenerator = obj.reflectionMapGenerator;
		this.bBumpMapApplied = obj.bBumpMapApplied;
		this.bReflectionMapApplied = obj.bReflectionMapApplied;
	}

	// 自分を複製する（クローンを作る）
	public BaseObject3D duplicate() {
		BaseObject3D obj = new BaseObject3D(this);
		return obj;
	}
	
	@Override
	public TransformGroup getTransformGroupToPlace() {
		return getBody().center;
	}
	
	public BaseObject3D getBody() {
		return this;
	}
	
//	public void placeFirst(RWTUniverse universe) {
//		universe.getRoot().insertChild(this.center, 0);
//	}
//
//	public void placeLast(RWTUniverse universe) {
//		universe.getRoot().addChild(this.center);
//	}
//	
	public Node getPrimitiveNode() {
		return (Node)center.getChild(0);
	}
	
	public Enumeration<Node> getPrimitiveNodes() {
		return (Enumeration<Node>)center.getAllChildren();
	}
	
	public Appearance getAppearance() {
		Appearance ap = null;
		Node node = getPrimitiveNode();
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
		return ap;
	}
	
	public ArrayList<Appearance> getAppearances() {
		ArrayList<Appearance> appearances = new ArrayList<Appearance>();
		appearances.add(getAppearance());
		return appearances;
	}

	public BoundingSurface[] getBoundingSurfaces() {
		if (boundingSurfaces == null) {
			Node node = getPrimitiveNode();
			if (node == null || !(node instanceof Shape3D)) return null;
			Shape3D shape = (Shape3D)node;
			double coordinate1[] = new double[3];

			// 3D上の頂点のArrayList、3D上から求めた2D上頂点のArrayListの宣言
			ArrayList<Vector3d> vertex3DList = new ArrayList<Vector3d>();

			// GeometryArrayの習得
			Geometry g = shape.getGeometry();
			if (g instanceof IndexedTriangleArray) {
				// IndexedTriangleArray の場合
				IndexedTriangleArray triArray = (IndexedTriangleArray)g;
		
				// 全頂点を3D上の頂点をvertex3DListに入れていく。
				for (int i = 0; i < triArray.getIndexCount() - 3; i++) {
					triArray.getCoordinates(triArray.getCoordinateIndex(i), coordinate1);
					vertex3DList.add(new Vector3d(coordinate1));
				}
			} else if (g instanceof IndexedTriangleStripArray) {
				// IndexedTriangleStripArray の場合
				IndexedTriangleStripArray triStripAttay = (IndexedTriangleStripArray)g;				
				int stripVertexCounts[] = new int[triStripAttay.getNumStrips()];
				triStripAttay.getStripIndexCounts(stripVertexCounts);
				// 全頂点を3D上の頂点をvertex3DListに入れていく
				int index = 0;
				double coordinate2[] = new double[3];
				double coordinate3[] = new double[3];
				double coordinate4[] = new double[3];
				for (int i = 0; i < triStripAttay.getNumStrips() - 1; i++) {
					for (int j = 0; j < stripVertexCounts[i]; j += 2) {						
						triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index), coordinate1);
						triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+1), coordinate2);
						triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+2), coordinate3);
						triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+3), coordinate4);
						vertex3DList.add(new Vector3d(coordinate1));	//1つめの三角形
						vertex3DList.add(new Vector3d(coordinate2));
						vertex3DList.add(new Vector3d(coordinate3));
						vertex3DList.add(new Vector3d(coordinate2));	//2つめの三角形
						vertex3DList.add(new Vector3d(coordinate4));
						vertex3DList.add(new Vector3d(coordinate3));
						index += 2;
					}
				}
			} else if (g instanceof TriangleStripArray) {
				// TriangleStripArray の場合
				TriangleStripArray triStripAttay = (TriangleStripArray)g;				
				int stripVertexCounts[] = new int[triStripAttay.getNumStrips()];
				triStripAttay.getStripVertexCounts(stripVertexCounts);
				// 全頂点を3D上の頂点をvertex3DListに入れていく
				int index = 0;
				double coordinate2[] = new double[3];
				double coordinate3[] = new double[3];
				double coordinate4[] = new double[3];
				for (int i = 0; i < triStripAttay.getNumStrips() - 1; i++) {
					for (int j = 0; j < stripVertexCounts[i]; j += 2) {						
						triStripAttay.getCoordinates(index, coordinate1);
						triStripAttay.getCoordinates(index+1, coordinate2);
						triStripAttay.getCoordinates(index+2, coordinate3);
						triStripAttay.getCoordinates(index+3, coordinate4);
						vertex3DList.add(new Vector3d(coordinate1));	//1つめの三角形
						vertex3DList.add(new Vector3d(coordinate2));
						vertex3DList.add(new Vector3d(coordinate3));
						vertex3DList.add(new Vector3d(coordinate2));	//2つめの三角形
						vertex3DList.add(new Vector3d(coordinate4));
						vertex3DList.add(new Vector3d(coordinate3));
						index += 2;
					}
				}
			} else {
				return null;
			}

			BoundingSurface[] surfaces = new BoundingSurface[vertex3DList.size() / 3];

			for (int i = 0; i < vertex3DList.size(); i += 3) {
				Vector3d v1 = vertex3DList.get(i);
				Vector3d v2 = vertex3DList.get(i + 1);
				Vector3d v3 = vertex3DList.get(i + 2);
				BoundingSurface bSurface = new BoundingSurface();
				bSurface.addVertex((Vector3d)v1.clone());
				bSurface.addVertex((Vector3d)v2.clone());
				bSurface.addVertex((Vector3d)v3.clone());
				bSurface.setBounds(createBoundingPolytope(v1, v2, v3));
				surfaces[i / 3] = bSurface;
			}
			boundingSurfaces = surfaces;
		}
		return boundingSurfaces;
	}

	protected BoundingPolytope createBoundingPolytope(Vector3d vertex1,
			Vector3d vertex2, Vector3d vertex3) {
		Vector3d v1 = new Vector3d();
		Vector3d v2 = new Vector3d();
		Vector3d v3 = new Vector3d();
		Vector3d v4 = new Vector3d();
		Vector3d v5 = new Vector3d();
		Vector3d v6 = new Vector3d();
		Vector3d cv1 = new Vector3d();
		Vector3d cv2 = new Vector3d();
		cv1.sub(vertex3, vertex1);
		cv2.sub(vertex2, vertex1);
		Vector3d cv = new Vector3d();
		cv.cross(cv1, cv2);
		cv.normalize();
		cv.scale(0.01);
		v1.set(vertex1);
		v2.set(vertex2);
		v3.set(vertex3);
		v4.set(vertex1);
		v4.add(cv);
		v5.set(vertex2);
		v5.add(cv);
		v6.set(vertex3);
		v6.add(cv);

		Vector3d pv1 = new Vector3d();
		Vector3d pv2 = new Vector3d();
		Vector3d pv3 = new Vector3d();
		Vector3d pn = new Vector3d();
		Vector4d[] plane = new Vector4d[5];

		// 0
		pv1 = v1;
		pv2.sub(v2, v1);
		pv3.sub(v3, v1);

		pn.cross(pv2, pv3);
		pn.normalize();
		plane[0] = new Vector4d(pn.x, pn.y, pn.z, -pn.dot(pv1));

		// 1
		pv1 = v1;
		pv2.sub(v4, v1);
		pv3.sub(v2, v1);

		pn.cross(pv2, pv3);
		pn.normalize();
		plane[1] = new Vector4d(pn.x, pn.y, pn.z, -pn.dot(pv1));

		// 2
		pv1 = v1;
		pv2.sub(v3, v1);
		pv3.sub(v4, v1);

		pn.cross(pv2, pv3);
		pn.normalize();
		plane[2] = new Vector4d(pn.x, pn.y, pn.z, -pn.dot(pv1));

		// 3
		pv1 = v6;
		pv2.sub(v3, v6);
		pv3.sub(v5, v6);

		pn.cross(pv2, pv3);
		pn.normalize();
		plane[3] = new Vector4d(pn.x, pn.y, pn.z, -pn.dot(pv1));

		// 4
		pv1 = v6;
		pv2.sub(v5, v6);
		pv3.sub(v4, v6);

		pn.cross(pv2, pv3);
		pn.normalize();
		plane[4] = new Vector4d(pn.x, pn.y, pn.z, -pn.dot(pv1));

		return new BoundingPolytope(plane);
	}

	public void createShadowVolume(ArrayList<Light> lights, double shadowDepth, Transform3D localToWorld) {
		for (int n = 0; n < lights.size(); n++) {
			shadowVolumes.add(new ShadowVolume(this, lights.get(n), shadowDepth, localToWorld));
		}
	}

	public void updateShadowVolume(Transform3D localToWorld) {
		for (int n = 0; n < shadowVolumes.size(); n++) {
			shadowVolumes.get(n).update(localToWorld);
		}
	}
	
	public void setBumpMapping(BumpMapGenerator g) {
		bumpMapGenerator = g;
		bBumpMapApplied = true;
	}
	
	public BumpMapGenerator getBumpMappingInfo() {
		return bumpMapGenerator;
	}
	
	public boolean isBumpMappingApplied() {
		return bBumpMapApplied;
	}
	
	public void setReflectionMapping(ReflectionMapGenerator g) {
		reflectionMapGenerator = g;
		bReflectionMapApplied = true;
	}
	
	public ReflectionMapGenerator getReflectionMappingInfo() {
		return reflectionMapGenerator;
	}
	
	public boolean isReflectionMappingApplied() {
		return bReflectionMapApplied;
	}
	
	public boolean hasAppearancePrepared() {
		if (bumpMapGenerator != null && !bumpMapGenerator.hasMapped()) return false;
		if (reflectionMapGenerator != null && !reflectionMapGenerator.hasMapped()) return false;
		return true;
	}
}