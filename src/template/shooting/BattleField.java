package template.shooting;

import java.awt.Container;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import framework.AI.GeometryGraph;
import framework.AI.IState;
import framework.AI.Location;
import framework.model3D.BaseObject3D;
import framework.model3D.CollisionResult;
import framework.model3D.GeometryCollector;
import framework.model3D.Object3D;
import framework.model3D.Placeable;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Velocity3D;

/**
 * プレイヤーと敵が闘う戦場
 * @author 新田直也
 *
 */
public class BattleField {
	private Universe universe;
	private Ground stage;
	private Player player;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<PlayersBullet> playersBullets = new ArrayList<PlayersBullet>();
	private ArrayList<EnemiesBullet> enemiesBullets = new ArrayList<EnemiesBullet>();
	private GeometryGraph geometryGraph = null;
	private PlayersBulletModel playersBulletModel;
	private EnemyModel enemyModel;
	private EnemiesBulletModel enemiesBulletModel;
	private boolean bPlayerHit = false;
	private int hitEnemyCount = 0;
	
	// 定数
	public static final int MAX_ENEMIES = 4;			// 敵の最大数

	public BattleField(Universe universe) {
		this.universe = universe;
		
		// 自分の弾のモデルを作成
		playersBulletModel = new PlayersBulletModel("data\\models\\bullet.3ds", null);
		
		// 敵のモデルを作成
		enemyModel = new EnemyModel("data\\Head4.wrl", "data\\pocha\\walk.wrl");
		
		// 敵の弾のモデルを作成
		enemiesBulletModel = new EnemiesBulletModel("data\\pocha\\bubble.wrl", null);
	}
	
	/**
	 * オブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void place(Node obj) {
		universe.place(obj);
	}

	/**
	 * オブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void place(Placeable obj) {
		if (obj instanceof Player) {
			player = (Player)obj;
		} else if (obj instanceof Ground) {
			stage = (Ground)obj;
			GeometryCollector v = new GeometryCollector();
			BaseObject3D baseObj = stage.getBody();
			if (baseObj instanceof Object3D) {
				if (obj instanceof Stage) {
					baseObj = ((Object3D)baseObj).getPart(((Stage)obj).getTerrainPart());
				}
				((Object3D)baseObj).accept(v);
				geometryGraph = new GeometryGraph(v.getGeometry());
			}
		}
		universe.place(obj);
	}
	
	/**
	 * 後で取り除けるようにオブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void placeDisplacable(Node obj) {
		universe.place(obj);
	}
	
	/**
	 * 後で取り除けるようにオブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void placeDisplacable(Placeable obj) {
		universe.place(obj);
	}
	
	/**
	 * 光源の追加
	 * @param light 追加する光源
	 */
	public void placeLight(Light light) {
		universe.place(light);
	}
	
	/**
	 * すべての構成要素を一斉に動かす
	 * @param interval 前回呼び出されたときからの経過時間（ミリ秒）
	 */
	public void motion(long interval) {
		// プレイヤーを動かす
		player.motion(interval, stage);		
		
		// すべての敵を動かすと同時に、プレイヤーと衝突判定し、更に敵の弾を発射する
		for (int n = 0; n < enemies.size(); n++) {
			Enemy enemy = enemies.get(n);				// 敵を一体取り出す
			enemy.motion(interval, stage, player, geometryGraph);		// 敵を動かす
			
			// 敵とプレイヤーの衝突判定
			CollisionResult collisionResult = PhysicsUtility.checkCollision(
					enemy.body, null, 
					player.body, null);			
			if (collisionResult != null) {
				// プレイヤーに当たった
				bPlayerHit = true;
			}
		}
		
		// すべてのプレイヤーの弾を動かすと同時に、敵と衝突判定
		for (int n = 0; n < playersBullets.size(); n++) {
			PlayersBullet bullet = playersBullets.get(n);	// プレイヤーの弾を一つ取り出す
			if (bullet.isAlive()) {
				bullet.motion(interval, stage);					// プレイヤーの弾を動かす
				
				for (int m = 0; m < enemies.size(); m++) {
					Enemy enemy = enemies.get(m);				// 敵を一体取り出す
					// プレイヤーの弾と敵の衝突判定
					CollisionResult collisionResult = PhysicsUtility.checkCollision(
							bullet.body, null, 
							enemy.body, null);
					if (collisionResult != null) {
						// 敵に当たった
						removeEnemy(enemy);						// 敵を消す
						removePlayersBullet(bullet);			// 当たった弾も消す
						n--;
						hitEnemyCount++;
						break;
					}
				}
			} else {
				removePlayersBullet(bullet);	// 弾を消す
				n--;
			}
		}
		
		// すべての敵の弾を動かすと同時に、プレイヤーと衝突判定
		for (int n = 0; n < enemiesBullets.size(); n++) {
			EnemiesBullet bullet = enemiesBullets.get(n);	// 敵の弾を一つ取り出す
			if (bullet.isAlive()) {			
				bullet.motion(interval, stage);					// 敵の弾を動かす
				
				// 敵の弾とプレイヤーの衝突判定
				CollisionResult collisionResult = PhysicsUtility.checkCollision(
						bullet.body, null, 
						player.body, null);			
				if (collisionResult != null) {
					// プレイヤーに当たった
					removeEnemiesBullet(bullet);				// 当たった弾を消す
					n--;
					bPlayerHit = true;
					break;
				}
			} else {
				// 弾が壁に当たったりして消えた場合
				removeEnemiesBullet(bullet);
				n--;
			}
		}
	}	
	
	/**
	 * プレイヤーの位置を取得する
	 * @return プレイヤーの位置ベクトル
	 */
	public Position3D getPlayerPosition() {
		return player.getPosition();
	}
	
	/**
	 * プレイヤーの位置を設定する
	 * @param pos プレイヤーの位置ベクトル
	 */
	public void setPlayerPosition(Position3D pos) {
		player.setPosition(pos);
	}
	
	/**
	 * プレイヤーを左右に回転させる
	 * @param ang 回転角度（ラジアン、正が左方向）
	 */
	public void playerTurn(double ang) {
		player.rotY(ang);
	}
	
	/**
	 * プレイヤーを現在向いている方向に歩かせる
	 * @param vel 歩く速度
	 */
	public void playerWalk(double vel) {
		Velocity3D curV = player.getVelocity();
		curV.setX(player.getDirection().getX() * vel);
		curV.setZ(player.getDirection().getZ() * vel);
		player.setVelocity(curV);		
	}
	
	/**
	 * プレイヤーを静止させる
	 */
	public void playerStop() {
		playerWalk(0.0);
	}
	
	/**
	 * プレイヤーをジャンプさせる
	 * @param vel ジャンプする際の初速度
	 */
	public void playerJump(double vel) {
		Velocity3D curV = player.getVelocity();
		curV.setY(vel);
		player.setVelocity(curV);				
	}
	
	/**
	 * プレイヤを取得する
	 * @return プレイヤ
	 */
	public Player getPlayer() {
		return player;
	}
		
	/**
	 * プレイヤが向いている方向を取得する
	 * @return プレイヤーが向いている方向ベクトル
	 */
	public Vector3d getPlayerDirection() {
		return player.getDirection();
	}
	
	/**
	 * プレイヤーを指定した方向に向かせる
	 * @param dir プレイヤーを向かせる方向ベクトル
	 */
	public void setPlayerDirection(Vector3d dir) {
		player.setDirection(dir);
	}
	
	/**
	 * プレイヤーの現在の移動速度を取得する
	 * @return プレイヤーの移動速度ベクトル
	 */
	public Velocity3D getPlayerVelocity() {
		return player.getVelocity();
	}
	
	/**
	 * プレイヤーの移動速度を設定する
	 * @param vel プレイヤーに設定する移動速度ベクトル
	 */
	public void setPlayerVelocity(Velocity3D vel) {
		player.setVelocity(vel);
	}
	
	/**
	 * プレイヤーが地面の上に乗っているかどうかを判定する
	 * @return true --- 地面の上に乗っている, false --- 宙に浮いている
	 */
	public boolean isPlayerOnGround() {
		return player.isOnGround();
	}
	
	/**
	 * プレイヤーに弾を撃たせる
	 */
	public void playerShoots() {
		PlayersBullet bullet = playersBulletModel.createPlayersBullet();
		Vector3d bulletVelocity = (Vector3d)player.getDirection().clone();
		bulletVelocity.normalize();
		bulletVelocity.scale(10.0);
		bullet.setVelocity(new Velocity3D(bulletVelocity));
		bullet.setPosition(player.getPosition().add(0.0, 1.0, 0.0));
		addPlayersBullet(bullet);
	}
	
	/**
	 * プレイヤーが攻撃を受けたか？
	 * @return true --- 攻撃を受けた, false --- 攻撃を受けていない
	 */
	public boolean isPlayerHit() {
		boolean hit = bPlayerHit;
		bPlayerHit = false;
		return hit;
	}
	
	/**
	 * 敵を発生させる
	 * @return 発生した敵のオブジェクト
	 */
	public Enemy createEnemy() {
		if (getEnemyCount() < MAX_ENEMIES) {
			Enemy newEnemy = enemyModel.createEnemy();
			ArrayList<IState> locations = geometryGraph.getStates();			
			Location startLoc;
			for (;;) {
				startLoc = (Location)locations.get((int)(Math.random() * locations.size()));
				if (startLoc.getNormal().dot(new Vector3d(0.0, 1.0, 0.0)) >= 0.95) break;
			}
			newEnemy.setPosition(new Position3D(new Vector3d(startLoc.getCenter())).add(0.0, 2.0, 0.0));
			addEnemy(newEnemy);
			return newEnemy;
		}
		return null;
	}
	
	/**
	 * 敵の総数を取得する
	 * @return 敵の総数
	 */
	public int getEnemyCount() {
		return enemies.size();
	}
	
	/**
	 * 指定した番号の敵を取得する
	 * @param n 敵の番号(0～)
	 * @return 敵のオブジェクト
	 */
	public Enemy getEnemy(int n) {
		return enemies.get(n);
	}

	/**
	 * 敵を消す
	 * @param enemy 消す敵
	 */
	public void removeEnemy(Enemy enemy) {
		enemies.remove(enemy);
		universe.displace(enemy);
	}
	
	/**
	 * 乱数で選んだ敵に弾を撃たせる
	 */
	public void enemyShoots() {
		if (enemies.size() > 0) {
			Enemy enemy = enemies.get((int)(Math.random() * (double)enemies.size()));
			EnemiesBullet enemiesBullet = enemy.shoot(player, enemiesBulletModel);
			addEnemiesBullet(enemiesBullet);		
		}
	}
	
	/**
	 * 倒した敵の数を取得する
	 * @return 新たに倒した敵の数
	 */
	public int getHitEnemyCount() {
		int hit = hitEnemyCount;
		hitEnemyCount = 0;
		return hit;
	}
	
	/**
	 * 敵を登場させる
	 * @param enemy　登場する敵
	 */
	private void addEnemy(Enemy enemy) {
		enemies.add(enemy);		
		universe.place(enemy);
	}
	
	/**
	 * プレイヤーの弾を発射する
	 * @param playersBullet 発射するプレイヤーの弾
	 */
	private void addPlayersBullet(PlayersBullet playersBullet) {
		playersBullets.add(playersBullet);
		universe.place(playersBullet);
	}
	
	/**
	 * 敵の弾を発射する
	 * @param enemy 発射する敵
	 */
	private void addEnemiesBullet(EnemiesBullet enemiesBullet) {
		enemiesBullets.add(enemiesBullet);		
		universe.place(enemiesBullet);
	}

	/**
	 * プレイヤーの弾を消す
	 * @param playersBullet 消す弾
	 */
	private void removePlayersBullet(PlayersBullet playersBullet) {
		playersBullets.remove(playersBullet);
		universe.displace(playersBullet);
	}

	/**
	 * 敵の弾を消す
	 * @param enemiesBullet 消す弾
	 */
	private void removeEnemiesBullet(EnemiesBullet enemiesBullet) {
		enemiesBullets.remove(enemiesBullet);
		universe.displace(enemiesBullet);
	}
}
