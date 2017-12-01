package framework.gameMain;
import framework.physics.Force3D;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;


public class ModeFreeFall extends Mode {	
	Force3D getForce(Solid3D body) {		
		return PhysicsUtility.getGravity(body);
	}
}
