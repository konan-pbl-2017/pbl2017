package framework.game2D;

import javax.media.j3d.TransformGroup;

import framework.model3D.BaseObject3D;
import framework.model3D.Object3D;
import framework.model3D.Placeable;
import framework.model3D.Position3D;

public class Object2D implements Placeable {
	Object3D obj3D;

	public TransformGroup getTransformGroupToPlace() {
		return obj3D.getTransformGroupToPlace();
	}

	public BaseObject3D getBody() {
		return obj3D.getBody();
	}
	
	public Position2D getPosition2D() {
		return new Position2D(obj3D.getPosition3D().clone().getX(), obj3D.getPosition3D().clone().getY());
	}
}
