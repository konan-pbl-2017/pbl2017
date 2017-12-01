package template.shooting;

import java.util.LinkedList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import framework.AI.AStar;
import framework.AI.GeometryGraph;
import framework.AI.Location;
import framework.AI.Plan;
import framework.animation.Animation3D;
import framework.gameMain.OvergroundActor;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.physics.Ground;
import framework.physics.Velocity3D;

/**
 * 敵
 * @author 新田直也
 *
 */
public class Enemy extends OvergroundActor {
	private AStar aStar = new AStar();
	private Plan plan = null;
	
	// 定数
	public static final double ENEMY_SPEED = 3.0;
	public static final double BULLET_SPEED = 8.0;
	
	public Enemy(Object3D body, Animation3D animation) {
		super(body, animation);
	}
	
	/**
	 * 単位時間ごとの動作（衝突判定処理も行う）
	 * @param interval --- 前回呼び出されたときからの経過時間（ミリ秒単位）
	 * @param ground --- 地面（構造物）
	 * @param player --- プレイヤーの位置を考慮して移動計画を立てるので必要
	 * @param geometryGraph --- グラフによって単純化した地面
	 */
	public void motion(long interval, Ground ground, Player player, GeometryGraph geometryGraph) {
		if (!doPlan(plan) || Math.random() < 0.005) {
			// 計画が完了するか、気まぐれで新しい計画を立てる
			plan = planning(player, geometryGraph);
			startPlan(plan);
		}
		super.motion(interval, ground);
	}
	
	private Plan planning(Player player, GeometryGraph geometryGraph) {
		Location startLoc;
		Location goalLoc;
		startLoc = geometryGraph.getNearestLocation(getPosition());
		goalLoc = geometryGraph.getNearestLocation(player.getPosition());
		Plan aStarPlan = aStar.getPath(startLoc, goalLoc);
		if (aStarPlan != null) {
			return aStarPlan;	// Aスターアルゴリズムを使った計画
		}
		startLoc = new Location(new Point3d(getPosition().getVector3d()));
		goalLoc = new Location(new Point3d(player.getPosition().getVector3d()));
		return new Plan(startLoc, goalLoc);			// スタートとゴールを直線で結ぶ計画
	}
	
	private void startPlan(Plan plan) {
		if (plan == null) return;		// そもそもまだ一回も計画を立てていない
		Location nextLoc = plan.getNextLocation();
		if (nextLoc == null) return;	// 計画のゴールに辿り着いた
		// 計画上の次の通過点目指して移動速度と体の向きを変更
		Vector3d direction = new Vector3d(nextLoc.getCenter());
		if (direction.length() > 0.0) {
			direction.sub(getPosition().getVector3d());
			direction.normalize();
			direction.scale(ENEMY_SPEED);
			setVelocity(new Velocity3D(direction));		// 移動速度の設定
			direction.setY(0.0);
			direction.normalize();
			setDirection(direction);	// 体の向きの設定
		}
	}
	
	private boolean doPlan(Plan plan) {
		if (plan == null) return false;		// そもそもまだ一回も計画を立てていない
		if (plan.updateCurrentLocation(getPosition())) {
			Location loc = plan.getCurrentLocation();
			if (loc == null) return false;	// 計画のゴールに辿り着いた
			// 計画上の次の通過点目指して移動速度と体の向きを変更
			Vector3d direction = new Vector3d(loc.getCenter());
			if (direction.length() > 0.0) {
				direction.sub(getPosition().getVector3d());
				direction.normalize();
				direction.scale(ENEMY_SPEED);
				setVelocity(new Velocity3D(direction));		// 移動速度の設定
				direction.setY(0.0);
				direction.normalize();
				setDirection(direction);	// 体の向きの設定
			}
		}
		return true;
	}

	public EnemiesBullet shoot(Player player, EnemiesBulletModel enemiesBulletModel) {
		EnemiesBullet enemiesBullet = enemiesBulletModel.createEnemiesBullet();
		// プレイヤーをめがけて打つ
		Vector3d bulletVelocity = player.getPosition().sub(this.getPosition()).getVector3d();
		bulletVelocity.normalize();
		bulletVelocity.scale(BULLET_SPEED);
		enemiesBullet.setVelocity(new Velocity3D(bulletVelocity));
		enemiesBullet.setPosition(this.getPosition().add(-0.5, 0.0, 0.0));
		return enemiesBullet;
	}
}
