package framework.gameMain;

import javax.vecmath.Vector3d;

import framework.physics.Force3D;
import framework.physics.PhysicsUtility;
import framework.physics.Solid3D;


public class ModeOnGround extends Mode {
	private Vector3d normal = PhysicsUtility.vertical;
	
	public Force3D getForce(Solid3D body) {
		return Force3D.ZERO;
	}

	public void setNormal(Vector3d normal) {
		this.normal = normal;
	}

	public Vector3d getNormal() {
		return normal;
	}	
}
