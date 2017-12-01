package templete.action2D;

import javax.vecmath.Vector2d;

import framework.game2D.Ground2D;
import framework.game2D.OvergroundActor2D;
import framework.game2D.Velocity2D;

/**
 * “G‚ÌƒNƒ‰ƒX
 * 
 * @author H.Kotobuki
 * 
 */
public class Enemy extends OvergroundActor2D {
	private static final double ENEMY_SPEED = 5.0;

	@Override
	public String getAnimationFileName() {
		return null;
	}

	@Override
	public String getModelFileName() {
		return "data\\Head4.wrl";
	}
	
	public void motion(long interval, Ground2D ground, Player player) {
		Vector2d v = player.getPosition().getVector2d();
		v.sub(getPosition().getVector2d());
		v.normalize();
		v.scale(ENEMY_SPEED);
		setVelocity(new Velocity2D(v));
		super.motion(interval, ground);
	}
}
