package fight3D;

import javax.vecmath.Vector3d;

import framework.gameMain.Mode;
import framework.gameMain.ModeFreeFall;
import framework.gameMain.ModeOnGround;
import framework.physics.PhysicsUtility;
import framework.physics.Velocity3D;


public class StateRightMove extends StateRepeatable {
	private Velocity3D initialVelocity = new Velocity3D(5.0, 0.0, 0.0);
	
	void setSpeed(double speed) {
		initialVelocity.setX(speed);
	}
	
	public boolean canChange(State nextState, Mode mode) {
		if(mode instanceof ModeFreeFall) {
			//落下中
			if(nextState instanceof StateBend) return false;
			if(nextState instanceof StateGuard) return false;
			if(nextState instanceof StateJump) return false;
		}
		return true;
	}
	
	public Velocity3D getInitialVelocity() {
		return initialVelocity;
	}

	public Velocity3D getVelocity(Velocity3D curVelocity, Mode mode) {
		if (mode instanceof ModeOnGround) {
			// 地面の向きに沿うように速度ベクトルの向きを変える
			// （地上を歩いているときはアニメーションなどの影響で速度が変わる可能性があるので、状態変化がなくても同じ速度を設定し続ける）
			ModeOnGround modeOnGround = (ModeOnGround)mode;
			Vector3d v = getInitialVelocity().getVector3d();
			double d = v.dot(modeOnGround.getNormal());
			v.scaleAdd(-d, modeOnGround.getNormal(), v);
			return new Velocity3D(v);
		}
		// 空中に滞在している場合は水平方向の速度成分だけ変更する（自由落下には影響を与えないように）
		if (counter == 0) {
			// 空中の場合、水平方向に加減速することはないので、速度を設定するのは初回のみでいい
			Vector3d v0 = curVelocity.getVector3d();
			Vector3d v1 = getInitialVelocity().getVector3d();
			v0.add(v1);
			double d0 = v0.dot(PhysicsUtility.horizon);	
			double d1 = v1.dot(PhysicsUtility.horizon);	
			v0.scaleAdd(d1 - d0, PhysicsUtility.horizon, v0);
			return new Velocity3D(v0);
		}
		return null;
	}
}
