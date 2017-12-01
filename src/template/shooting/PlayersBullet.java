package template.shooting;

import framework.animation.Animation3D;
import framework.gameMain.Actor;
import framework.model3D.CollisionResult;
import framework.model3D.Object3D;
import framework.physics.Force3D;
import framework.physics.Ground;

/**
 * プレイヤーの弾
 * @author 新田直也
 *
 */
public class PlayersBullet extends Actor {
	private boolean bAlive = true;
	private long left = 0;
	
	// 定数
	public static final long LIFE_TIME = 3000L;		// 弾の寿命
	
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
		// 弾は落下しないので重力をゼロとする
		return Force3D.ZERO;
	}
	
	public boolean isAlive() {
		return bAlive;
	}
}
