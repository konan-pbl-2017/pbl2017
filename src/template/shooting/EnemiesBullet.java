package template.shooting;

import framework.AI.GeometryGraph;
import framework.animation.Animation3D;
import framework.gameMain.Actor;
import framework.model3D.CollisionResult;
import framework.model3D.Object3D;
import framework.physics.Force3D;
import framework.physics.Ground;

/**
 * ìGÇÃíe
 * @author êVìcíºñÁ
 *
 */
public class EnemiesBullet extends Actor {
	private boolean bAlive = true;
	private long left = 0;
	
	// íËêî
	public static final long LIFE_TIME = 2500L;		// íeÇÃéıñΩ
	
	public EnemiesBullet(Object3D body, Animation3D animation) {
		super(body, animation);
		left = LIFE_TIME;
	}
	
	public void motion(long interval, Ground ground) {
		left -= interval;
		if (left < 0L) {
			bAlive = false;
		}
		super.motion(interval, ground);
	}

	@Override
	public void onEndFall() {
	}

	@Override
	public void onIntersect(CollisionResult normal, long interval) {
		bAlive = false;
	}

	@Override
	public void onEndAnimation() {
	}

	@Override
	public Force3D getGravity() {
		// íeÇÕóéâ∫ÇµÇ»Ç¢ÇÃÇ≈èdóÕÇÉ[ÉçÇ∆Ç∑ÇÈ
		return Force3D.ZERO;
	}
	
	public boolean isAlive() {
		return bAlive;
	}
}
