package fight3D;
import framework.gameMain.Mode;
import framework.gameMain.ModeFreeFall;
import framework.gameMain.ModeOnGround;
import framework.physics.Velocity3D;


public class StateJump extends StateOnce {
	private Velocity3D initialVelocity = new Velocity3D(0.0, 10.0, 0.0);
	
	void setSpeed(double speed) {
		initialVelocity.setY(speed);
	}

	public boolean canChange(State nextState, Mode mode) {
		if(mode instanceof ModeOnGround) {
			if(nextState instanceof StateNormalRight) return true;
			else if(nextState instanceof StateNormalLeft) return true;
		}
		if(mode instanceof ModeFreeFall) {
			//落下中
			if(nextState instanceof StateJumpAttack) return true;
			if(nextState instanceof StateUpperAttack) return true;	// ジャンプした直後は対空攻撃ができる（操作性上の理由）
			if(nextState instanceof StateFlinch) return true;
			if(nextState instanceof StateLeftMove) return true;
			if(nextState instanceof StateRightMove) return true;
			if(nextState instanceof StateGuard) return true;
			if(nextState instanceof StateNormalRight) return true;
			if(nextState instanceof StateNormalLeft) return true;
		}
		return false;
	}
	
	public Velocity3D getInitialVelocity() {
		return initialVelocity;
	}
}
