package framework.model3D;


public abstract class Property3D{
	public abstract void applyTo(Object3D o);
	
	public abstract Property3D clone();
}
