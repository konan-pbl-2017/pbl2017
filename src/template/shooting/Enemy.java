package template.shooting;

import java.util.LinkedList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import framework.AI.AStar;
import framework.AI.GeometryGraph;
import framework.AI.Location;
import framework.AI.Plan;
import framework.animation.Animation3D;
import framework.gameMain.OvergroundActor;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.physics.Ground;
import framework.physics.Velocity3D;

/**
 * �G
 * @author �V�c����
 *
 */
public class Enemy extends OvergroundActor {
	private AStar aStar = new AStar();
	private Plan plan = null;
	
	// �萔
	public static final double ENEMY_SPEED = 3.0;
	public static final double BULLET_SPEED = 8.0;
	
	public Enemy(Object3D body, Animation3D animation) {
		super(body, animation);
	}
	
	/**
	 * �P�ʎ��Ԃ��Ƃ̓���i�Փ˔��菈�����s���j
	 * @param interval --- �O��Ăяo���ꂽ�Ƃ�����̌o�ߎ��ԁi�~���b�P�ʁj
	 * @param ground --- �n�ʁi�\�����j
	 * @param player --- �v���C���[�̈ʒu���l�����Ĉړ��v��𗧂Ă�̂ŕK�v
	 * @param geometryGraph --- �O���t�ɂ���ĒP���������n��
	 */
	public void motion(long interval, Ground ground, Player player, GeometryGraph geometryGraph) {
		if (!doPlan(plan) || Math.random() < 0.005) {
			// �v�悪�������邩�A�C�܂���ŐV�����v��𗧂Ă�
			plan = planning(player, geometryGraph);
			startPlan(plan);
		}
		super.motion(interval, ground);
	}
	
	private Plan planning(Player player, GeometryGraph geometryGraph) {
		Location startLoc;
		Location goalLoc;
		startLoc = geometryGraph.getNearestLocation(getPosition());
		goalLoc = geometryGraph.getNearestLocation(player.getPosition());
		Plan aStarPlan = aStar.getPath(startLoc, goalLoc);
		if (aStarPlan != null) {
			return aStarPlan;	// A�X�^�[�A���S���Y�����g�����v��
		}
		startLoc = new Location(new Point3d(getPosition().getVector3d()));
		goalLoc = new Location(new Point3d(player.getPosition().getVector3d()));
		return new Plan(startLoc, goalLoc);			// �X�^�[�g�ƃS�[���𒼐��Ō��Ԍv��
	}
	
	private void startPlan(Plan plan) {
		if (plan == null) return;		// ���������܂������v��𗧂ĂĂ��Ȃ�
		Location nextLoc = plan.getNextLocation();
		if (nextLoc == null) return;	// �v��̃S�[���ɒH�蒅����
		// �v���̎��̒ʉߓ_�ڎw���Ĉړ����x�Ƒ̂̌�����ύX
		Vector3d direction = new Vector3d(nextLoc.getCenter());
		if (direction.length() > 0.0) {
			direction.sub(getPosition().getVector3d());
			direction.normalize();
			direction.scale(ENEMY_SPEED);
			setVelocity(new Velocity3D(direction));		// �ړ����x�̐ݒ�
			direction.setY(0.0);
			direction.normalize();
			setDirection(direction);	// �̂̌����̐ݒ�
		}
	}
	
	private boolean doPlan(Plan plan) {
		if (plan == null) return false;		// ���������܂������v��𗧂ĂĂ��Ȃ�
		if (plan.updateCurrentLocation(getPosition())) {
			Location loc = plan.getCurrentLocation();
			if (loc == null) return false;	// �v��̃S�[���ɒH�蒅����
			// �v���̎��̒ʉߓ_�ڎw���Ĉړ����x�Ƒ̂̌�����ύX
			Vector3d direction = new Vector3d(loc.getCenter());
			if (direction.length() > 0.0) {
				direction.sub(getPosition().getVector3d());
				direction.normalize();
				direction.scale(ENEMY_SPEED);
				setVelocity(new Velocity3D(direction));		// �ړ����x�̐ݒ�
				direction.setY(0.0);
				direction.normalize();
				setDirection(direction);	// �̂̌����̐ݒ�
			}
		}
		return true;
	}

	public EnemiesBullet shoot(Player player, EnemiesBulletModel enemiesBulletModel) {
		EnemiesBullet enemiesBullet = enemiesBulletModel.createEnemiesBullet();
		// �v���C���[���߂����đł�
		Vector3d bulletVelocity = player.getPosition().sub(this.getPosition()).getVector3d();
		bulletVelocity.normalize();
		bulletVelocity.scale(BULLET_SPEED);
		enemiesBullet.setVelocity(new Velocity3D(bulletVelocity));
		enemiesBullet.setPosition(this.getPosition().add(-0.5, 0.0, 0.0));
		return enemiesBullet;
	}
}
