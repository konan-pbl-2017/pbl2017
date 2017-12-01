package framework.game2D;


/**
 * 迷路ゲームでの衝突判定による返り値をもつクラス
 * 
 * @author T.Kuno
 * 
 */
public class MapCollisionResult {
	private boolean checkColision = false;
	private Velocity2D colisionBackVelocity = new Velocity2D();

	
	/**
	 * 衝突があったかどうかを返す
	 * 
	 * @return true --- 衝突があった false --- 衝突がなかった
	 */
	public boolean isCheckColision() {
		return checkColision;
	}

	/**
	 * 衝突があったとき、その結果を新しくcheckColisionで設定する
	 * 
	 * @param checkColision
	 */
	public void setCheckColision(boolean checkColision) {
		this.checkColision = checkColision;
	}

	/**
	 * 衝突があったときに元の位置に戻す速度を返す
	 * 
	 * @return
	 */
	public Velocity2D getColisionBackVelocity() {
		return colisionBackVelocity;
	}

	/**
	 * 衝突があったときに元の位置に戻す速度を新しくcolisionBackVelocityで設定する
	 * 
	 * @param colisionBackVelocity
	 */
	public void setColisionBackVelocity(Velocity2D colisionBackVelocity) {
		this.colisionBackVelocity = colisionBackVelocity;
	}

}
