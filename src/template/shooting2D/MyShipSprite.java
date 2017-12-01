package template.shooting2D;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import framework.game2D.Actor2D;
import framework.game2D.Sprite;
import framework.game2D.Velocity2D;

public class MyShipSprite extends Sprite {
	// 弾の座標
	double bulletX, bulletY;
	// 弾幕の最大数
	public final int MAX_DANMAKU = 32;
	// 弾の発射時の自機からの位置
	private final int BULLET_DISTANCE = 1;
	private MyShipBullet myShipBullet;

	public MyShipSprite(String string) {
		super(string);
		super.setCollisionRadius(1.0);
	}

	// ////////////////////////////////////////////////////
	//
	// 自機の弾を発射するメソッド
	//
	// ///////////////////////////////////////////////////

	/**
	 * 弾幕が入ったArrayListを返す
	 * 
	 * @return -- 弾幕が入ったArrayList
	 */
	public ArrayList<MyShipBullet> shootDanmaku() {
		ArrayList<MyShipBullet> myShipBulletList = new ArrayList<MyShipBullet>();
		for (int i = 0; i < MAX_DANMAKU; i++) {
			myShipBullet = new MyShipBullet("data\\images\\myBullet.gif");

			bulletX = BULLET_DISTANCE
					* (Math.cos(i * (2 * Math.PI / MAX_DANMAKU)));
			bulletY = BULLET_DISTANCE
					* (Math.sin(i * (2 * Math.PI / MAX_DANMAKU)));

			// 弾の位置
			myShipBullet.setPosition(this.getPosition());
			// 弾の移動ベクトル
			myShipBullet.setVelocity(new Velocity2D(bulletX * 5, bulletY * 5));

			myShipBulletList.add(myShipBullet);
		}

		return myShipBulletList;
	}

	// ////////////////////////////////////////////////////
	//
	// プレイヤーがウィンドウ内にいるかどうかのメソッド
	//
	// ///////////////////////////////////////////////////

	/**
	 * widthとheightで決められたウィンドウの幅の中にプレイヤーがいるかどうかを返す
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean isInScreen(int width, int height) {
		if (this.getPosition().getX() < width / 2.0
				&& this.getPosition().getX() > -1.0 * width / 2.0) {
			if (this.getPosition().getY() < height / 2.0
					&& this.getPosition().getY() > -1.0 * height / 2.0) {
				return true;
			}
		}
		return false;
	}
}
