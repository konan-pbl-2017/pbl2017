package fight3D;
import framework.gameMain.Mode;
import framework.physics.Velocity3D;

//çUåÇ
public class StateAttack extends StateOnce {
	static final Velocity3D initialVelocity = new Velocity3D(0.0, 0.0, 0.0);
	
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
	
	public Velocity3D getInitialVelocity() {
		return  initialVelocity;
	}
}
