package fight3D;
import framework.gameMain.Mode;
import framework.physics.Velocity3D;


public class StateJumpAttack extends StateOnce {

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
		// TODO Auto-generated method stub
		return null;
	}

}
