package framework.AI;

import java.util.LinkedList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import framework.model3D.Position3D;

public class Plan {
	private LinkedList<Location> path;		// 計画内容を表すパス
	private int currentLoc = 0;		// パス上の現在の Location 

	/**
	 * 複数の通過点を持つ計画
	 * @param locationPath
	 */
	public Plan(LinkedList<Location> locationPath) {
		path = locationPath;
		currentLoc = locationPath.size() - 1;
	}
	
	/**
	 * スタートとゴールをダイレクトに結ぶ計画
	 * @param start
	 * @param goal
	 */
	public Plan(Location start, Location goal) {
		path = new LinkedList<Location>();
		path.add(goal);
		path.add(start);
		currentLoc = 1;
	}

	/**
	 * 現在の Location を取得する
	 * @return　現在の Location, すでにゴールに着いているときは null を返す
	 */
	public Location getCurrentLocation() {
		if (currentLoc <= 0) return null;
		return path.get(currentLoc);
	}

	/**
	 * 次の Location を取得する
	 * @return　次の Location, すでにゴールに着いているときは null を返す
	 */
	public Location getNextLocation() {
		if (currentLoc <= 0) return null;
		return path.get(currentLoc - 1);
	}

	/**
	 * 現在の座標値を元に、現在の Location を更新する
	 * @param position　現在の座標値
	 * @return　更新した --- true, 以前のまま --- false
	 */
	public boolean updateCurrentLocation(Position3D position) {
		Vector3d toCurrentPosition = position.getVector3d();
		Location curLocation = getCurrentLocation();
		if (curLocation == null) return true;
		toCurrentPosition.sub(curLocation.getCenter());
		double distanceToCurrentPosition = toCurrentPosition.length();
		Vector3d toNextLocation = new Vector3d(getNextLocation().getCenter());
		toNextLocation.sub(curLocation.getCenter());
		double distanceToNextLocation = toNextLocation.length();
		if (distanceToCurrentPosition >= distanceToNextLocation) {
			// 次の Location を通り過ぎた場合
			currentLoc--;
			return true;
		}
		return false;
	}

}
