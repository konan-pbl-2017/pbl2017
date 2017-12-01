package framework.AI;

import java.util.ArrayList;

import javax.media.j3d.Geometry;
import javax.media.j3d.IndexedTriangleArray;
import javax.vecmath.Point3d;

import framework.model3D.Position3D;

public class GeometryGraph extends StateMachine {
	// コンストラクタ
	public GeometryGraph(Geometry g) {
		if (g instanceof IndexedTriangleArray) {

			IndexedTriangleArray ita = (IndexedTriangleArray) g;

			// itaからstatesにデータを書き込み
			for (int i = 0; i < ita.getIndexCount() / 3; i++) {// 面の数だけループ
				Location e = new Location(i, ita);
				if (e.getNormal() != null) {
					states.add(e);
				}
			}

			// 関連付け
			for (int i = 0; i < states.size(); i++) {
				setSuccessors(i, ita);
			}
		}
	}
	

	// 連結成分を関連付け
	public void setSuccessors(int index, IndexedTriangleArray ita) {
		Location e = (Location)states.get(index);
		
		// 探索
		int[] List = new int[ita.getIndexCount()];

		for (int i = 0; i < ita.getIndexCount(); i++) {
			List[i] = ita.getCoordinateIndex(i);
		}

		// -1で初期化
		for (int i = 0; i < 3; i++) {
			e.successorIndex[i] = -1;
			e.IndexOfSharedEdge[i] = -1;
		}

		int j = 0;

		for (int i = 0; i < states.size() * 3; i++) {
			// System.out.println("indexList[0] とList["+i+"] の照合");
			// //////idでＬｉｓｔを置き換えてメソッド呼び出しを少なく?
			// int id = ita.getCoordinateIndex(i);
			if (e.indexList[0] == List[i]) {
				if (i % 3 == 0) {
					if (e.indexList[1] == List[i + 2]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[0] = e.indexList[0];
						e.IndexOfSharedEdge[1] = e.indexList[1];
						e.addSuccessor(states.get(i / 3));
						j++;
					} else if (e.indexList[2] == List[i + 1]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[0] = e.indexList[0];
						e.IndexOfSharedEdge[2] = e.indexList[2];
						e.addSuccessor(states.get(i / 3));
						j++;
					}
				} else if (i % 3 == 1) {
					if (e.indexList[1] == List[i - 1]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[0] = e.indexList[0];
						e.IndexOfSharedEdge[1] = e.indexList[1];
						e.addSuccessor(states.get(i / 3));
						j++;
					} else if (e.indexList[2] == List[i + 1]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[0] = e.indexList[0];
						e.IndexOfSharedEdge[2] = e.indexList[2];
						e.addSuccessor(states.get(i / 3));
						j++;
					}
				} else if (i % 3 == 2) {
					if (e.indexList[1] == List[i - 1]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[0] = e.indexList[0];
						e.IndexOfSharedEdge[1] = e.indexList[1];
						e.addSuccessor(states.get(i / 3));
						j++;
					} else if (e.indexList[2] == List[i - 2]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[0] = e.indexList[0];
						e.IndexOfSharedEdge[2] = e.indexList[2];
						e.addSuccessor(states.get(i / 3));
						j++;
					}
				}

			}
		}

		for (int i = 0; i < states.size() * 3; i++) {
			// System.out.println("indexList[1] とList["+i+"] の照合");
			if (e.indexList[1] == List[i]) {
				if (i % 3 == 0) {
					if (e.indexList[2] == List[i + 2]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[1] = e.indexList[1];
						e.IndexOfSharedEdge[2] = e.indexList[2];
						e.addSuccessor(states.get(i / 3));
						j++;
					}
				} else if (i % 3 == 1) {
					if (e.indexList[2] == List[i - 1]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[1] = e.indexList[1];
						e.IndexOfSharedEdge[2] = e.indexList[2];
						e.addSuccessor(states.get(i / 3));
						j++;
					}
				} else if (i % 3 == 2) {
					if (e.indexList[2] == List[i - 2]) {
						e.successorIndex[j] = i / 3;
						e.IndexOfSharedEdge[1] = e.indexList[1];
						e.IndexOfSharedEdge[2] = e.indexList[2];
						e.addSuccessor(states.get(i / 3));
						j++;
					}
				}
			}
		}

		// System.out.println("1つ目のsuccessor**"+successorIndex[0]+"***");
		// System.out.println("2つ目のsuccessor**"+successorIndex[1]+"***");
		// System.out.println("3つ目のsuccessor**"+successorIndex[2]+"***");

		return;
	}


	// アクセッサ
	public ArrayList<IState> getStates() {
		return states;
	}

	public Location getNearestLocation(Position3D pos) {
		Location nearest = null;
		double distance = 0.0;
		for (int n = 0; n < states.size(); n++) {
			Location loc = (Location)states.get(n);
			if (nearest == null) {
				nearest = loc;
				distance = nearest.getCenter().distance(
						new Point3d(pos.getVector3d()));
			} else {
				double d = loc.getCenter().distance(
						new Point3d(pos.getVector3d()));
				if (d < distance) {
					nearest = loc;
					distance = d;
				}
			}
		}
		return nearest;
	}
}
