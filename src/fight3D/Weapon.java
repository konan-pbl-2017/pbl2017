package fight3D;

import framework.gameMain.Actor;
import framework.model3D.CollisionResult;
import framework.model3D.Object3D;
import framework.physics.Force3D;
import framework.physics.Ground;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;


/**
 * 飛び道具、弾
 * @author 新田直也
 *
 */
public class Weapon extends Actor implements Attackable {
	private Player owner;
	private boolean fCalculatePhysics = false;
	boolean fActive = true;
	long lifetime;
	private long timeLag = 0;	// 攻撃が効き始めるまでの時間（発生時間）
	
	public Weapon(WeaponModel m, Player owner, long t) {
		super(m);
		this.owner = owner;
		lifetime = 10000;
		timeLag = t;
		disappear();
	}
	
	public void motion(long interval, Ground ground){
		
		if(doesCalculatePhysics()){
			lifetime = lifetime -interval;
			if(lifetime <= 0){
				disappear();
			}
		}
		super.motion(interval, ground);
	}
	
	public void appear(){
		fActive = true;
		setVisible();
	}
	
	public void disappear() {
		fActive = false;
		setInvisible();
	}

	//遠距離攻撃の直線運動をするのか落下運動をするのかを判定
	public boolean doesCalculatePhysics(){
		
		return fCalculatePhysics;
	}	
	
	public boolean isAlive(){
		return fActive;
	}
			
	//攻撃力をかえす
	public int getAP() {
		// TODO Auto-generated method stub
		return owner.character.getPower();
	}

	//2つの物体があたるときの３次元の形をおしえる
	public Object3D getBody() {
		// TODO Auto-generated method stub
		return body;
	}

	@Override
	public String getPartName() {
		// TODO Auto-generated method stub
		return null;
	}	

	//誰のWeaponがあたったのか判定する
	public Player getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}
	
	//Playerと独立した動きをするのかどうか判定（Weaponでは独立したうごきをする）
	public boolean isMovable() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void onEndAnimation() {
	}
	
	public void onEndFall(){
	}

	//Weaponが地面に当たったときにどういう動きをするか
	public void onIntersect(CollisionResult cr, long interval) {
		// TODO Auto-generated method stub
		if(doesCalculatePhysics()){
			Force3D f = PhysicsUtility.calcForce(interval, cr.normal, (Solid3D)body);
			((Solid3D)body).move(interval, f, cr.collisionPoint);
		}
		else{
			disappear();
		}
	}
	
	public Force3D getGravity() {
		if (doesCalculatePhysics()) {
			return super.getGravity();
		}
		return Force3D.ZERO;
	}
	
	private void setVisible(){
		animation.reset();
		body.scale(1.0);
	}

	private void setInvisible(){
		body.apply(owner.getPosition(), false);
		body.scale(0.01);
	}

	@Override
	public boolean isActivate() {
		return (animation.time >= timeLag);
	}
}
