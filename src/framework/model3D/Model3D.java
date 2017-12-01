package framework.model3D;



public abstract class Model3D {
	String name;
	public abstract Object3D createObject();
	public abstract Object3D createObjectSharingAppearance();
}
