package fight3D;
import javax.media.j3d.BranchGroup;

import framework.model3D.Object3D;


public interface Attackable {
	
	abstract public Object3D getBody();
	abstract public String getPartName();
	abstract public Player getOwner();
	abstract public int getAP();
	abstract public boolean isMovable();
	abstract public boolean isAlive();
	abstract public void appear();
	abstract public void disappear();
	abstract public boolean isActivate();
	
}
