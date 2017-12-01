package template.maze2D;

import java.math.BigDecimal;
import framework.game2D.Maze2D;

/**
 * 迷路ゲームのステージのクラス
 * @author T.Kuno
 *
 */
public class MazeStage extends Maze2D {
	// コンストラクタ
	public MazeStage(String blockImage, String tileImage) {
		super(blockImage, tileImage);
	}
	
	// 抽象メソッドの実装
	//　0:タイル　1:ブロック　
	@Override
	public int[][] createMap() {
		int[][] map = {
				{1,1,1,1,1,1,1,1,1,1,1},
				{1,0,0,0,0,0,0,0,0,0,1},
				{1,0,1,0,1,0,1,0,1,0,1},
				{1,0,0,0,0,0,0,0,0,0,1},
				{1,0,1,0,1,0,1,0,1,0,1},
				{1,0,0,0,0,0,0,0,0,0,1},
				{1,0,1,0,1,0,1,0,1,0,1},
				{1,0,0,0,0,0,0,0,0,0,1},
				{1,0,1,0,1,0,1,0,1,0,1},
				{1,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,1,1,1}
				
//				{1,1,1,1,1},
//				{1,0,0,0,1},
//				{1,0,1,0,1},
//				{1,0,0,0,1},
//				{1,1,1,1,1}
				
//				{1}

//				{1,0},
//				{1,1}
		};
		return map;
	}

	public boolean checkGridPoint(MazeSpritePlayer mazeSpritePlayer) {
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
