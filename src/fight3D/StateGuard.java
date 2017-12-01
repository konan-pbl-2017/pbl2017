package fight3D;
import framework.gameMain.Mode;
import framework.physics.Velocity3D;


public class StateGuard extends StateRepeatable {
	static final Velocity3D initialVelocity = new Velocity3D(0.0, 0.0, 0.0);
	
	public boolean canChange(State nextState, Mode mode) {
		
		if(nextState instanceof StateAttack) return false;
		if(nextState instanceof StateUpperAttack) return false;
		if(nextState instanceof StateJumpAttack) return false;
		if(nextState instanceof StateBend) return false;
		// if(nextState instanceof StateFlinch) return false;
		if(nextState instanceof StateJump) return false;
		if(nextState instanceof StateLeftMove) return false;
		if(nextState instanceof StateRightMove) return false;
			
		return true;
	}
	
	public Velocity3D getInitialVelocity() {
		return initialVelocity;
	}

}
