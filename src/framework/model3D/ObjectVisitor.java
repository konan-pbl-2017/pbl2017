package framework.model3D;

import java.util.ArrayList;

import javax.media.j3d.Transform3D;

public abstract class ObjectVisitor {
	protected ArrayList<Transform3D> stackList = new ArrayList<Transform3D>();
	
	public abstract void preVisit(Object3D obj);
	public abstract void postVisit(Object3D obj);
	
	protected void pushTransform(Object3D obj) {
		Transform3D transPos = new Transform3D();
		obj.pos.getTransform(transPos);
		stackList.add(transPos);
		Transform3D transRot = new Transform3D();
		obj.rot.getTransform(transRot);
		stackList.add(transRot);
		Transform3D transScale = new Transform3D();
		obj.scale.getTransform(transScale);
		stackList.add(transScale);
		Transform3D transCenter = new Transform3D();
		obj.center.getTransform(transCenter);
		stackList.add(transCenter);
	}
	
	protected void popTransform() {
		for (int i = 0; i < 4; i++) {
			stackList.remove(stackList.size() - 1);
		}
	}

}
