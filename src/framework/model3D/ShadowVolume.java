package framework.model3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.media.j3d.Appearance;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.IndexedTriangleFanArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleFanArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * ステンシルバッファを用いたシャドウボリューム
 * @author 新田直也
 *
 */
public class ShadowVolume {
	private static final boolean WIRE_FRAME_VIEW = false;
	
	private Light light = null;
	private double shadowDepth = 10.0;
	
	private IndexedQuadArray volumeGeometry = null;
	private Shape3D shadowVolumeFront = null;
	private Shape3D shadowVolumeBack = null;
	
	private int vertexCount = 0;
	private double coordinates[] = null;
	private int indicies[] = null;
	private Hashtable<Integer, Edge> edges = null;
	private ArrayList<Triangle> triangles = null;	
	
	/**
	 * 平行光源用のシャドウボリューム
	 * @param occuluder 遮蔽オブジェクト（影を落とすオブジェクト）
	 * @param light 光源
	 * @param shadowDepth 影の深さ
	 * @param localToWorld 遮蔽オブジェクト用の座標変換
	 */
	public ShadowVolume(BaseObject3D occuluder, Light light, double shadowDepth,
			Transform3D localToWorld) {
		this.light = light;
		this.shadowDepth = shadowDepth;
		
		// 幾何情報の抽出
		if (!extractGeometricInformation(occuluder)) return;
		
		// シャドウボリュームの表面を生成
		volumeGeometry = createVolumeSurface(light, shadowDepth, localToWorld, coordinates, edges, triangles);
		
		// シャドウボリュームオブジェクトの生成
		createShadowVolumeObject(volumeGeometry, occuluder);
	}
	
	/**
	 * シャドウボリュームの更新
	 * @param localToWorld 遮蔽オブジェクト用の座標変換
	 */
	public void update(Transform3D localToWorld) {
		// シャドウボリュームの表面を再生成
		volumeGeometry = createVolumeSurface(light, shadowDepth, localToWorld, coordinates, edges, triangles);
		
		// シャドウボリュームオブジェクトの更新
		shadowVolumeFront.removeAllGeometries();
		shadowVolumeFront.addGeometry(volumeGeometry);
		shadowVolumeBack.removeAllGeometries();
		shadowVolumeBack.addGeometry(volumeGeometry);
	}
	
	/**
	 * 幾何情報の抽出
	 * @param shadowCastingObject 抽出元のオブジェクト
	 * @return true --- 正常に抽出できた, false --- 正常に抽出できなかった
	 */
	private boolean extractGeometricInformation(BaseObject3D shadowCastingObject) {
		Node node = shadowCastingObject.getPrimitiveNode();
		if (node == null) return false;
		// GeometryArrayの習得
		ArrayList<Geometry> geometries = new ArrayList<Geometry>();
		Geometry g = null;
		if (node instanceof Shape3D) {
			g = ((Shape3D)node).getGeometry();
			geometries.add(g);
		} else if (node instanceof Cone) {
			g = ((Cone)node).getShape(Cone.BODY).getGeometry();
			geometries.add(g);
			g = ((Cone)node).getShape(Cone.CAP).getGeometry();
			geometries.add(g);
		} else if (node instanceof Cylinder) {
			g = ((Cylinder)node).getShape(Cylinder.BODY).getGeometry();			
			geometries.add(g);
			g = ((Cylinder)node).getShape(Cylinder.TOP).getGeometry();			
			geometries.add(g);
			g = ((Cylinder)node).getShape(Cylinder.BOTTOM).getGeometry();			
			geometries.add(g);
		} else if (node instanceof Box) {
			g = ((Box)node).getShape(Box.TOP).getGeometry();
			geometries.add(g);
			g = ((Box)node).getShape(Box.BOTTOM).getGeometry();
			geometries.add(g);
			g = ((Box)node).getShape(Box.FRONT).getGeometry();
			geometries.add(g);
			g = ((Box)node).getShape(Box.BACK).getGeometry();
			geometries.add(g);
			g = ((Box)node).getShape(Box.LEFT).getGeometry();
			geometries.add(g);
			g = ((Box)node).getShape(Box.RIGHT).getGeometry();
			geometries.add(g);
		} else if (node instanceof Sphere) {
			g = ((Sphere)node).getShape().getGeometry();
			geometries.add(g);
		} else {
			return false;
		}

		// 頂点の座標値を取得し、設定
		vertexCount = 0;
		for (int n = 0; n < geometries.size(); n++) {
			g = geometries.get(n);
			if (g instanceof GeometryArray) {
				// GeometryArray の場合				
				GeometryArray geometryArray = (GeometryArray)g;
				vertexCount += geometryArray.getVertexCount();
			}
		}
		coordinates = new double[vertexCount * 3 * 2];		
		indicies = new int[vertexCount * 4 * 3];		// 辺の数を頂点の数の最大3倍と考えている
		int base = 0;
		for (int n = 0; n < geometries.size(); n++) {
			g = geometries.get(n);
			if (g instanceof GeometryArray) {
				// GeometryArray の場合				
				GeometryArray geometryArray = (GeometryArray)g;
				double coordinate[] = new double[3];
				int count = geometryArray.getVertexCount();
				for (int v = 0; v < count; v++) {
					geometryArray.getCoordinate(v, coordinate);
					coordinates[(v + base) * 3] = coordinate[0];
					coordinates[(v + base) * 3 + 1] = coordinate[1];
					coordinates[(v + base) * 3 + 2] = coordinate[2];
				}
				base += count;
			}
		}
				
		// 面と辺の情報を作成
		edges = new Hashtable<Integer, Edge>();
		triangles = new ArrayList<Triangle>();
		base = 0;
		for (int n = 0; n < geometries.size(); n++) {
			g = geometries.get(n);
			if (g instanceof GeometryArray) {
				// GeometryArray の場合				
				GeometryArray geometryArray = (GeometryArray)g;
				Point3d p1 = new Point3d();
				Point3d p2 = new Point3d();
				Point3d p3 = new Point3d();
				int v1, v2, v3;
				if (g instanceof TriangleStripArray) {
					// TriangleStripArray の場合
					TriangleStripArray triStripArray = (TriangleStripArray)g;				
					int num = triStripArray.getNumStrips();
					int strips[] = new int[num];
					triStripArray.getStripVertexCounts(strips);
					int index = base;
					for (int j = 0; j < num; j++) {
						index += 2;
						for (int i = 2; i < strips[j]; i += 2) {
							addTriangle(index - 2, index - 1, index, p1, p2, p3);						
							if (i <= strips[j] - 2) {
								addTriangle(index - 1, index + 1, index, p1, p2, p3);						
								index += 1;
							}
							index += 1;
						}
					}
				} else if (g instanceof TriangleFanArray) {
						// TriangleFanArray の場合
						TriangleFanArray triFanArray = (TriangleFanArray)g;					
						int num = triFanArray.getNumStrips();
						int strips[] = new int[num];
						triFanArray.getStripVertexCounts(strips);
						int index = base;
						for (int j = 0; j < num; j++) {
							v1 = index;
							index += 1;
							for (int i = 1; i < strips[j] - 1; i += 1) {
								addTriangle(v1, index, index + 1, p1, p2, p3);						
								index += 1;
							}
						}
				} else if (g instanceof IndexedTriangleArray) {
					// IndexedTriangleArray の場合
					IndexedTriangleArray triArray = (IndexedTriangleArray)g;				
					int indexCount = triArray.getIndexCount();
					for (int i = 0; i < indexCount; i += 3) {
						v1 = triArray.getCoordinateIndex(i) + base;
						v2 = triArray.getCoordinateIndex(i + 1) + base;
						v3 = triArray.getCoordinateIndex(i + 2) + base;
						addTriangle(v1, v2, v3, p1, p2, p3);
					}
				} else if (g instanceof IndexedTriangleStripArray) {
					// IndexedTriangleStripArray の場合
					IndexedTriangleStripArray triStripArray = (IndexedTriangleStripArray)g;				
					int num = triStripArray.getNumStrips();
					int strips[] = new int[num];
					triStripArray.getStripIndexCounts(strips);
					int index = 0;
					for (int j = 0; j < num; j++) {
						index += 2;
						for (int i = 2; i < strips[j]; i += 2) {
							v1 = triStripArray.getCoordinateIndex(index - 2) + base;
							v2 = triStripArray.getCoordinateIndex(index - 1) + base;
							v3 = triStripArray.getCoordinateIndex(index) + base;
							addTriangle(v1,v2, v3, p1, p2, p3);						
							if (i <= strips[j] - 2) {
								v1 = v2;
								v2 = triStripArray.getCoordinateIndex(index + 1) + base;
								addTriangle(v1, v2, v3, p1, p2, p3);						
								index += 1;
							}
							index += 1;
						}
					}
				} else if (g instanceof IndexedTriangleFanArray) {
					// IndexedTriangleFanArray の場合
					IndexedTriangleFanArray triFanArray = (IndexedTriangleFanArray)g;				
					int num = triFanArray.getNumStrips();
					int strips[] = new int[num];
					triFanArray.getStripIndexCounts(strips);
					int index = 0;
					for (int j = 0; j < num; j++) {
						v1 = triFanArray.getCoordinateIndex(index) + base;
						index += 1;
						for (int i = 1; i < strips[j] - 1; i += 1) {
							v2 = triFanArray.getCoordinateIndex(index) + base;
							v3 = triFanArray.getCoordinateIndex(index + 1) + base;
							addTriangle(v1, v2, v3, p1, p2, p3);
							index += 1;
						}
					}
				}
				base += geometryArray.getVertexCount();
			}
		}
		return true;
	}

	private void addTriangle(int v1, int v2, int v3, Point3d p1, Point3d p2, Point3d p3) {
		Vector3d vec1;
		Vector3d vec2;
		Vector3d vec;
		Triangle tri;
		Edge e;
		p1.x = coordinates[v1 * 3];
		p1.y = coordinates[v1 * 3 + 1];
		p1.z = coordinates[v1 * 3 + 2];
		p2.x = coordinates[v2 * 3];
		p2.y = coordinates[v2 * 3 + 1];
		p2.z = coordinates[v2 * 3 + 2];
		p3.x = coordinates[v3 * 3];
		p3.y = coordinates[v3 * 3 + 1];
		p3.z = coordinates[v3 * 3 + 2];
		vec = new Vector3d(p1);
		vec1 = new Vector3d(p2);
		vec2 = new Vector3d(p3);
		vec1.sub(vec);
		vec2.sub(vec);
		vec1.cross(vec1, vec2);
		if (vec1.length() < GeometryUtility.TOLERANCE) {
			return;
		}
		vec1.normalize();
		tri = new Triangle(v1, v2, v3, vec1);
		triangles.add(tri);
		e = edges.get(new Integer(v2 * vertexCount + v1));	// 逆向きに回っている辺のみ共有
		if (e != null) {
			tri.edges[0] = e;
			e.triangles[1] = tri;
		} else {
			vec = new Vector3d(p2);
			vec.sub(p1);
		    e = new Edge(v1, v2, vec);
			tri.edges[0] = e;
			e.triangles[0] = tri;
			edges.put(new Integer(v1 * vertexCount + v2), e);
		}
		e = edges.get(new Integer(v3 * vertexCount + v2));	// 逆向きに回っている辺のみ共有
		if (e != null) {
			tri.edges[1] = e;
			e.triangles[1] = tri;
		} else {
			vec = new Vector3d(p3);
			vec.sub(p2);
		    e = new Edge(v2, v3, vec);
			tri.edges[1] = e;
			e.triangles[0] = tri;
			edges.put(new Integer(v2 * vertexCount + v3), e);
		}
		e = edges.get(new Integer(v1 * vertexCount + v3));	// 逆向きに回っている辺のみ共有
		if (e != null) {
			tri.edges[2] = e;
			e.triangles[1] = tri;
		} else {
			vec = new Vector3d(p1);
			vec.sub(p3);
		    e = new Edge(v3, v1, vec);
			tri.edges[2] = e;
			e.triangles[0] = tri;
			edges.put(new Integer(v3 * vertexCount + v1), e);
		}
	}

	/**
	 * シャドウボリュームのジオメトリの作成
	 * @param light
	 * @param shadowDepth
	 * @param localToWorld
	 * @param coordinates
	 * @param edges
	 * @param triangles
	 * @return シャドウボリュームのジオメトリ
	 */
	private IndexedQuadArray createVolumeSurface(Light light, double shadowDepth, Transform3D localToWorld, 
			double[] coordinates, Hashtable<Integer, Edge> edges, ArrayList<Triangle> triangles) {
		// オブジェクトに対する相対座標系に変換
		Vector3d displacement =  null;
		Point3d localLightPosition = null;
		Transform3D worldToLocal;
		Point3d worldLightPosition = null;
		if (light instanceof DirectionalLight) {
			// 平行光源の場合
			Vector3f lightDirectionF = new Vector3f();
			((DirectionalLight)light).getDirection(lightDirectionF);
			displacement = new Vector3d(lightDirectionF);
			displacement.scale(shadowDepth);			
			worldToLocal = new Transform3D(localToWorld);
			worldToLocal.invert();
			worldToLocal.transform(displacement);
		} else if (light instanceof PointLight) {
			// 点光源の場合
			Point3f worldLightPositionF = new Point3f();
			((PointLight)light).getPosition(worldLightPositionF);
			worldLightPosition = new Point3d(worldLightPositionF);
			localLightPosition = new Point3d(worldLightPosition);
			worldToLocal = new Transform3D(localToWorld);
			worldToLocal.invert();
			worldToLocal.transform(localLightPosition);
		} else {
			return null;
		}

		Triangle tri;
		// すべての面の光線に対する向きを計算
		if (displacement != null) {
			for (int n = 0; n < triangles.size(); n++) {
				tri = triangles.get(n);
				tri.innerProduct = tri.normal.dot(displacement);
			}
		} else if (localLightPosition != null) {
			for (int n = 0; n < triangles.size(); n++) {
				tri = triangles.get(n);
				Vector3d v = new Vector3d(coordinates[tri.v1*3], coordinates[tri.v1*3 + 1], coordinates[tri.v1*3 + 2]);
				v.sub(localLightPosition);
				tri.innerProduct = tri.normal.dot(v);
			}			
		}
		Collection<Edge> es = edges.values();
		Edge edge;
		int index = 0;
		boolean doesGenerateSurface = false;
		double i0 = 0, i1 = 0;
		Vector3d displacement1 = displacement;
		Vector3d displacement2 = displacement;
		Vector3d tempVec = new Vector3d();
		Point3d worldPos = new Point3d();
		if (localLightPosition != null) {
			// 点光源の場合
			displacement1 = new Vector3d();
			displacement2 = new Vector3d();
		}
		for (Iterator<Edge> it = es.iterator(); it.hasNext(); ) {
			edge = it.next();			
			// edgeに対してシャドウボリュームの表面を生成するか否かの判定
			doesGenerateSurface = false;
			if (edge.triangles[1] == null) {
				// 開多面体の場合
				i0 = edge.triangles[0].innerProduct;
				if (i0 < -GeometryUtility.TOLERANCE) {
					// 光源に対して表向きの面の辺に対してのみ生成
					doesGenerateSurface = true;	// 生成する
				}
			} else {
				i0 = edge.triangles[0].innerProduct;
				i1 = edge.triangles[1].innerProduct;
				if (i0 * i1 < GeometryUtility.TOLERANCE) {
					if (i0 * i1 > -GeometryUtility.TOLERANCE) {
						// 辺を挟む２つの面の一方が光線の向きに対して平行である
						if (i0 < -GeometryUtility.TOLERANCE || i1 < -GeometryUtility.TOLERANCE) {
							// いずれかの面が表向きだった場合
							tempVec.cross(edge.triangles[0].normal, edge.triangles[1].normal);
							if (edge.v1ToV2.dot(tempVec) > GeometryUtility.TOLERANCE) {
								// 辺が凸の場合のみ生成
								doesGenerateSurface = true;	// 生成する								
							}
						}
					} else {
						// 辺を挟む２つの面が光線の向きに対して裏表である
						doesGenerateSurface = true;	// 生成する
					}
				}
			}
			if (doesGenerateSurface) {
				// シャドウボリュームの表面を生成する
				if (localLightPosition != null) {
					// 点光源の場合
					displacement1.x = coordinates[edge.v1 * 3] - localLightPosition.x;
					displacement1.y = coordinates[edge.v1 * 3 + 1] - localLightPosition.y;
					displacement1.z = coordinates[edge.v1 * 3 + 2] - localLightPosition.z;
					worldPos.x = coordinates[edge.v1 * 3];
					worldPos.y = coordinates[edge.v1 * 3 + 1];
					worldPos.z = coordinates[edge.v1 * 3 + 2];
					localToWorld.transform(worldPos);
					displacement1.scale(shadowDepth / worldPos.distance(worldLightPosition));				
					displacement2.x = coordinates[edge.v2 * 3] - localLightPosition.x;
					displacement2.y = coordinates[edge.v2 * 3 + 1] - localLightPosition.y;
					displacement2.z = coordinates[edge.v2 * 3 + 2] - localLightPosition.z;
					worldPos.x = coordinates[edge.v2 * 3];
					worldPos.y = coordinates[edge.v2 * 3 + 1];
					worldPos.z = coordinates[edge.v2 * 3 + 2];
					localToWorld.transform(worldPos);
					displacement2.scale(shadowDepth / worldPos.distance(worldLightPosition));				
				}
				if (i0 < -GeometryUtility.TOLERANCE) {
					// 表を向いている面はedge.triangles[0]、edgeの向きは常にedge.triangles[0]の辺の向きと一致するので、
					// edgeの向きに従ってシャドウボリュームの表面を生成
					indicies[index] = edge.v2;
					indicies[index + 1] = edge.v1;
					indicies[index + 2] = edge.v1 + vertexCount;
					indicies[index + 3] = edge.v2 + vertexCount;
				} else {
					// 表を向いている面はedge.triangles[1]、edgeの向きは常にedge.triangles[1]の辺の向きと反対なので、
					// edgeの向きと逆向きにシャドウボリュームの表面を生成
					indicies[index] = edge.v1;
					indicies[index + 1] = edge.v2;
					indicies[index + 2] = edge.v2 + vertexCount;
					indicies[index + 3] = edge.v1 + vertexCount;
				}
				coordinates[(edge.v1 + vertexCount) * 3] = coordinates[edge.v1 * 3] + displacement1.x;
				coordinates[(edge.v1 + vertexCount) * 3 + 1] = coordinates[edge.v1 * 3 + 1] + displacement1.y;
				coordinates[(edge.v1 + vertexCount) * 3 + 2] = coordinates[edge.v1 * 3 + 2] + displacement1.z;
				coordinates[(edge.v2 + vertexCount) * 3] = coordinates[edge.v2 * 3] + displacement2.x;
				coordinates[(edge.v2 + vertexCount) * 3 + 1] = coordinates[edge.v2 * 3 + 1] + displacement2.y;
				coordinates[(edge.v2 + vertexCount) * 3 + 2] = coordinates[edge.v2 * 3 + 2] + displacement2.z;
				index += 4;
			}
		}
		if (index == 0) return null;
		int validIndicies[] = new int[index];
		System.arraycopy(indicies, 0, validIndicies, 0, index);
		volumeGeometry = new IndexedQuadArray(coordinates.length / 3, 
				IndexedQuadArray.COORDINATES | IndexedQuadArray.BY_REFERENCE, 
				index);
		volumeGeometry.setCapability(IndexedQuadArray.ALLOW_COORDINATE_READ);
		volumeGeometry.setCapability(IndexedQuadArray.ALLOW_COORDINATE_WRITE);
		volumeGeometry.setCapability(IndexedQuadArray.ALLOW_COORDINATE_INDEX_READ);
		volumeGeometry.setCapability(IndexedQuadArray.ALLOW_COORDINATE_INDEX_WRITE);
		
		volumeGeometry.setCoordRefDouble(coordinates);
		volumeGeometry.setCoordinateIndices(0, validIndicies);
		return volumeGeometry;
	}

	/**
	 * シャドウボリュームの生成
	 * @param volumeGeometry
	 * @param ｏｃｃｕｌｕｄｅｒ
	 */
	private void createShadowVolumeObject(IndexedQuadArray volumeGeometry, BaseObject3D ｏｃｃｕｌｕｄｅｒ) {
		// シャドウボリュームの表面のオブジェクトの生成
		Appearance frontAp = new Appearance();
		if (WIRE_FRAME_VIEW) frontAp.setMaterial(new Material());
		RenderingAttributes frontRA = new RenderingAttributes();
		if (!WIRE_FRAME_VIEW) frontRA.setStencilEnable(true);		// ステンシルバッファを用いる（ただし、Canvas3Dで適切な設定がされている必要がある）
		if (!WIRE_FRAME_VIEW) frontRA.setDepthBufferWriteEnable(false);							// Zバッファを更新しない
		frontRA.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);	// Zバッファの影響を受ける
		frontRA.setStencilFunction(RenderingAttributes.ALWAYS, 0, ~0);		// ステンシルバッファの影響を受けない
		frontRA.setStencilOp(RenderingAttributes.STENCIL_KEEP, 
				RenderingAttributes.STENCIL_KEEP, 
				RenderingAttributes.STENCIL_INCR);	// 見えているピクセルのみステンシルバッファの値を増やす
		frontAp.setRenderingAttributes(frontRA);
		PolygonAttributes pa1 = new PolygonAttributes();
		if (WIRE_FRAME_VIEW) pa1.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		pa1.setCullFace(PolygonAttributes.CULL_BACK);	// 裏面を処理しない
		frontAp.setPolygonAttributes(pa1);
		if (!WIRE_FRAME_VIEW) {
			TransparencyAttributes frontTA = new TransparencyAttributes();	// setVisible(false)だとステンシルバッファが更新されないので、透明にする
			frontTA.setTransparencyMode(TransparencyAttributes.BLENDED);
			frontTA.setTransparency(1.0f);
			frontAp.setTransparencyAttributes(frontTA);
		}
		shadowVolumeFront = new Shape3D(volumeGeometry, frontAp);
		shadowVolumeFront.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shadowVolumeFront.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		ｏｃｃｕｌｕｄｅｒ.center.addChild(shadowVolumeFront);
		
		// シャドウボリュームの裏面のオブジェクトの生成
		Appearance backAp= new Appearance();
		if (WIRE_FRAME_VIEW) backAp.setMaterial(new Material());
		RenderingAttributes backRA= new RenderingAttributes();
		if (!WIRE_FRAME_VIEW) backRA.setStencilEnable(true);		// ステンシルバッファを用いる（ただし、Canvas3Dで適切な設定がされている必要がある）
		if (!WIRE_FRAME_VIEW) backRA.setDepthBufferWriteEnable(false);							// Zバッファを更新しない
		backRA.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);		// Zバッファの影響を受ける
		backRA.setStencilFunction(RenderingAttributes.ALWAYS, 0, ~0);		// ステンシルバッファの影響を受けない
		backRA.setStencilOp(RenderingAttributes.STENCIL_KEEP, 
				RenderingAttributes.STENCIL_KEEP, 
				RenderingAttributes.STENCIL_DECR);	// 見えているピクセルのみステンシルバッファの値を減らす
		backAp.setRenderingAttributes(backRA);
		PolygonAttributes pa2 = new PolygonAttributes();
		if (WIRE_FRAME_VIEW) pa2.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		pa2.setCullFace(PolygonAttributes.CULL_FRONT);	// 表面を処理しない
		backAp.setPolygonAttributes(pa2);
		if (!WIRE_FRAME_VIEW) {
			TransparencyAttributes backTA = new TransparencyAttributes();	// setVisible(false)だとステンシルバッファが更新されないので、透明にする
			backTA.setTransparencyMode(TransparencyAttributes.BLENDED);
			backTA.setTransparency(1.0f);
			backAp.setTransparencyAttributes(backTA);
		}
		shadowVolumeBack = new Shape3D(volumeGeometry, backAp);
		shadowVolumeBack.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		shadowVolumeBack.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		ｏｃｃｕｌｕｄｅｒ.center.addChild(shadowVolumeBack);
	}
	
	private class Triangle {
		Edge edges[] = new Edge[3];
		Vector3d normal;
		double innerProduct;
		int v1, v2, v3;
		
		Triangle(int v1, int v2, int v3, Vector3d normal) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
			this.normal = normal;
		}
	}
	
	private class Edge {
		Triangle triangles[] = new Triangle[2];
		int v1, v2;
		Vector3d v1ToV2;
//		Vector3d normal = null;
		
		Edge(int v1, int v2, Vector3d v1ToV2) {
			this.v1 = v1;
			this.v2 = v2;
			this.v1ToV2 = v1ToV2;
		}
		
//		Vector3d normal() {
//			if (normal != null) return normal;
//			normal = new Vector3d(triangles[0].normal);
//			if (triangles[1] != null) {
//				normal.add(triangles[1].normal);
//				normal.scale(0.5);
//			}
//			return normal;
//		}
	}
}
