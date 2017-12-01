package template.music2D;

import framework.game2D.Sprite;

public class Target extends Sprite {

	public Target(String string) {
		super(string);
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
	public boolean doesScrrenOut(int width, int height) {
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
