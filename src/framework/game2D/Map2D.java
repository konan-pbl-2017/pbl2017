package framework.game2D;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector2d;

import framework.model3D.BaseObject3D;
import framework.model3D.Placeable;

public abstract class Map2D implements Placeable {

	protected int[][] map;
	protected TransformGroup transformGroup;
	protected int mapWidth;
	protected int mapHeight;
	protected int blockId;
	private ArrayList<MapSprite> stageObjectList = new ArrayList<MapSprite>();

	/**
	 * MazeGameで使用するステージを返す抽象メソッド。
	 * オブジェクトの設置の指定は0:タイル、1:ブロックで設置する。
	 * @return	2次元配列で指定したステージの配列
	 */
	public abstract int[][] createMap();
	
	/**
	 * @param spriteImages マップで使用するスプライトのイメージファイルの配列
	 * @param blockId この値以上のidを持つスプライトをブロック（障害物）とみなす
	 */
	public Map2D(String[] spriteImages, int blockId) {
		transformGroup = new TransformGroup();
		this.blockId = blockId;
		
		// マップの生成
		map = createMap();
		
		// マップを生成する際に配置基準点となる座標点の変数
		double pointX = 0.0;
		double pointY = 0.0;
		
		// 2次元配列で表されたマップからタイル、ブロックのインスタンスを作り、ステージを構成するリストに追加していく。
		// 配置位置は配置基準点から幅2.0、高さ2.0のオブジェクトを生成する。
		// 例えば(2.0 2.0）の位置に生成する場合は(2.0,2.0)(4.0,2.0)(2.0,4.0)(4.0,4.0)のこの4点がブロックの角となる
		//　これを基準に衝突判定などゲームにおける処理を行っている。
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				
				if (map[map.length - 1 - j][i] < blockId) {
					Tile tile = new Tile(spriteImages[map[map.length - 1 - j][i]]);
					tile.setMapPositonX(i);
					tile.setMapPositonY(map.length - 1 - j);					
					tile.setPosition(pointX + i * 2.0, pointY + j * 2.0, -1.0);
					tile.setCollisionRadius(1.0);
					stageObjectList.add(tile);
					transformGroup.addChild(tile.getTransformGroupToPlace());
				} else {
					Block block = new Block(spriteImages[map[map.length - 1 - j][i]]);
					block.setMapPositonX(i);
					block.setMapPositonY(map.length - 1 - j);					
					block.setPosition(pointX + i * 2.0, pointY + j * 2.0, -1.0);
					block.setCollisionRadius(1.0);
					stageObjectList.add(block);
					transformGroup.addChild(block.getTransformGroupToPlace());
				}
			}
		}
		// マップの中心にカメラが中止するようにマップの幅・高さを計算する。
		mapWidth = map.length * 2;
		mapHeight = map.length * 2;		
	}

	@Override
	public BaseObject3D getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransformGroup getTransformGroupToPlace() {
		// TODO Auto-generated method stub
		return transformGroup;
	}

	/**
	 * マップの幅を返す
	 * 
	 * @return マップの幅
	 */
	public int getMapWidth() {
		return mapWidth;
	}

	/**
	 * マップの幅をmapWidthで設定する
	 * 
	 * @param mapWidth
	 */
	protected void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}

	/**
	 * マップの高さを返す
	 * 
	 * @return マップの高さ
	 */
	public int getMapHeight() {
		return mapHeight;
	}

	/**
	 * マップの高さをmapHeightで設定する
	 * 
	 * @param mapHeight
	 */
	protected void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}

	/**
	 * MazeGameのステージを構成するオブジェクトが入ったリストを返す
	 * 
	 * @return stageObjectList --- タイルやブロックのインスタンスが入ったリスト
	 */
	public ArrayList<MapSprite> getStageObjectList() {
		return stageObjectList;
	}

	/**
	 * MazeGameのステージを構成するオブジェクトが入ったリストをstageObjectListで設定する
	 * 
	 * @param stageObjectList
	 */
	public void setStageObjectList(ArrayList<MapSprite> stageObjectList) {
		this.stageObjectList = stageObjectList;
	}

	public MapCollisionResult checkCollision(Sprite mazeSprite) {
			MapCollisionResult collisionResult = new MapCollisionResult();
			
			// 衝突判定時のy方向の深さベクトルの計算用の変数
			Vector2d vecX1;
			Vector2d vecX2;
			Vector2d vecY1;
			Vector2d vecY2;
			
			// 衝突判定があった時にスプライトがブロックにめり込んだ深さを表す2次元ベクトル
			Vector2d collisionVectorX = new Vector2d();
			Vector2d collisionVectorY = new Vector2d();
			
			
	
			
			// CollisitonX,CollisitionYの誤差修正用変数（小数第2位から切り捨て）
			double corectionX;
			double corectionY;
			
			// スプライトの座標値をBigDecimalで丸め誤差処理（小数点第3位から切り捨て）を行い、double型の変数に変換し、代入する。
			// ここで宣言する変数の値はスプライトの角の4点の座標が代入される。		
			double spriteMinX = new BigDecimal(mazeSprite.getPosition().getX()).setScale(2, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
			double spriteMaxX = new BigDecimal(mazeSprite.getPosition().getX() + 2.0).setScale(2, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
			double spriteMinY = new BigDecimal(mazeSprite.getPosition().getY()).setScale(2, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
			double spriteMaxY = new BigDecimal(mazeSprite.getPosition().getY() + 2.0).setScale(2, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
			
	//		double spriteMinX = mazeSprite.getPosition().getX();
	//		double spriteMaxX = mazeSprite.getPosition().getX() + 2.0;
	//		double spriteMinY = mazeSprite.getPosition().getY();
	//		double spriteMaxY = mazeSprite.getPosition().getY() + 2.0;
			
			// MageGameを構成するオブジェクトとの衝突判定
			for(int i = 0; i < getStageObjectList().size(); i++){
				if (getStageObjectList().get(i) instanceof Block) {
					// 丸め誤差処理。小数第2位を切り捨て。
					double blockMinX = new BigDecimal(getStageObjectList().get(i).getPosition().getX()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double blockMaxX = new BigDecimal(getStageObjectList().get(i).getPosition().getX() + 2.0).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double blockMinY = new BigDecimal(getStageObjectList().get(i).getPosition().getY()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					double blockMaxY = new BigDecimal(getStageObjectList().get(i).getPosition().getY() + 2.0).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
	
					
					
					
					///////////////////////////////////////////////////////////////////////////////////////
					//
					//	ブロックの2つ角がスプライトの2つの角と重なる場合（数字は基準）
					//
					//	3-----------4
					//	|			|
					//	|			|
					//	|			|
					//	|			|
					//	|			|
					//	2-----------1
					//
					///////////////////////////////////////////////////////////////////////////////////////
					
					// ここから衝突判定の始まり（if文の始まり）
					if((spriteMinX <= blockMinX && spriteMaxX >= blockMaxX)
						|| (spriteMinX >= blockMinX && spriteMaxX <= blockMaxX)){
						if(spriteMaxY > blockMinY && spriteMaxY < blockMaxY){
	//						System.out.println("基準点1から辺同士（スプライトの進行方向が上向き）による衝突判定を行ったぞ！");
							
							collisionResult.setCheckColision(true);
							
							// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
							vecX1 = new Vector2d(blockMinX, blockMinY);		//	2
							vecX2 = new Vector2d(blockMaxX, blockMinY);		//	1
							
							// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
							vecY1 = new Vector2d(spriteMaxX, spriteMaxY);	//	スプライト座標
							vecY2 = new Vector2d(blockMaxX, blockMinY);		//	1
		
							collisionVectorX.sub(vecX1, vecX2);
							collisionVectorY.sub(vecY1, vecY2);
							
							// 誤差修正（値を小数第2以降切り捨て）
							corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							
							collisionVectorX.set(corectionX, 0.0);
							collisionVectorY.set(0.0, corectionY);
							
							collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
							break;
						}
						else if(spriteMinY < blockMaxY && spriteMinY > blockMinY){
	//						System.out.println("基準点3から辺同士（スプライトの進行方向が下向き）による衝突判定を行ったぞ！");
	
							collisionResult.setCheckColision(true);
							
							// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
							vecX1 = new Vector2d(blockMaxX, blockMaxY);		//	4
							vecX2 = new Vector2d(blockMinX, blockMaxY);		//	3
							
							// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
							vecY1 = new Vector2d(spriteMinX, spriteMinY);	//	スプライト座標	
							vecY2 = new Vector2d(blockMinX, blockMaxY);		//	3
		
							collisionVectorX.sub(vecX1, vecX2);
							collisionVectorY.sub(vecY1, vecY2);
							
							// 誤差修正（値を小数第2以降切り捨て）
							corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							
							collisionVectorX.set(corectionX, 0.0);
							collisionVectorY.set(0.0, corectionY);
							
							collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
	
							break;
						}
					}
					else if((spriteMinY <= blockMinY && spriteMaxY >= blockMaxY)
							|| (spriteMinY >= blockMinY && spriteMaxY <= blockMaxY)){
						if(spriteMaxX > blockMinX && spriteMaxX < blockMaxX){
	//						System.out.println("基準点2から辺同士（スプライトの進行方向が右向き）による衝突判定を行ったぞ！");
	
							collisionResult.setCheckColision(true);
							
							// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
							vecX1 = new Vector2d(spriteMaxX, spriteMinY);		
							vecX2 = new Vector2d(blockMinX, blockMinY);		//	2
							
							// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
							vecY1 = new Vector2d(blockMinX, blockMaxY);		//	3
							vecY2 = new Vector2d(blockMinX, blockMinY);		//	2
		
							collisionVectorX.sub(vecX1, vecX2);
							collisionVectorY.sub(vecY1, vecY2);
							
							// 誤差修正（値を小数第2以降切り捨て）
							corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							
							collisionVectorX.set(corectionX, 0.0);
							collisionVectorY.set(0.0, corectionY);
							
							collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
	
							break;
						}
						else if(spriteMinX < blockMaxX && spriteMinX > blockMinX){
	//						System.out.println("基準点4から辺同士（スプライトの進行方向が左向き）による衝突判定を行ったぞ！");
							
							collisionResult.setCheckColision(true);
							
							// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
							vecX1 = new Vector2d(spriteMinX, spriteMaxY);		
							vecX2 = new Vector2d(blockMaxX, blockMaxY);		//	4
							
							// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
							vecY1 = new Vector2d(blockMaxX, blockMinY);		//	1
							vecY2 = new Vector2d(blockMaxX, blockMaxY);		//	4
		
							collisionVectorX.sub(vecX1, vecX2);
							collisionVectorY.sub(vecY1, vecY2);
							
							// 誤差修正（値を小数第2以降切り捨て）
							corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
							
							collisionVectorX.set(corectionX, 0.0);
							collisionVectorY.set(0.0, corectionY);
							
							collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
	
							break;
						}
					}
	
	
					
					//////////////////////////////////////////////////////////////////////////////////////
					//
					//	1.衝突した際のイメージ。	Oは衝突判定時の深さを表すベクトルを計算をする時の基準として考えています。
					//
					//	+---------------+
					//	|	ブロック		|	
					//	|				|
					//	|		+-------+---+
					//	|		|		|	|
					//	|		|		|	|
					//	+-------+-------O	|	
					//			|			|
					//			|　プレイヤー	|
					//			+-----------+
					//				
					/////////////////////////////////////////////////////////////////////////////////////
					
					else if (spriteMinX > blockMinX && spriteMinX < blockMaxX && spriteMaxY > blockMinY && spriteMaxY < blockMaxY) {
//						System.out.println("めり込みパターン①（ブロックとスプライト重なりがブロックの右下）による衝突判定を行ったぞ！");
	
						collisionResult.setCheckColision(true);
						
						// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
						vecX1 = new Vector2d(spriteMinX, blockMinY);
						vecX2 = new Vector2d(blockMaxX, blockMinY);
						
						// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
						vecY1 = new Vector2d(blockMaxX, spriteMaxY);
						vecY2 = new Vector2d(blockMaxX, blockMinY);
	
						collisionVectorX.sub(vecX1, vecX2);
						collisionVectorY.sub(vecY1, vecY2);
						
						// 誤差修正（値を小数第2以降切り捨て）
						corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						collisionVectorX.set(corectionX, 0.0);
						collisionVectorY.set(0.0, corectionY);
						collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
						
	//					System.out.println("①ブロック" + i + "番目と当たったよ！");
	//					System.out.println("①ブロック" + i + "番目のX座標は" + mazeGround.getStageObjectList().get(i).getPosition().getX()+", Y座標は"+mazeGround.getStageObjectList().get(i).getPosition().getY());
						break;
					}
					
					//////////////////////////////////////////////////////////////////////////////////////
					//
					//	2.衝突した際のイメージ。	Oは衝突判定時の深さを表すベクトルを計算をする時の基準として考えています。
					//
					//		+-----------+
					//		|	ブロック	|	
					//		|			|
					//	+---+-------+	|
					//	|	|		|	|
					//	|	|		|	|
					//	|	|		|	|
					//	|	O-------+---+
					//	|			|				
					//	|　 プレイヤー	|			
					//	+-----------+
					//
					//////////////////////////////////////////////////////////////////////////////////////
					
					else if (spriteMaxX > blockMinX && spriteMaxX < blockMaxX && spriteMaxY > blockMinY && spriteMaxY < blockMaxY) {
//						System.out.println("めり込みパターン②（ブロックとスプライト重なりがブロックの左下）による衝突判定を行ったぞ！");
						collisionResult.setCheckColision(true);
						
						// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
						vecX1 = new Vector2d(spriteMaxX, blockMinY);
						vecX2 = new Vector2d(blockMinX, blockMinY);
						
						// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
						vecY1 = new Vector2d(blockMinX, spriteMaxY);
						vecY2 = new Vector2d(blockMinX, blockMinY);
	
						collisionVectorX.sub(vecX1, vecX2);
						collisionVectorY.sub(vecY1, vecY2);
	
						
						// 誤差修正（値を小数第2以降切り捨て）
						corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
						corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
						
						collisionVectorX.set(corectionX, 0.0);
						collisionVectorY.set(0.0, corectionY);
						
						collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
	//					System.out.println("②ブロック" + i + "番目と当たったよ！");
	//					System.out.println("②ブロック" + i + "番目のX座標は" + mazeGround.getStageObjectList().get(i).getPosition().getX()+", Y座標は"+mazeGround.getStageObjectList().get(i).getPosition().getY());
						break;
					}
					
	
		
					/////////////////////////////////////////////////////////////////////////////////
					//
					//	3.衝突した際のイメージ。	Oはベクトルの計算時の基準として考えています。
					//
					//	+---------------+
					//	|	プレイヤー		|	
					//	|				|
					//	|		O-------+---+
					//	|		|		|	|
					//	|		|		|	|
					//	+-------+-------+	|	
					//			|			|
					//			|	ブロック	|
					//			+-----------+
					//				
					/////////////////////////////////////////////////////////////////////////////////
	
					
					else if (spriteMaxX > blockMinX && spriteMaxX < blockMaxX && spriteMinY > blockMinY && spriteMinY < blockMaxY) {
//						System.out.println("めり込みパターン③（ブロックとスプライト重なりがブロックの左上）による衝突判定を行ったぞ！");
						collisionResult.setCheckColision(true);
						
						// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
						vecX1 = new Vector2d(spriteMaxX, blockMaxY);
						vecX2 = new Vector2d(blockMinX, blockMaxY);
						
						// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
						vecY1 = new Vector2d(blockMinX, spriteMinY);
						vecY2 = new Vector2d(blockMinX, blockMaxY);
	
						collisionVectorX.sub(vecX1, vecX2);
						collisionVectorY.sub(vecY1, vecY2);
						
						// 誤差修正（値を小数第2以降切り捨て）
						corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						
						collisionVectorX.set(corectionX, 0.0);
						collisionVectorY.set(0.0, corectionY);
						
						collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
						
	//					System.out.println("③ブロック" + i + "番目と当たったよ！");
	//					System.out.println("③ブロック" + i + "番目のX座標は" + mazeGround.getStageObjectList().get(i).getPosition().getX()+", Y座標は"+mazeGround.getStageObjectList().get(i).getPosition().getY());
						break;
					}
	
	
	
					/////////////////////////////////////////////////////////////////////////////////
					//
					//	4.衝突した際のイメージ。	Oはベクトルの計算時の基準として考えています。
					//
					//		+-----------+
					//		|　プレイヤー	|	
					//		|			|
					//	+---+-------O	|
					//	|	|		|	|
					//	|	|		|	|
					//	|	|		|	|
					//	|	+-------+---+
					//	|			|				
					//	|　ブロック		|			
					//	+-----------+
					//
					/////////////////////////////////////////////////////////////////////////////////
	
					else if (spriteMinX > blockMinX && spriteMinX < blockMaxX && spriteMinY > blockMinY && spriteMinY < blockMaxY) {
//						System.out.println("めり込みパターン④（ブロックとスプライト重なりがブロックの右上）による衝突判定を行ったぞ！");
						collisionResult.setCheckColision(true);
						
						// 衝突判定時のx方向の深さベクトルcollisionVectorXの計算
						vecX1 = new Vector2d(spriteMinX, blockMaxY);
						vecX2 = new Vector2d(blockMaxX, blockMaxY);
						
						// 衝突判定時のy方向の深さベクトルcollisionVectorYの計算
						vecY1 = new Vector2d(blockMaxX, spriteMinY);
						vecY2 = new Vector2d(blockMaxX, blockMaxY);
	
						collisionVectorX.sub(vecX1, vecX2);
						collisionVectorY.sub(vecY1, vecY2);
						
						// 誤差修正（値を小数第2以降切り捨て）
						corectionX = new BigDecimal(collisionVectorX.getX()).setScale(2, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
						corectionY = new BigDecimal(collisionVectorY.getY()).setScale(2, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
						
						collisionVectorX.set(corectionX, 0.0);
						collisionVectorY.set(0.0, corectionY);
						
						collisionResult.setColisionBackVelocity(collisionBackVelocity(collisionVectorX,collisionVectorY,mazeSprite.getVelocity()));
						
	//					System.out.println("④ブロック" + i + "番目と当たったよ！");
	//					System.out.println("④ブロック" + i + "番目のX座標は" + mazeGround.getStageObjectList().get(i).getPosition().getX()+", Y座標は"+mazeGround.getStageObjectList().get(i).getPosition().getY());
						break;
					}
				}
			}
			return collisionResult;
		}

	/**
	 * 衝突してきた際に生じる、逆の速度backVelocityを返す
	 * 
	 * @param collisionVectorX
	 *            衝突判定した時の深さを表すx方向ベクトル
	 * @param collisionVectorY
	 *            衝突判定した時の深さを表すy方向ベクトル
	 * @param objectVelocity
	 *            オブジェクトの現在の速度
	 *            
	 * @return 衝突してきたオブジェクトに対する速度ベクトル
	 */
	public Velocity2D collisionBackVelocity(Vector2d collisionVectorX, Vector2d collisionVectorY, Velocity2D objectVelocity) {
		Velocity2D collisionBackVel = new Velocity2D();
		
		if (objectVelocity.getY() >= 0) {
			if (collisionVectorX.length() > collisionVectorY.length()) {
				if (collisionVectorX.getX() > 0 && collisionVectorY.getY() > 0) {
					collisionBackVel.set(0.0, -1.0 * collisionVectorY.length());
					return collisionBackVel;
				} 
				else if (collisionVectorX.getX() > 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(-1.0 * collisionVectorY.length(), 0.0);
					return collisionBackVel;
				} 
				else if (collisionVectorX.getX() < 0 && collisionVectorY.getY() > 0) {
					collisionBackVel.set(0.0, -1.0 * collisionVectorY.length());
					return collisionBackVel;
				} 
				else if (collisionVectorX.getX() < 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(-1.0 * collisionVectorY.length(), 0.0);
					return collisionBackVel;
				}
			} 
			else if (collisionVectorX.length() < collisionVectorY.length()) {
				if (collisionVectorX.getX() > 0 && collisionVectorY.getY() > 0) {
					collisionBackVel.set(-1.0 * collisionVectorX.length(), 0.0);
					return collisionBackVel;
				}
				else if (collisionVectorX.getX() > 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(-1.0 * collisionVectorX.length(), 0.0);
					return collisionBackVel;
				}
				else if (collisionVectorX.getX() < 0 && collisionVectorY.getY() > 0) {
					collisionBackVel.set(collisionVectorX.length(), 0.0);
					return collisionBackVel;
				}
				else if (collisionVectorX.getX() < 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(1.0 * collisionVectorX.length(), 0.0);
					return collisionBackVel;
				}
			}
			collisionBackVel.set(-1.0	* collisionVectorX.getX(), 1.0	* objectVelocity.getY());
			return collisionBackVel;
		}
	
		else if (objectVelocity.getY() <= 0) {
			if (collisionVectorX.length() > collisionVectorY.length()) {
				if (collisionVectorX.getX() > 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(0.0, 1.0 * collisionVectorY.length());
					return collisionBackVel;
				} 
				else if (collisionVectorX.getX() < 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(0.0, 1.0 * collisionVectorY.length());
					return collisionBackVel;
				}
			} 
			else if (collisionVectorX.length() < collisionVectorY.length()) {
				if (collisionVectorX.getX() > 0 && collisionVectorY.getY() > 0) {
					collisionBackVel.set(-1.0 * collisionVectorX.length(), 0.0);
					return collisionBackVel;
				} 
				else if (collisionVectorX.getX() > 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(-1.0 * collisionVectorX.length(), 0.0);
					return collisionBackVel;
				} 
				else if (collisionVectorX.getX() < 0 && collisionVectorY.getY() > 0) {
					collisionBackVel.set(1.0 * collisionVectorX.length(), 0.0);
					return collisionBackVel;
				}
				else if (collisionVectorX.getX() < 0 && collisionVectorY.getY() < 0) {
					collisionBackVel.set(1.0 * collisionVectorX.length(), 0.0);
					return collisionBackVel;
				}
			}
			collisionBackVel.set(-1.0 * objectVelocity.getX(), -1.0	* collisionVectorY.length());
			return collisionBackVel;
		}
	
		collisionBackVel.set(-1.0 * objectVelocity.getX(), -1.0	* objectVelocity.getY());
		return collisionBackVel;
	}

}
