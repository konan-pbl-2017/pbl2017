package fight3D;

import framework.gameMain.Actor;
import framework.gameMain.ActorModel;
import framework.model3D.CollisionResult;
import framework.physics.Force3D;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;


public class Item extends Actor {
		boolean fCalculatePhysics;
	
	public Item(ActorModel m) {
		super(m);
	}

	boolean doesCalculatePhysics(){		
		return fCalculatePhysics;
	}
	
	
	public void onIntersect(){
	}
	
	public void onEndAnimation() {
	}
	
	public void onEndFall(){
	}

	@Override
	public void onIntersect(CollisionResult cr,long interval) {
		// TODO Auto-generated method stub
		Force3D f = PhysicsUtility.calcForce(interval, cr.normal, (Solid3D)body);
		((Solid3D)body).move(interval, f, cr.collisionPoint);
	}
}
