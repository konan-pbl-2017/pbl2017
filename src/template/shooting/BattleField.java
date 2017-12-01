package template.shooting;

import java.awt.Container;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import framework.AI.GeometryGraph;
import framework.AI.IState;
import framework.AI.Location;
import framework.model3D.BaseObject3D;
import framework.model3D.CollisionResult;
import framework.model3D.GeometryCollector;
import framework.model3D.Object3D;
import framework.model3D.Placeable;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Velocity3D;

/**
 * �v���C���[�ƓG���������
 * @author �V�c����
 *
 */
public class BattleField {
	private Universe universe;
	private Ground stage;
	private Player player;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<PlayersBullet> playersBullets = new ArrayList<PlayersBullet>();
	private ArrayList<EnemiesBullet> enemiesBullets = new ArrayList<EnemiesBullet>();
	private GeometryGraph geometryGraph = null;
	private PlayersBulletModel playersBulletModel;
	private EnemyModel enemyModel;
	private EnemiesBulletModel enemiesBulletModel;
	private boolean bPlayerHit = false;
	private int hitEnemyCount = 0;
	
	// �萔
	public static final int MAX_ENEMIES = 4;			// �G�̍ő吔

	public BattleField(Universe universe) {
		this.universe = universe;
		
		// �����̒e�̃��f�����쐬
		playersBulletModel = new PlayersBulletModel("data\\models\\bullet.3ds", null);
		
		// �G�̃��f�����쐬
		enemyModel = new EnemyModel("data\\Head4.wrl", "data\\pocha\\walk.wrl");
		
		// �G�̒e�̃��f�����쐬
		enemiesBulletModel = new EnemiesBulletModel("data\\pocha\\bubble.wrl", null);
	}
	
	/**
	 * �I�u�W�F�N�g��z�u����
	 * @param obj �z�u����I�u�W�F�N�g
	 */
	public void place(Node obj) {
		universe.place(obj);
	}

	/**
	 * �I�u�W�F�N�g��z�u����
	 * @param obj �z�u����I�u�W�F�N�g
	 */
	public void place(Placeable obj) {
		if (obj instanceof Player) {
			player = (Player)obj;
		} else if (obj instanceof Ground) {
			stage = (Ground)obj;
			GeometryCollector v = new GeometryCollector();
			BaseObject3D baseObj = stage.getBody();
			if (baseObj instanceof Object3D) {
				if (obj instanceof Stage) {
					baseObj = ((Object3D)baseObj).getPart(((Stage)obj).getTerrainPart());
				}
				((Object3D)baseObj).accept(v);
				geometryGraph = new GeometryGraph(v.getGeometry());
			}
		}
		universe.place(obj);
	}
	
	/**
	 * ��Ŏ�菜����悤�ɃI�u�W�F�N�g��z�u����
	 * @param obj �z�u����I�u�W�F�N�g
	 */
	public void placeDisplacable(Node obj) {
		universe.place(obj);
	}
	
	/**
	 * ��Ŏ�菜����悤�ɃI�u�W�F�N�g��z�u����
	 * @param obj �z�u����I�u�W�F�N�g
	 */
	public void placeDisplacable(Placeable obj) {
		universe.place(obj);
	}
	
	/**
	 * �����̒ǉ�
	 * @param light �ǉ��������
	 */
	public void placeLight(Light light) {
		universe.place(light);
	}
	
	/**
	 * ���ׂĂ̍\���v�f����Ăɓ�����
	 * @param interval �O��Ăяo���ꂽ�Ƃ�����̌o�ߎ��ԁi�~���b�j
	 */
	public void motion(long interval) {
		// �v���C���[�𓮂���
		player.motion(interval, stage);		
		
		// ���ׂĂ̓G�𓮂����Ɠ����ɁA�v���C���[�ƏՓ˔��肵�A�X�ɓG�̒e�𔭎˂���
		for (int n = 0; n < enemies.size(); n++) {
			Enemy enemy = enemies.get(n);				// �G����̎��o��
			enemy.motion(interval, stage, player, geometryGraph);		// �G�𓮂���
			
			// �G�ƃv���C���[�̏Փ˔���
			CollisionResult collisionResult = PhysicsUtility.checkCollision(
					enemy.body, null, 
					player.body, null);			
			if (collisionResult != null) {
				// �v���C���[�ɓ�������
				bPlayerHit = true;
			}
		}
		
		// ���ׂẴv���C���[�̒e�𓮂����Ɠ����ɁA�G�ƏՓ˔���
		for (int n = 0; n < playersBullets.size(); n++) {
			PlayersBullet bullet = playersBullets.get(n);	// �v���C���[�̒e������o��
			if (bullet.isAlive()) {
				bullet.motion(interval, stage);					// �v���C���[�̒e�𓮂���
				
				for (int m = 0; m < enemies.size(); m++) {
					Enemy enemy = enemies.get(m);				// �G����̎��o��
					// �v���C���[�̒e�ƓG�̏Փ˔���
					CollisionResult collisionResult = PhysicsUtility.checkCollision(
							bullet.body, null, 
							enemy.body, null);
					if (collisionResult != null) {
						// �G�ɓ�������
						removeEnemy(enemy);						// �G������
						removePlayersBullet(bullet);			// ���������e������
						n--;
						hitEnemyCount++;
						break;
					}
				}
			} else {
				removePlayersBullet(bullet);	// �e������
				n--;
			}
		}
		
		// ���ׂĂ̓G�̒e�𓮂����Ɠ����ɁA�v���C���[�ƏՓ˔���
		for (int n = 0; n < enemiesBullets.size(); n++) {
			EnemiesBullet bullet = enemiesBullets.get(n);	// �G�̒e������o��
			if (bullet.isAlive()) {			
				bullet.motion(interval, stage);					// �G�̒e�𓮂���
				
				// �G�̒e�ƃv���C���[�̏Փ˔���
				CollisionResult collisionResult = PhysicsUtility.checkCollision(
						bullet.body, null, 
						player.body, null);			
				if (collisionResult != null) {
					// �v���C���[�ɓ�������
					removeEnemiesBullet(bullet);				// ���������e������
					n--;
					bPlayerHit = true;
					break;
				}
			} else {
				// �e���ǂɓ��������肵�ď������ꍇ
				removeEnemiesBullet(bullet);
				n--;
			}
		}
	}	
	
	/**
	 * �v���C���[�̈ʒu���擾����
	 * @return �v���C���[�̈ʒu�x�N�g��
	 */
	public Position3D getPlayerPosition() {
		return player.getPosition();
	}
	
	/**
	 * �v���C���[�̈ʒu��ݒ肷��
	 * @param pos �v���C���[�̈ʒu�x�N�g��
	 */
	public void setPlayerPosition(Position3D pos) {
		player.setPosition(pos);
	}
	
	/**
	 * �v���C���[�����E�ɉ�]������
	 * @param ang ��]�p�x�i���W�A���A�����������j
	 */
	public void playerTurn(double ang) {
		player.rotY(ang);
	}
	
	/**
	 * �v���C���[�����݌����Ă�������ɕ�������
	 * @param vel �������x
	 */
	public void playerWalk(double vel) {
		Velocity3D curV = player.getVelocity();
		curV.setX(player.getDirection().getX() * vel);
		curV.setZ(player.getDirection().getZ() * vel);
		player.setVelocity(curV);		
	}
	
	/**
	 * �v���C���[��Î~������
	 */
	public void playerStop() {
		playerWalk(0.0);
	}
	
	/**
	 * �v���C���[���W�����v������
	 * @param vel �W�����v����ۂ̏����x
	 */
	public void playerJump(double vel) {
		Velocity3D curV = player.getVelocity();
		curV.setY(vel);
		player.setVelocity(curV);				
	}
	
	/**
	 * �v���C�����擾����
	 * @return �v���C��
	 */
	public Player getPlayer() {
		return player;
	}
		
	/**
	 * �v���C���������Ă���������擾����
	 * @return �v���C���[�������Ă�������x�N�g��
	 */
	public Vector3d getPlayerDirection() {
		return player.getDirection();
	}
	
	/**
	 * �v���C���[���w�肵�������Ɍ�������
	 * @param dir �v���C���[��������������x�N�g��
	 */
	public void setPlayerDirection(Vector3d dir) {
		player.setDirection(dir);
	}
	
	/**
	 * �v���C���[�̌��݂̈ړ����x���擾����
	 * @return �v���C���[�̈ړ����x�x�N�g��
	 */
	public Velocity3D getPlayerVelocity() {
		return player.getVelocity();
	}
	
	/**
	 * �v���C���[�̈ړ����x��ݒ肷��
	 * @param vel �v���C���[�ɐݒ肷��ړ����x�x�N�g��
	 */
	public void setPlayerVelocity(Velocity3D vel) {
		player.setVelocity(vel);
	}
	
	/**
	 * �v���C���[���n�ʂ̏�ɏ���Ă��邩�ǂ����𔻒肷��
	 * @return true --- �n�ʂ̏�ɏ���Ă���, false --- ���ɕ����Ă���
	 */
	public boolean isPlayerOnGround() {
		return player.isOnGround();
	}
	
	/**
	 * �v���C���[�ɒe����������
	 */
	public void playerShoots() {
		PlayersBullet bullet = playersBulletModel.createPlayersBullet();
		Vector3d bulletVelocity = (Vector3d)player.getDirection().clone();
		bulletVelocity.normalize();
		bulletVelocity.scale(10.0);
		bullet.setVelocity(new Velocity3D(bulletVelocity));
		bullet.setPosition(player.getPosition().add(0.0, 1.0, 0.0));
		addPlayersBullet(bullet);
	}
	
	/**
	 * �v���C���[���U�����󂯂����H
	 * @return true --- �U�����󂯂�, false --- �U�����󂯂Ă��Ȃ�
	 */
	public boolean isPlayerHit() {
		boolean hit = bPlayerHit;
		bPlayerHit = false;
		return hit;
	}
	
	/**
	 * �G�𔭐�������
	 * @return ���������G�̃I�u�W�F�N�g
	 */
	public Enemy createEnemy() {
		if (getEnemyCount() < MAX_ENEMIES) {
			Enemy newEnemy = enemyModel.createEnemy();
			ArrayList<IState> locations = geometryGraph.getStates();			
			Location startLoc;
			for (;;) {
				startLoc = (Location)locations.get((int)(Math.random() * locations.size()));
				if (startLoc.getNormal().dot(new Vector3d(0.0, 1.0, 0.0)) >= 0.95) break;
			}
			newEnemy.setPosition(new Position3D(new Vector3d(startLoc.getCenter())).add(0.0, 2.0, 0.0));
			addEnemy(newEnemy);
			return newEnemy;
		}
		return null;
	}
	
	/**
	 * �G�̑������擾����
	 * @return �G�̑���
	 */
	public int getEnemyCount() {
		return enemies.size();
	}
	
	/**
	 * �w�肵���ԍ��̓G���擾����
	 * @param n �G�̔ԍ�(0�`)
	 * @return �G�̃I�u�W�F�N�g
	 */
	public Enemy getEnemy(int n) {
		return enemies.get(n);
	}

	/**
	 * �G������
	 * @param enemy �����G
	 */
	public void removeEnemy(Enemy enemy) {
		enemies.remove(enemy);
		universe.displace(enemy);
	}
	
	/**
	 * �����őI�񂾓G�ɒe����������
	 */
	public void enemyShoots() {
		if (enemies.size() > 0) {
			Enemy enemy = enemies.get((int)(Math.random() * (double)enemies.size()));
			EnemiesBullet enemiesBullet = enemy.shoot(player, enemiesBulletModel);
			addEnemiesBullet(enemiesBullet);		
		}
	}
	
	/**
	 * �|�����G�̐����擾����
	 * @return �V���ɓ|�����G�̐�
	 */
	public int getHitEnemyCount() {
		int hit = hitEnemyCount;
		hitEnemyCount = 0;
		return hit;
	}
	
	/**
	 * �G��o�ꂳ����
	 * @param enemy�@�o�ꂷ��G
	 */
	private void addEnemy(Enemy enemy) {
		enemies.add(enemy);		
		universe.place(enemy);
	}
	
	/**
	 * �v���C���[�̒e�𔭎˂���
	 * @param playersBullet ���˂���v���C���[�̒e
	 */
	private void addPlayersBullet(PlayersBullet playersBullet) {
		playersBullets.add(playersBullet);
		universe.place(playersBullet);
	}
	
	/**
	 * �G�̒e�𔭎˂���
	 * @param enemy ���˂���G
	 */
	private void addEnemiesBullet(EnemiesBullet enemiesBullet) {
		enemiesBullets.add(enemiesBullet);		
		universe.place(enemiesBullet);
	}

	/**
	 * �v���C���[�̒e������
	 * @param playersBullet �����e
	 */
	private void removePlayersBullet(PlayersBullet playersBullet) {
		playersBullets.remove(playersBullet);
		universe.displace(playersBullet);
	}

	/**
	 * �G�̒e������
	 * @param enemiesBullet �����e
	 */
	private void removeEnemiesBullet(EnemiesBullet enemiesBullet) {
		enemiesBullets.remove(enemiesBullet);
		universe.displace(enemiesBullet);
	}
}
