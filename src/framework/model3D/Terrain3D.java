package framework.model3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.Shape3D;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class Terrain3D extends BaseObject3D {
	static final int MESH_SIZE = 4;
	private Position3D origin;
	private double sizeX = 0;
	private double sizeZ = 0;
	private double heightMap[][] = null;
	private int meshX = 0;
	private int meshZ = 0;
	private TriangleStripArray triStripAttay = null;
	
	public Terrain3D(Position3D origin, double sizeX, double sizeZ, double heightMap[][], Appearance a) {
		super();
		this.origin = origin;
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		this.heightMap = heightMap;
		meshX = heightMap[0].length;
		meshZ = heightMap.length;
		
		int stripVertexCounts[] = new int[meshZ - 1];
		for (int n = 0; n < meshZ - 1; n++) {
			stripVertexCounts[n] = meshX * 2;
		}
		triStripAttay = new TriangleStripArray(meshX * 2 * (meshZ - 1),
				TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS, 
				stripVertexCounts);
		int index = 0;
		for (int z = 0; z < meshZ; z++) {
			for (int x = 0; x < meshX; x++) {
				// メッシュごとに三角形を2つづつ生成
				if (z < meshZ - 1) {
					// 頂点座標の設定
					// 頂点の法線ベクトルの設定
					triStripAttay.setCoordinate(index, 
							new Point3d(origin.getX() + sizeX * (double)x, 
									origin.getY() + heightMap[z][x], 
									origin.getZ() + sizeZ * (double)z));
					Vector3f normal = calcNormal(z, x);
					normal.normalize();
					triStripAttay.setNormal(index, normal);
					
					triStripAttay.setCoordinate(index + 1, 
							new Point3d(origin.getX() + sizeX * (double)x, 
									origin.getY() + heightMap[z + 1][x], 
									origin.getZ() + sizeZ * (double)(z + 1)));
					normal = calcNormal(z + 1, x);
					normal.normalize();
					triStripAttay.setNormal(index + 1, normal);
					
					index += 2;
				}
			}
		}
		Appearance a2 = (Appearance)a.cloneNodeComponent(true);
		a2.setCapability(Appearance.ALLOW_MATERIAL_READ);
		a2.setCapability(Appearance.ALLOW_TEXTURE_READ);
		a2.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		a2.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
		a2.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
		Shape3D shape = new Shape3D(triStripAttay, a2);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		center.addChild(shape);		
	}

	private Vector3f calcNormal(int z, int x) {
		Vector3f normal = new Vector3f();
		if (x < meshX - 1) {
			Vector3f xp = new Vector3f((float)sizeX, (float)(heightMap[z][x+1] - heightMap[z][x]), 0.0f);
			if (z < meshZ - 1) {
				Vector3f zp = new Vector3f(0.0f, (float)(heightMap[z+1][x] - heightMap[z][x]), (float)sizeZ);
				Vector3f v1 = new Vector3f();
				v1.cross(zp, xp);
				v1.normalize();
				normal.add(v1);
			}
			if (z > 0) {
				Vector3f zm = new Vector3f(0.0f, (float)(heightMap[z-1][x] - heightMap[z][x]), -(float)sizeZ);
				Vector3f v1 = new Vector3f();
				v1.cross(xp, zm);
				v1.normalize();
				normal.add(v1);
			}
		}
		if (x > 0) {
			Vector3f xm = new Vector3f(-(float)sizeX, (float)(heightMap[z][x-1] - heightMap[z][x]), 0.0f);					
			if (z < meshZ - 1) {
				Vector3f zp = new Vector3f(0.0f, (float)(heightMap[z+1][x] - heightMap[z][x]), (float)sizeZ);
				Vector3f v1 = new Vector3f();
				v1.cross(xm, zp);
				v1.normalize();
				normal.add(v1);
			}
			if (z > 0) {
				Vector3f zm = new Vector3f(0.0f, (float)(heightMap[z-1][x] - heightMap[z][x]), -(float)sizeZ);
				Vector3f v1 = new Vector3f();
				v1.cross(zm, xm);
				v1.normalize();
				normal.add(v1);
			}
		}
		return normal;
	}

	public BoundingSurface[] getBoundingSurfaces() {
		if (boundingSurfaces == null) {
			if (triStripAttay == null) return null;
			double coordinate1[] = new double[3];
			double coordinate2[] = new double[3];
			double coordinate3[] = new double[3];
			double coordinate4[] = new double[3];

			// GeometryArrayの習得
			if (triStripAttay instanceof TriangleStripArray) {
				// IndexedTriangleStripArray の場合
				// 8 x 8 メッシュ単位でまとめる
				BoundingSurface surfaces[] = new BoundingSurface[((meshX + MESH_SIZE - 2) / MESH_SIZE) * ((meshZ + MESH_SIZE - 2) / MESH_SIZE)];
				int n = 0;
				for (int j = 0; j < meshZ - 1; j += MESH_SIZE) {
					for (int i = 0; i < meshX - 1; i += MESH_SIZE) {
						BoundingSurface parent = new BoundingSurface();
						double lowY = 1.0;
						double highY = -1.0;
						for (int j2 = 0; j + j2 < meshZ - 1 && j2 < MESH_SIZE; j2++) {
							for (int i2 = 0; i + i2 < meshX - 1 && i2 < MESH_SIZE; i2++) {
								int index = (j + j2) * meshX * 2 + (i + i2) * 2;
								triStripAttay.getCoordinates(index, coordinate1);
								triStripAttay.getCoordinates(index + 1, coordinate2);
								triStripAttay.getCoordinates(index + 2, coordinate3);
								triStripAttay.getCoordinates(index + 3, coordinate4);
								if (lowY > highY) {
									lowY = highY = coordinate1[1];
								} else {
									if (lowY > coordinate1[1]) {
										lowY = coordinate1[1];
									} else if (highY < coordinate1[1]) {
										highY = coordinate1[1];
									}
									if (lowY > coordinate2[1]) {
										lowY = coordinate2[1];
									} else if (highY < coordinate2[1]) {
										highY = coordinate2[1];
									}
									if (lowY > coordinate3[1]) {
										lowY = coordinate3[1];
									} else if (highY < coordinate3[1]) {
										highY = coordinate3[1];
									}
									if (lowY > coordinate4[1]) {
										lowY = coordinate4[1];
									} else if (highY < coordinate4[1]) {
										highY = coordinate4[1];
									}
								}
								
								// 1 x 1 メッシュ内のBoundingSurface
								Vector3d v1 = new Vector3d(coordinate1);
								Vector3d v2 = new Vector3d(coordinate2);
								Vector3d v3 = new Vector3d(coordinate3);
								Vector3d v4 = new Vector3d(coordinate4);
								BoundingSurface bSurface = new BoundingSurface();
								bSurface.addVertex((Vector3d)v1.clone());	//1つめの三角形
								bSurface.addVertex((Vector3d)v2.clone());
								bSurface.addVertex((Vector3d)v3.clone());
								bSurface.setBounds(createBoundingPolytope(v1, v2, v3));
								parent.addChild(bSurface, false);
								
								bSurface = new BoundingSurface();
								bSurface.addVertex((Vector3d)v2.clone());	//2つめの三角形
								bSurface.addVertex((Vector3d)v4.clone());
								bSurface.addVertex((Vector3d)v3.clone());
								bSurface.setBounds(createBoundingPolytope(v2, v4, v3));
								parent.addChild(bSurface, true);
							}
						}
						// 8 x 8 メッシュ単位のBoundingSurface
						triStripAttay.getCoordinates(j * meshX * 2 + i * 2, coordinate1);
						parent.setBounds(new BoundingBox(new Point3d(coordinate1[0], lowY, coordinate1[2]), 
								new Point3d(coordinate4[0], highY, coordinate4[2])));
						surfaces[n] = parent;
						n++;
					}
				}
				boundingSurfaces = surfaces;
			} else {
				return null;
			}
		}
		return boundingSurfaces;
	}
	
	public double getHeight(double x, double z) {
		int i = (int)((x - origin.getX()) / sizeX);
		int j = (int)((z - origin.getZ()) / sizeZ);
		if (i >= meshX || i < 0 || j >= meshZ || j < 0) return 0.0;

		int index = j * meshX * 2 + i * 2;
		double coordinate1[] = new double[3];
		double coordinate2[] = new double[3];
		double coordinate3[] = new double[3];
		double coordinate4[] = new double[3];
		triStripAttay.getCoordinates(index, coordinate1);
		triStripAttay.getCoordinates(index + 1, coordinate2);
		triStripAttay.getCoordinates(index + 2, coordinate3);
		triStripAttay.getCoordinates(index + 3, coordinate4);
		Vector3d v1 = new Vector3d(coordinate1);
		Vector3d v2 = new Vector3d(coordinate2);
		Vector3d v3 = new Vector3d(coordinate3);
		Vector3d v4 = new Vector3d(coordinate4);
		Vector3d p1 = new Vector3d(x, 0.0, z);
		Vector3d p2 = new Vector3d(x, 1.0, z);
		
		double x2 = x -  (double)i * sizeX - origin.getX();
		double z2 = z - (double)j * sizeZ - origin.getZ();
		if (x2 < GeometryUtility.TOLERANCE || (meshZ - z2) / x2 > meshZ / meshX) {
			// 1つめの三角形を通る場合
			Vector3d crossPoint = GeometryUtility.intersect(GeometryUtility.createPlane(v1, v2, v3), p1, p2);
			return crossPoint.getY();
		} else {
			// 2つめの三角形を通る場合
			Vector3d crossPoint = GeometryUtility.intersect(GeometryUtility.createPlane(v2, v4, v3), p1, p2);
			return crossPoint.getY();
		}
	}
}

//
// ********** IndexedTriangleStripArray を使うバージョン **********
// (IndexedTriangleStripArrayを用いるとメモリを節約できるが、シェーディングができないので注意)
//
//public class Terrain3D extends BaseObject3D {
//	static final int MESH_SIZE = 8;
//	private Position3D origin;
//	private double sizeX = 0;
//	private double sizeZ = 0;
//	private double heightMap[][] = null;
//	private int meshX = 0;
//	private int meshZ = 0;
//	private IndexedTriangleStripArray triStripAttay = null;
//	
//	public Terrain3D(Position3D origin, double sizeX, double sizeZ, double heightMap[][], Appearance a) {
//		super();
//		this.origin = origin;
//		this.sizeX = sizeX;
//		this.sizeZ = sizeZ;
//		this.heightMap = heightMap;
//		meshX = heightMap[0].length;
//		meshZ = heightMap.length;
//		
//		int stripVertexCounts[] = new int[meshZ - 1];
//		for (int n = 0; n < meshZ - 1; n++) {
//			stripVertexCounts[n] = meshX * 2;
//		}
//		triStripAttay = new IndexedTriangleStripArray(meshX * meshZ,
//				IndexedTriangleStripArray.COORDINATES | IndexedTriangleStripArray.NORMALS, 
//				meshX * 2 * (meshZ - 1),
//				stripVertexCounts);
//		int index = 0, index2 = 0, i1, i2;
//		for (int z = 0; z < meshZ; z++) {
//			for (int x = 0; x < meshX; x++) {
//				// 頂点座標の設定
//				triStripAttay.setCoordinate(index, 
//						new Point3d(origin.getX() + sizeX * (double)x, 
//								origin.getY() + heightMap[z][x], 
//								origin.getZ() + sizeZ * (double)z));
//				// 頂点の法線ベクトルの設定
//				Vector3f normal = new Vector3f();
//				if (x < meshX - 1) {
//					Vector3f xp = new Vector3f((float)sizeX, (float)(heightMap[z][x+1] - heightMap[z][x]), 0.0f);
//					if (z < meshZ - 1) {
//						Vector3f zp = new Vector3f(0.0f, (float)(heightMap[z+1][x] - heightMap[z][x]), (float)sizeZ);
//						Vector3f v1 = new Vector3f();
//						v1.cross(zp, xp);
//						v1.normalize();
//						normal.add(v1);
//					}
//					if (z > 0) {
//						Vector3f zm = new Vector3f(0.0f, (float)(heightMap[z-1][x] - heightMap[z][x]), -(float)sizeZ);
//						Vector3f v1 = new Vector3f();
//						v1.cross(xp, zm);
//						v1.normalize();
//						normal.add(v1);
//					}
//				}
//				if (x > 0) {
//					Vector3f xm = new Vector3f(-(float)sizeX, (float)(heightMap[z][x-1] - heightMap[z][x]), 0.0f);					
//					if (z < meshZ - 1) {
//						Vector3f zp = new Vector3f(0.0f, (float)(heightMap[z+1][x] - heightMap[z][x]), (float)sizeZ);
//						Vector3f v1 = new Vector3f();
//						v1.cross(xm, zp);
//						v1.normalize();
//						normal.add(v1);
//					}
//					if (z > 0) {
//						Vector3f zm = new Vector3f(0.0f, (float)(heightMap[z-1][x] - heightMap[z][x]), -(float)sizeZ);
//						Vector3f v1 = new Vector3f();
//						v1.cross(zm, xm);
//						v1.normalize();
//						normal.add(v1);
//					}
//				}
//				normal.normalize();
//				triStripAttay.setNormal(index, normal);
//				index++;
//				// メッシュごとに三角形を2つづつ生成
//				if (z < meshZ - 1) {
//					i1 = z * meshX + x;
//					i2 = (z + 1) * meshX + x;
//					triStripAttay.setCoordinateIndices(index2, new int[]{i1, i2});
//					index2 += 2;
//				}
//			}
//		}
//		Appearance a2 = (Appearance)a.cloneNodeComponent(true);
//		a2.setCapability(Appearance.ALLOW_MATERIAL_READ);
//		a2.setCapability(Appearance.ALLOW_TEXTURE_READ);
//		a2.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
//		a2.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_READ);
//		a2.setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
//		Shape3D shape = new Shape3D(triStripAttay, a2);
//		shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
//		shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
//		center.addChild(shape);		
//	}
//
//	public BoundingSurface[] getBoundingSurfaces() {
//		if (boundingSurfaces == null) {
//			if (triStripAttay == null) return null;
//			double coordinate1[] = new double[3];
//			double coordinate2[] = new double[3];
//			double coordinate3[] = new double[3];
//			double coordinate4[] = new double[3];
//
//			// GeometryArrayの習得
//			if (triStripAttay instanceof IndexedTriangleStripArray) {
//				// IndexedTriangleStripArray の場合
//				// 8 x 8 メッシュ単位でまとめる
//				BoundingSurface surfaces[] = new BoundingSurface[((meshX + MESH_SIZE - 2) / MESH_SIZE) * ((meshZ + MESH_SIZE - 2) / MESH_SIZE)];
//				int n = 0;
//				for (int j = 0; j < meshZ - 1; j += MESH_SIZE) {
//					for (int i = 0; i < meshX - 1; i += MESH_SIZE) {
//						BoundingSurface parent = new BoundingSurface();
//						double lowY = 1.0;
//						double highY = -1.0;
//						for (int j2 = 0; j + j2 < meshZ - 1 && j2 < MESH_SIZE; j2++) {
//							for (int i2 = 0; i + i2 < meshX - 1 && i2 < MESH_SIZE; i2++) {
//								int index = (j + j2) * meshX * 2 + (i + i2) * 2;
//								triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index), coordinate1);
//								triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+1), coordinate2);
//								triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+2), coordinate3);
//								triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+3), coordinate4);
//								if (lowY > highY) {
//									lowY = highY = coordinate1[1];
//								} else {
//									if (lowY > coordinate1[1]) {
//										lowY = coordinate1[1];
//									} else if (highY < coordinate1[1]) {
//										highY = coordinate1[1];
//									}
//									if (lowY > coordinate2[1]) {
//										lowY = coordinate2[1];
//									} else if (highY < coordinate2[1]) {
//										highY = coordinate2[1];
//									}
//									if (lowY > coordinate3[1]) {
//										lowY = coordinate3[1];
//									} else if (highY < coordinate3[1]) {
//										highY = coordinate3[1];
//									}
//									if (lowY > coordinate4[1]) {
//										lowY = coordinate4[1];
//									} else if (highY < coordinate4[1]) {
//										highY = coordinate4[1];
//									}
//								}
//								
//								// 1 x 1 メッシュ内のBoundingSurface
//								Vector3d v1 = new Vector3d(coordinate1);
//								Vector3d v2 = new Vector3d(coordinate2);
//								Vector3d v3 = new Vector3d(coordinate3);
//								Vector3d v4 = new Vector3d(coordinate4);
//								BoundingSurface bSurface = new BoundingSurface();
//								bSurface.addVertex((Vector3d)v1.clone());	//1つめの三角形
//								bSurface.addVertex((Vector3d)v2.clone());
//								bSurface.addVertex((Vector3d)v3.clone());
//								bSurface.setBounds(createBoundingPolytope(v1, v2, v3));
//								parent.addChild(bSurface, false);
//								
//								bSurface = new BoundingSurface();
//								bSurface.addVertex((Vector3d)v2.clone());	//2つめの三角形
//								bSurface.addVertex((Vector3d)v4.clone());
//								bSurface.addVertex((Vector3d)v3.clone());
//								bSurface.setBounds(createBoundingPolytope(v2, v4, v3));
//								parent.addChild(bSurface, true);
//							}
//						}
//						// 8 x 8 メッシュ単位のBoundingSurface
//						triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(j * meshX * 2 + i * 2), coordinate1);
//						parent.setBounds(new BoundingBox(new Point3d(coordinate1[0], lowY, coordinate1[2]), 
//								new Point3d(coordinate4[0], highY, coordinate4[2])));
//						surfaces[n] = parent;
//						n++;
//					}
//				}
//				boundingSurfaces = surfaces;
//			} else {
//				return null;
//			}
//		}
//		return boundingSurfaces;
//	}
//	
//	public double getHeight(double x, double z) {
//		int i = (int)((x - origin.getX()) / sizeX);
//		int j = (int)((z - origin.getZ()) / sizeZ);
//		if (i >= meshX || i < 0 || j >= meshZ || j < 0) return 0.0;
//
//		int index = j * meshX * 2 + i * 2;
//		double coordinate1[] = new double[3];
//		double coordinate2[] = new double[3];
//		double coordinate3[] = new double[3];
//		double coordinate4[] = new double[3];
//		triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index), coordinate1);
//		triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+1), coordinate2);
//		triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+2), coordinate3);
//		triStripAttay.getCoordinates(triStripAttay.getCoordinateIndex(index+3), coordinate4);
//		Vector3d v1 = new Vector3d(coordinate1);
//		Vector3d v2 = new Vector3d(coordinate2);
//		Vector3d v3 = new Vector3d(coordinate3);
//		Vector3d v4 = new Vector3d(coordinate4);
//		Vector3d p1 = new Vector3d(x, 0.0, z);
//		Vector3d p2 = new Vector3d(x, 1.0, z);
//		
//		double x2 = x -  (double)i * sizeX - origin.getX();
//		double z2 = z - (double)j * sizeZ - origin.getZ();
//		if (x2 < GeometryUtility.TOLERANCE || (meshZ - z2) / x2 > meshZ / meshX) {
//			// 1つめの三角形を通る場合
//			Vector3d crossPoint = GeometryUtility.intersect(GeometryUtility.createPlane(v1, v2, v3), p1, p2);
//			return crossPoint.getY();
//		} else {
//			// 2つめの三角形を通る場合
//			Vector3d crossPoint = GeometryUtility.intersect(GeometryUtility.createPlane(v2, v4, v3), p1, p2);
//			return crossPoint.getY();
//		}
//	}
//}
