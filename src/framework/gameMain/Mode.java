package framework.gameMain;
import framework.physics.Force3D;
import framework.physics.Solid3D;


public abstract class Mode {
	
	abstract Force3D getForce(Solid3D body);

}
