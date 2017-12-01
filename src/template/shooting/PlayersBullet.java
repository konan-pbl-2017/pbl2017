package template.shooting;

import framework.animation.Animation3D;
import framework.gameMain.Actor;
import framework.model3D.CollisionResult;
import framework.model3D.Object3D;
import framework.physics.Force3D;
import framework.physics.Ground;

/**
 * �v���C���[�̒e
 * @author �V�c����
 *
 */
public class PlayersBullet extends Actor {
	private boolean bAlive = true;
	private long left = 0;
	
	// �萔
	public static final long LIFE_TIME = 3000L;		// �e�̎���
	
	public PlayersBullet(Object3D body, Animation3D animation) {
		super(body, null);
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
	}

	@Override
	public void onEndAnimation() {
	}

	@Override
	public Force3D getGravity() {
		// �e�͗������Ȃ��̂ŏd�͂��[���Ƃ���
		return Force3D.ZERO;
	}
	
	public boolean isAlive() {
		return bAlive;
	}
}
