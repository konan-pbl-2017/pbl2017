package template.PRG2D;

import java.math.BigDecimal;
import framework.game2D.Map2D;

/**
 * 迷路ゲームのステージのクラス
 * @author T.Kuno
 *
 */
public class MapStage extends Map2D {
	// コンストラクタ
	public MapStage() {
		super(new String[]{
				"data\\RPG\\grass1.jpg",
				"data\\RPG\\grass2.jpg",
				"data\\RPG\\flower.jpg",
				"data\\RPG\\road1.jpg",
				"data\\RPG\\road2.jpg",
				"data\\RPG\\road3.jpg",
				"data\\RPG\\beach.jpg",
				"data\\RPG\\tree.jpg",		// ここから下は障害物
				"data\\RPG\\rock.jpg",
				"data\\RPG\\block.jpg",
				"data\\RPG\\sea1.jpg",
				"data\\RPG\\sea2.jpg"},
		7);
	}
	
	// 抽象メソッドの実装
	//　0～6:移動可能　7以上:障害物　
	@Override
	public int[][] createMap() {
		int[][] map = {
				{ 8, 8, 8, 8, 8, 8, 7, 7, 3, 7, 7, 7, 3, 8,10,10},
				{ 8, 8, 8, 8, 8, 8, 7, 7, 3, 7, 7, 7, 3, 8,10,10},
				{ 8, 8, 8, 7, 7, 7, 7, 7, 3, 3, 7, 7, 3, 8, 8,10},
				{ 8, 8, 7, 7, 7, 7, 7, 7, 7, 3, 7, 7, 3, 3, 6,10},
				{ 8, 7, 7, 7, 7, 1, 1, 0, 0, 3, 7, 7, 7, 3, 6,10},
				{ 8, 7, 7, 7, 1, 0, 0, 0, 1, 3, 0, 0, 7, 3, 6,10},
				{ 8, 8, 7, 7, 0, 0, 2, 0, 0, 3, 0, 1, 0, 3, 6,10},
				{ 8, 8, 7, 7, 7, 0, 1, 0, 0, 3, 3, 7, 0, 3, 6,10},
				{ 8, 8, 8, 7, 7, 0, 0, 1, 9, 0, 3, 0, 0, 3, 6,10},
				{ 8, 8, 8, 8, 7, 7, 0, 0, 0, 2, 3, 3, 3, 3, 6,10},
				{ 8, 8, 8, 8, 8, 7, 1, 0, 0, 0, 3, 0, 0, 0, 6,10},
				{ 3, 3, 3, 8, 8, 7, 7, 2, 0, 3, 3, 7, 0, 6, 6,10},
				{ 8, 8, 3, 8, 8, 8, 0, 0, 3, 3, 0, 6, 6, 6,10,10},
				{ 8, 8, 3, 3, 3, 3, 3, 3, 3, 1, 6, 6,10,10,10,11},
				{ 8, 8, 8, 8, 8, 8, 6, 6, 6, 6,10,10,10,11,11,11},
				{10,10,10,10,10,10,10,10,10,10,10,11,11,11,11,11}				
		};
		return map;
	}

	public boolean checkGridPoint(Player mazeSpritePlayer) {
		// 丸め誤差処理用変数の生成
		double mazeSpritePositionX = new BigDecimal(mazeSpritePlayer
				.getPosition().getX()).setScale(1, BigDecimal.ROUND_DOWN)
				.doubleValue();
		double mazeSpritePositionY = new BigDecimal(mazeSpritePlayer
				.getPosition().getY()).setScale(1, BigDecimal.ROUND_DOWN)
				.doubleValue();
		
		// ステージの構成オブジェクトの位置とプレイヤーの位置が同じかどうかっ判定する
		for (int i = 0; i < this.getStageObjectList().size(); i++) {
			if (
					mazeSpritePositionX == this.getStageObjectList().get(i).getPosition().getX()
					&& mazeSpritePositionY == this.getStageObjectList().get(i).getPosition().getY()
				){
				return true;
			}

		}
		return false;
	}
}
