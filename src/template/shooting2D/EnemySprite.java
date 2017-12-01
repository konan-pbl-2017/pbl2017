package template.shooting2D;

import java.util.ArrayList;

import framework.game2D.Sprite;
import framework.game2D.Velocity2D;

public class EnemySprite extends Sprite {
	// 衝突判定用のBoundingSphereの半径
	public double collisionRadius = 1.0;

	private int enemyHP = 100;

	// 弾幕の最大数
	private final int MAX_DANMAKU = 32;
	
	// 弾の発射時の敵からの位置
	private final int BULLET_DISTANCE = 1;

	// ゲームの表示範囲から得ることが出来る幅・高さ
	int rangeWidth = TemplateShooting2D.RANGE;
	int rangeHeight = TemplateShooting2D.RANGE;

	public EnemySprite(String imageFile) {
		super(imageFile);
	}

	// ////////////////////////////////////////////////////
	//
	// 敵機の衝突判定関連メソッド
	//
	// ///////////////////////////////////////////////////

	/**
	 * 衝突判定のBounding Sphere（境界球）をcollisionRadiusで設定する
	 * 
	 * @param collisionRadius
	 *            -- BoundingSphereの半径
	 */
	public void setCollisionRadius(double collisionRadius) {
		this.collisionRadius = collisionRadius;
	}

	/**
	 * 衝突判定のBounding Sphere（境界球）の半径を返す
	 * 
	 * @return　 BoundingSphereの半径
	 */
	public double getCollisionRadius() {
		return collisionRadius;
	}

	// ////////////////////////////////////////////////////
	//
	// 敵機の弾を発射するメソッド
	//
	// ///////////////////////////////////////////////////

	/**
	 * 弾幕が入ったArrayListを返す
	 * 
	 * @return -- 弾幕が入ったArrayList
	 */
	public ArrayList<EnemyBullet> shootDanmaku() {
		double bulletX, bulletY;
		
		ArrayList<EnemyBullet> enemyBulletList = new ArrayList<EnemyBullet>();
		for (int i = 0; i < MAX_DANMAKU; i++) {
			EnemyBullet enemyBullet = new EnemyBullet("data\\images\\enemyBullet.gif");

			bulletX = BULLET_DISTANCE * (Math.cos(i * (2 * Math.PI / MAX_DANMAKU)));
			bulletY = BULLET_DISTANCE * (Math.sin(i * (2 * Math.PI / MAX_DANMAKU)));

			// 弾の位置を設定
			enemyBullet.setPosition(this.getPosition());
			// 弾の移動ベクトルを設定する
			enemyBullet.setVelocity(new Velocity2D(bulletX * 5, bulletY * 5));

			enemyBulletList.add(enemyBullet);
		}

		return enemyBulletList;
	}

	// ////////////////////////////////////////////////////
	//
	// 敵がウィンドウ内にいるかどうかのメソッド
	//
	// ///////////////////////////////////////////////////

	public void motion(long interval) {
		Velocity2D vel = this.getVelocity();

		switch (insideX()) {
		case -1:
			vel.setX(Math.abs(this.getVelocity().getX()));
			break;
		case 1:
			vel.setX(Math.abs(this.getVelocity().getX()) * -1.0);
			break;
		}

		switch (insideY()) {
		case -1:
			vel.setY(Math.abs(this.getVelocity().getY()));
			break;
		case 1:
			vel.setY(Math.abs(this.getVelocity().getY()) * -1.0);
			break;
		}
		
		setVelocity(vel);
		super.motion(interval);

	}

	/**
	 * 画面をX方向に出ていないか?
	 * 
	 * @return -1: Xの負の方向に出ている, 0: X方向に出ていない, 1: Xの正の方向に出ている
	 */
	private int insideX() {
		if (this.getPosition().getX() > rangeWidth / 2.0) {
			return 1;		
		} else if (this.getPosition().getX() < -rangeWidth / 2.0) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 画面をY方向に出ていないか?
	 * 
	 * @return -1: Yの負の方向に出ている, 0: Y方向に出ていない, 1: Yの正の方向に出ている
	 */
	private int insideY() {
		if (this.getPosition().getY() > rangeHeight / 2.0) {
			return 1;
		} else if (this.getPosition().getY() < -rangeHeight / 2.0) {
			return -1;
		} else {
			return 0;
		}
	}

	// ////////////////////////////////////////////////////
	//
	// 敵機のHP関連メソッド
	//
	// ///////////////////////////////////////////////////
	public boolean shootDown() {
		if (enemyHP <= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void addEnemyHP(int value) {
		enemyHP += value;
	}

	public int getEnemyHP() {
		return enemyHP;
	}

	public void setEnemyHP(int enemyHP) {
		this.enemyHP = enemyHP;
	}

}
