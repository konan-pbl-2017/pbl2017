package framework.AI;

import java.util.*;

/**
 * A*クラス。
 */
public class AStar {
	private LinkedList<Location> locationOpenList = new LinkedList<Location>();
	private LinkedList<Location> locationClosedList = new LinkedList<Location>();

	/**
	 * 最短経路探査。 最短経路のリストを返すか、見つからない場合nullを返す。
	 * start = goalの場合もnullを返す。
	 * 戻り値にはgoalからstartの１個前まで経路をさかのぼったものが入っている。startは入っていない。
	 */
	public Plan getPath(Location startLocation,
			Location goalLocation) {
		
		//出発地点と目的地点が同じならnullを返す
		if (startLocation.equals(goalLocation)) {
			return null;
		}
		
		// 初期化
		locationOpenList = new LinkedList<Location>();
		locationClosedList = new LinkedList<Location>();
		
		// 出発地点をLocationOpenListに追加する
		locationOpenList.add(startLocation);
		while (!locationOpenList.isEmpty()) {
			// LocationOpenListの最小スコアの地点をcurLocationとして取り出す
			Collections.sort(locationOpenList, new CostComparator());
			Location curLocation = locationOpenList.removeFirst();

			// 現在の地点が目的地点ならば探査完了
			if (curLocation.equals(goalLocation)) {
				LinkedList<Location> locationPath = new LinkedList<Location>();

				// 出発地点まで通った地点を辿る
				curLocation = goalLocation;
				while (!curLocation.equals(startLocation)) {
					locationPath.add(curLocation);
					curLocation = (Location) curLocation.getParent();
				}
				return new Plan(locationPath);
			}

			// 現在の地点が目的地点でないなら、現在の地点をLocationClosedListに移す
			locationClosedList.add(curLocation);

			// 隣接する地点を調べる
			ArrayList<IState> neighbors = curLocation.getSuccessors();

			// 隣接する地点の数だけ、ループ
			for (int i = 0; i < neighbors.size(); i++) {
				// 通過でき、各リストに入ってないならコストをもらい、
				if (!locationOpenList.contains(neighbors.get(i))
						&& !locationClosedList.contains(neighbors.get(i))) {
					//地点コストを求める
					//agent.getLocationCost((Location)neighbors.get(i), null, null, null);
					
					//パスコストを求める
					((Location)neighbors.get(i)).calculatesCosts(curLocation, goalLocation);

					// LocationOpenListに移す
					locationOpenList.add((Location) neighbors.get(i));
				}
			}

			// -------------------------------------------------------------------------------------------------------------------------------------------

		}
		return null;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------
	// コストの昇順ソートのためのクラスを作ろう

	/**
	 * ソート条件式クラス。
	 */
	class CostComparator implements Comparator<Location> {
		public int compare(Location n1, Location n2) {
			return n1.getScore() - n2.getScore();
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------

}
