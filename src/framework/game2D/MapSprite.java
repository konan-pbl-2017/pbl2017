package framework.game2D;

/**
 * 迷路ゲーム専用のスプライトのクラス
 * @author T.Kuno
 *
 */
public class MapSprite extends Sprite {
	private int mapPositonX;
	private int mapPositonY;
	
	public MapSprite(String imageFile) {
		super(imageFile);
	}

	/**
	 * 2次元配列で表されるマップのX方向の添字を返す
	 * 
	 * @return
	 */
	public int getMapPositonX() {
		return mapPositonX;
	}

	/**
	 * 2次元配列で表されるマップのX方向の添字に新しくmapPositonXを設定する
	 * 
	 * @param mapPositonX
	 */
	public void setMapPositonX(int mapPositonX) {
		this.mapPositonX = mapPositonX;
	}

	/**
	 * 2次元配列で表されるマップのX方向の添字を返す
	 * 
	 * @return
	 */
	public int getMapPositonY() {
		return mapPositonY;
	}

	/**
	 * 2次元配列で表されるマップのX方向の添字に新しくmapPositonXを設定する
	 * 
	 * @param mapPositonY
	 */
	public void setMapPositonY(int mapPositonY) {
		this.mapPositonY = mapPositonY;
	}


	
}
