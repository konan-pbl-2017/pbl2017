package framework.model3D;

import java.util.ArrayList;
import java.util.Stack;

import javax.media.j3d.Geometry;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.IndexedTriangleStripArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Stripifier;

public class GeometryCollector extends ObjectVisitor{
	private int totalVertexCount = 0;
	private int totalIndexCount = 0;
	private ArrayList<Geometry> geometryList = new ArrayList<Geometry>(); // 葉のとき、Geometryを保存
	IndexedGeometryArray geometry = null;
//	private Stack<Transform3D> transforms = new Stack<Transform3D>();
	
	int i=0;
	int j=0;
	
	//コンストラクタ
//	public GeometryCollector() {
//		Transform3D t = new Transform3D();
//		transforms.push(t);
//	}
	
	@Override
	public void preVisit(Object3D obj) {
		// TODO Auto-generated method stub
		if(obj.hasChildren()){
//			System.out.println("pre"+i);
//			i++;
		}else{
//			System.out.println("pre"+i+" Leaf");
//			i++;
			Geometry g = ((Shape3D)obj.getBody().getPrimitiveNode()).getGeometry();
			geometryList.add(g);
			totalVertexCount += ((IndexedGeometryArray)g).getVertexCount();
			totalIndexCount += ((IndexedGeometryArray)g).getIndexCount();
		}
		
		
	}
	
	@Override
	public void postVisit(Object3D obj) {
		// TODO Auto-generated method stub
		if(obj.hasChildren()){
//			System.out.println("post"+j);
			j++;
		}else{
//			System.out.println("post"+j+"   Leaf");
			j++;
		}
	}

	public Geometry getGeometry(){
		if (geometry == null) {
			// リスト中の複数の Geometry を1つにまとめる
			int coordinateOfs = 0;
			int indexOfs = 0;
			double coodinates[] = new double[totalVertexCount * 3];
			int indicies[] = new int[totalIndexCount];
			for (int n = 0; n < geometryList.size(); n++) {
				IndexedGeometryArray geometry = (IndexedGeometryArray)geometryList.get(n);
				double tmpCoordinates[] = new double[geometry.getVertexCount() * 3];
				geometry.getCoordinates(0, tmpCoordinates);
				System.arraycopy(tmpCoordinates, 0, coodinates, coordinateOfs * 3, geometry.getVertexCount() * 3);
				int tmpIndicies[] = new int[geometry.getIndexCount()];
				geometry.getCoordinateIndices(0, tmpIndicies);
				for (int m = 0; m < geometry.getIndexCount(); m++) {
					tmpIndicies[m] += coordinateOfs;
				}
				System.arraycopy(tmpIndicies, 0, indicies, indexOfs, geometry.getIndexCount());
				coordinateOfs += geometry.getVertexCount();
				indexOfs += geometry.getIndexCount();
			}
			geometry = new IndexedTriangleArray(totalVertexCount, IndexedTriangleArray.COORDINATES, totalIndexCount);
			geometry.setCoordinates(0, coodinates);
			geometry.setCoordinateIndices(0, indicies);
						
			// 重複している頂点を1つにまとめる
			GeometryUtility.compressGeometry(geometry);
			
//			// IndexedTriangleStripArray　に変換（重複している頂点を1つにまとめるため）
//			GeometryInfo gi = new GeometryInfo(geometry);
//			Stripifier sf = new Stripifier();
//			sf.stripify(gi);
//			geometry = gi.getIndexedGeometryArray();	// IndexedTriangleStripArray が返される
//			
//			// IndexedTriangleArray に変換（GeometryGraph が IndexedTriangleStripArray に未対応のため）
//			if (geometry instanceof IndexedTriangleStripArray) {
//				int numStrips = ((IndexedTriangleStripArray)geometry).getNumStrips();
//				int indexCounts[] = new int[numStrips];
//				((IndexedTriangleStripArray)geometry).getStripIndexCounts(indexCounts);
//				int dstIndicies[] = new int[(((IndexedTriangleStripArray)geometry).getIndexCount() - numStrips * 2) * 3];
//				int dstOfs = 0;
//				int srcOfs = 0;
//				for (int s = 0; s < numStrips; s++) {
//					for (int i = 2; i < indexCounts[s]; i += 2) {
//						dstIndicies[dstOfs] = ((IndexedTriangleStripArray)geometry).getCoordinateIndex(srcOfs + i - 2);
//						dstIndicies[dstOfs + 1] = ((IndexedTriangleStripArray)geometry).getCoordinateIndex(srcOfs + i - 1);
//						dstIndicies[dstOfs + 2] = ((IndexedTriangleStripArray)geometry).getCoordinateIndex(srcOfs + i);
//						dstOfs += 3;
//						if (i + 1 < indexCounts[s]) {
//							dstIndicies[dstOfs] = ((IndexedTriangleStripArray)geometry).getCoordinateIndex(srcOfs + i);
//							dstIndicies[dstOfs + 1] = ((IndexedTriangleStripArray)geometry).getCoordinateIndex(srcOfs + i - 1);
//							dstIndicies[dstOfs + 2] = ((IndexedTriangleStripArray)geometry).getCoordinateIndex(srcOfs + i + 1);
//							dstOfs += 3;
//						}
//					}
//					srcOfs += indexCounts[s];
//				}
//				
//				int vertexCount = ((IndexedTriangleStripArray)geometry).getVertexCount();
//				coodinates = new double[vertexCount * 3];
//				((IndexedTriangleStripArray)geometry).getCoordinates(0, coodinates);
//				
//				geometry = new IndexedTriangleArray(vertexCount, IndexedTriangleArray.COORDINATES, dstIndicies.length);
//				geometry.setCoordinates(0, coodinates);
//				geometry.setCoordinateIndices(0, dstIndicies);				
//			}
		}
		return geometry;
	}
}
