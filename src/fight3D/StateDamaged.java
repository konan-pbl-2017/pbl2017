package fight3D;
import framework.gameMain.Mode;
import framework.physics.Velocity3D;


public class StateDamaged extends StateOnce {
	static final Velocity3D initialVelocity = new Velocity3D(0.0, 0.0, 0.0);

	@Override
	public boolean canChange(State nextState, Mode mode) {
		return false;
	}

	@Override
	public Velocity3D getInitialVelocity() {
		return  initialVelocity;
	}

}
