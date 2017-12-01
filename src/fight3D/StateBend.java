package fight3D;
import framework.gameMain.Mode;
import framework.gameMain.ModeOnGround;
import framework.physics.Velocity3D;

//ÇµÇ·Ç™Çﬁ
public class StateBend extends StateRepeatable {
	static final Velocity3D initialVelocity = new Velocity3D(0.0, 0.0, 0.0);
	
	public boolean canChange(State nextState, Mode mode) {
		
		//í èÌ
		if(mode instanceof ModeOnGround) {
			if(nextState instanceof StateAttack) return false;
			if(nextState instanceof StateUpperAttack) return false;
			if(nextState instanceof StateJumpAttack) return false;
			if(nextState instanceof StateGuard) return false;
			if(nextState instanceof StateJump) return false;
			if(nextState instanceof StateLeftMove) return false;
			if(nextState instanceof StateRightMove) return false;
		}
		
		//óéâ∫íÜ
		return true;
	}
	
	public Velocity3D getInitialVelocity() {
		return initialVelocity;
	}

}
