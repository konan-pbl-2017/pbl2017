package template.shooting2D;

import javax.vecmath.Vector2d;

import framework.game2D.Actor2D;
import framework.game2D.FlyingActor2D;
import framework.game2D.Sprite;

public class MyShipBullet extends Sprite {

	public MyShipBullet(String imageFile) {
		super(imageFile);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * widthとheightで表されるウィンドウサイズ内に存在するかを判定する
	 * 
	 * @param width
	 *            --- ウィンドウの幅
	 * @param height
	 *            --- ウィンドウの高さ
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
