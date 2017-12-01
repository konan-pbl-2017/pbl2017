package fight3D;
import framework.gameMain.Mode;
import framework.physics.Velocity3D;


public class StateUpperAttack extends StateOnce {
	static final Velocity3D initialVelocity = new Velocity3D(0.0, 0.0, 0.0);

	@Override
	public boolean canChange(State nextState, Mode mode) {
		if (nextState instanceof StateAttack) return false;
		if (nextState instanceof StateUpperAttack) return false;
		if (nextState instanceof StateJumpAttack) return false;
		if (nextState instanceof StateNormalLeft) return false;
		if (nextState instanceof StateNormalRight) return false;
		
		if(nextState instanceof StateFlinch) return true;
		if(nextState instanceof StateDamaged) return true;
		
		return false;
	}

	@Override
	public Velocity3D getInitialVelocity() {
		// ジャンプ開始直後は対空攻撃ができるので、その場合はジャンプを中断する（落下させる）
		return  initialVelocity;
	}

}
