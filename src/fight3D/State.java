package fight3D;
import framework.gameMain.Mode;
import framework.physics.Velocity3D;


public abstract class State {
	protected long counter = 0; // この状態に切り替わってから何ループ目か？
	
	public void init() {
		counter = 0;
	}
	
	public void countUp() {
		counter++;
	}
	
	public long getCounter() {
		return counter;
	}
	
	/**
	 * 速度を返す
	 * @param curVelocity 現在の速度
	 * @param mode 現在のモード
	 * @return 次の瞬間の速度
	 */
	public Velocity3D getVelocity(Velocity3D curVelocity, Mode mode) {
		if (counter == 0) {
			// 最初の瞬間だけ初速度を返す
			return getInitialVelocity();
		}
		return null;
	}
	
	/**
	 * 自分から次の状態に遷移可能か否かを返す
	 * @param nextState 次の状態
	 * @param mode 現在のモード（空中に滞在しているか地上にいるか）
	 * @return true --- 遷移可能, false --- 遷移不可能
	 */
	public abstract boolean canChange(State nextState, Mode mode);
	
	
	/**
	 * 反復（継続）可能な状態か1回限りで元に戻る状態か
	 * @return true --- 反復（継続）可能な状態, false --- 1回限りで元に戻る状態
	 */
	public abstract boolean isRepeatable();
	
	/**
	 * 初速度を返す
	 * @return 初速度
	 */
	public abstract Velocity3D getInitialVelocity();

}
